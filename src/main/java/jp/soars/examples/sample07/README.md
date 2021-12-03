### サンプル7：スポット・エージェントの動的追加・削除

スポット・エージェントの動的追加・削除を実装する際の注意点について
- 動的追加は定時実行ルールにすることができない．
- スポットはエージェントが存在する場合削除することができない．
- エージェントの移動と同じ時間・同じステージで実行することはできず，必ずステージを分けなければならない．

#### サンプルプログラムの実行

サンプル6のプログラムは，jp.soars.examples.sample06パッケージにある．実行方法は，以下の通りである．

    java jp.soars.examples.sample07.TMain


#### シナリオとシミュレーション条件

以下のシナリオを考える．
- Creatorエージェント(３人)は毎日８時にダミースポットとダミーエージェントを１つずつ追加する．
- Killerエージェント(３人)は毎日１６時にダミースポットとダミーエージェントをランダムに１つずつ削除する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：2日目24:00
- 時間ステップ：60分

#### スポットタイプの定義

スポットタイプとしてエージェントのホーム"home"とダミースポット"dummySpot"を定義する．

`TSpotTypes.java`

```java
public class TSpotTypes {
    //ホーム
    public static final String HOME = "home";
    //作成・削除されるダミースポット
    public static final String DUMMY_SPOT = "dummySpot";
}

```

#### エージェントタイプの定義

エージェントタイプとしてスポット・エージェントを動的追加する"creator"，スポット・エージェントを動的削除する"killer"，ダミーエージェントとして"dummyAgent"を定義する．

`TAgentTypes.java`

```java
public class TAgentTypes {
    //スポット・エージェントを作成するエージェント
    public static final String CREATOR = "creator";
    //スポット・エージェントを削除するエージェント
    public static final String KILLER = "killer";
    //作成・削除されるダミーエージェント
    public static final String DUMMY_AGENT = "dummyAgent";
}

```

#### ステージの定義

ステージとして，スポット・エージェントの動的追加ステージ"DynamicAddition"，スポット・エージェントの動的削除ステージ"DynamicRemoval"を定義する．

`TStages.java`

```java
public class TStages {
    /** スポット・エージェント動的追加ステージ */
    public static final String DYNAMIC_ADDITION = "DynamicAddition";
    /** スポット・エージェント動的削除ステージ */
    public static final String DYNAMIC_REMOVAL = "DynamicRemoval";
}

```

### ルールの定義

ダミースポットとダミーエージェントを１つずつ追加するルールとしてTCreatorRuleクラスを定義する．
動的追加は定時実行ルールとして定義することができないので，発火するたびに次の発火時刻を24時間後に設定することで疑似的に定時実行させる．
また，globalSharedVariablesからgetしているリストは主に動的削除で利用するためそちらで解説する．

TCreatorRuleクラスのソースコードを以下に示す．

`TCreatorRule.java`

```java
public class TCreatorRule extends TAgentRule {
    /** ルール名 */
	public static String RULE_NAME = "CreatorRule";

    /** デバッグ情報として作成したエージェント名とスポット名を出力する */
    private String fSpotName = "";
    private String fAgentName = "";

    public TCreatorRule(TRole ownerRole) {
        super(RULE_NAME, ownerRole);
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, 
                     TAgentManager agentManager, HashMap<String, Object> globalSharedVariables){
        //新たなスポットとエージェントの作成
        TSpot newSpot = spotManager.createSpots(TSpotTypes.DUMMY_SPOT, 1).get(0);
        TAgent newAgent = agentManager.createAgents(TAgentTypes.DUMMY_AGENT, 1).get(0);
        TSpot newHome = spotManager.createSpots(TSpotTypes.HOME, 1).get(0);
        newAgent.initializeCurrentSpot(newHome);
        fSpotName = newSpot.getName();
        fAgentName = newAgent.getName();
        //スポット・エージェントの作成は定時実行できないので，次に発火する時刻を設定
        TTime nextTime = new TTime(currentTime).add("24:00"); //24時間後に実行されるように設定
        this.setTimeAndStage(false, nextTime, getStage());
    }

    @Override
    public String debugInfo(){
        return "spot:" + fSpotName + " agent:" + fAgentName;
    }
}
```

ダミースポットとダミーエージェントを１つずつランダムに削除するルールとしてTKillerRuleクラスを定義する．
globalSharedVariablesには現在存在するダミースポットのみが入っている"dummySpotList"と現在存在するダミーエージェントのみが入っている"dummyAgentList"があり，そこからランダムに１つずつ選んで削除する．

同じ時間，同じステージに複数のkillerが同じダミーを削除しようとした場合，warningが発生するが問題なく実行できる．

`TKillerRule.java`

```java
public class TKillerRule extends TAgentRule {

    /** ルール名 */
	public static String RULE_NAME = "KillerRule";

    /** デバッグ情報として削除(しようとした)エージェント名とスポット名を出力する */
    private String fSpotName = "";
    private String fAgentName = "";

    public TKillerRule(TRole ownerRole) {
        super(RULE_NAME, ownerRole);
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, 
                     TAgentManager agentManager, HashMap<String, Object> globalSharedVariables){
        ICRandom rand = getOwnerRole().getRandom();
        //ダミースポットをランダムに１つ削除
        List<TSpot> dummySpotList = spotManager.getSpots(TSpotTypes.DUMMY_SPOT);
        TSpot spot = dummySpotList.get(rand.nextInt(dummySpotList.size()));
        fSpotName = spot.getName();
        if(spot.getAgents().isEmpty()){ //エージェントがいるスポットを消そうとするとエラー
            spotManager.deleteSpot(spot); 
        }
        //ダミーエージェントをランダムに１つ削除
        List<TAgent> dummyAgentList = agentManager.getAgents(TAgentTypes.DUMMY_AGENT);
        TAgent agent = dummyAgentList.get(rand.nextInt(dummyAgentList.size()));
        agentManager.deleteAgent(agent);
        fAgentName = agent.getName();
    }

    @Override
    public String debugInfo(){
        return "spot:" + fSpotName + " agent:" + fAgentName;
    }
}
```