### サンプル11：レイヤの指定

#### サンプルプログラムの実行

サンプル11のプログラムは，jp.soars.examples.sample11パッケージにある．実行方法は，以下の通りである．

    java jp.soars.examples.sample11.TMain


#### シナリオとシミュレーション条件

以下のシナリオを考える．
- 2体のエージェント(agent1〜agent2)が，1体ずつの2つのレイヤにそれぞれ配置し，3個のスポット(spot1からspot3)上をランダムに動き回る．
- エージェントは，8時から18時まで，10分おきにランダムに決定したスポットへ移動する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：1日目24:00
- 時間ステップ：10分

#### スポットタイプの定義

スポットタイプとして"spot"を定義する．

`TSpotTypes.java`

```java
public class TSpotTypes {
    /** スポット */
    public static final String SPOT = "spot";
}
```

#### エージェントタイプの定義

エージェントタイプとして"agent"を定義する．

`TAgentTypes.java`

```java
public class TAgentTypes {
    public static final String AGENT = "agent";
}
```

#### ステージの定義

ステージとして，エージェント移動ステージ"AgentMoving"を定義する．

`TStages.java`

```java
public class TStages {
    /** エージェント移動ステージ */
    public static final String AGENT_MOVING = "AgentMoving";
}
```

### ルールの定義

終了時刻まで指定された時間間隔でランダムに移動を繰り返し，終了時刻に初期スポットに移動するルールとして，TRandomlyMovingRuleクラスを定義する．TRandomlyMovingRuleクラスのソースを以下に示す．

TRandomlyMovingRuleクラスは，doItメソッドでエージェントの移動と次のルールの予約を行っている．
次のルールの予約では，自分が定時実行ルールならば次回実行するルールを新たに生成して，臨時実行ルールとして発火時刻，発火ステージ，発火スポットを設定している．
自分が臨時実行ルールならば，臨時実行ルールとして自分自身に発火時刻，発火ステージ，発火スポットの条件を設定して，自分自身を再利用することにより，無駄なオブジェクト生成を抑えている．

`TRandomlyMovingRule.java`

```java
public class TRandomlyMovingRule extends TAgentRule {

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** レイヤー名 */
    private String fLayerName;

    /**
     * コンストラクタ
     *
     * @param ruleName       このルールの名前
     * @param ownerRole      このルールを持つ役割
     * @param spotList       ルールで移動する候補地
     * @param layerName      このルールで指定するレイヤー名
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, ArrayList<TSpot> spotList, String layerName) {
        super(ruleName, ownerRole);
        fSpotList = spotList;
        fLayerName = layerName;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        ICRandom rand = getOwnerRole().getRandom();
        String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
        // ランダムに目的地を選択する
        moveTo(spotManager.getSpotDB(fLayerName).get(destination));
    }

}
```

#### 役割の定義

エージェントの役割として，TAgentRoleクラスを作成する．TAgentRoleクラスは，コンストラクタでTRandomlyMovingRuleクラスのオブジェクトを以下の条件で生成しているだけである．

- 各レイヤ内のスポットにランダムで移動する．

エージェント役割クラスのソースを以下に示す．

`TAgentRole.java`

```java
public class TAgentRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "AgentRole";

    /** ランダムに移動する */
    public static final String RANDOM = "RandomlyMoving";

    /**
     * コンストラクタ
     *
     * @param ownerAgent  この役割を持つエージェント
     * @param initialSpot 初期スポット
     * @param spotList    全スポットのリスト
     */
    public TAgentRole(TAgent ownerAgent, TSpot initialSpot, ArrayList<TSpot> spotList, String layerName) {
        super(ROLE_NAME, ownerAgent);
        // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRandomlyMovingRule(RANDOM, this, spotList, layerName));
        getRule(RANDOM).setStage(TStages.AGENT_MOVING);
    }
}
```

#### メインクラスの定義

メインクラスを定義する．メインクラスは，メインメソッドのみをもつ．

メインメソッド中の「スポットの初期化」では，スポットを3個生成している．最後に，エージェント役割TAgentRoleに渡す必要がある各レイヤの全スポット名のリストspotListを取得している．

メインメソッド中の「エージェントの初期化」では，2体のエージェントを生成している．

その他の詳細はソースコードを参照されたい．メインクラスのソースコードを以下に示す．

`TMain.java`

```java
public class TMain {

    /**
     * スポットを生成する
     *
     * @param spotManager スポット管理
     * @param noOfHomes   スポット数
     */
    private static void createSpots(TSpotManager spotManager, int noOfSpots, int noOfSpotLayers) {
        for(int i=0;i<noOfSpotLayers;i++){
            String layerName = String.valueOf(i);
            spotManager.createSpots(TSpotTypes.SPOT, noOfSpots, layerName);
            // noOfSpots個のスポットを生成
        }
    }

    /**
     * メインメソッド．
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample11";
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 10; // １ステップの分数
        long seed = 0; // 乱数シード
        int noOfSpotLayers = 2; // レイヤー数
        TModel model = new TModel(stages, interval, seed, noOfSpotLayers);
        model.getRuleAggregator().makeStageAlwaysExecuted(TStages.AGENT_MOVING);
        int noOfSpots = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots, noOfSpotLayers);// スポットの初期化

        // エージェントの初期化
        int noOfAgents = 2;// エージェント数
        ArrayList<TAgent> agents = agentManager.createAgents(TAgentTypes.AGENT, noOfAgents);
        // エージェント管理
        for (int i = 0; i < agents.size(); i++) {
            String layerName = String.valueOf(i);
            ArrayList<TSpot> spotList = spotManager.getSpotLayers(layerName);
            TAgent agent = agents.get(i);// i番目のエージェントを取り出す．
            TSpot initialSpot = spotList.get(model.getRandom().nextInt(spotList.size()));
            // 初期スポットをランダムに選ぶ．
            TAgentRole agentRole = new TAgentRole(agent, initialSpot, spotList, layerName);
            // エージェント役割を生成する．
            agent.addRole(agentRole);// エージェント役割を設定する．
            agent.activateRole(agentRole.getName());// エージェント役割を有効にする．
            agent.initializeCurrentSpot(initialSpot); // 初期位置を設定する．
        }
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("2/0:00"); // シミュレーション終了時刻
        PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spot.csv");
        while (model.getTime().isLessThan(simulationPeriod)) {
            printWriter.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                printWriter.print(a.getCurrentSpotName() + ","); //
                // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
        }
        printWriter.close();
    }
}
```