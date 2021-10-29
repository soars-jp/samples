# サンプル2：サンプル1の拡張（日を跨ぐ相対時刻指定）

サンプル2は，サンプル1を修正したものである．サンプル1では父親は，毎日，9時に出社して17時に帰宅していた．これに対して，このサンプルでは，9時に家にいれば出社して，32時間(1日と8時間)後に帰宅する．以下ではサンプル1との差分だけを説明する．

## サンプルプログラムの実行

サンプル2のプログラムは，jp.soars.examples.sample02パッケージにある．実行方法は，以下の通りである．

    java jp.soars.examples.sample02.TMain


## シナリオとシミュレーション条件

以下のシナリオを考える．
- 3人の父親(father1, father2, father3)は，それぞれ自宅(home1, home2, home3)を持つ．
- 3人の父親は，9:00に自宅から同じ会社(company)に移動する．
- 3人の父親は，出社して32時間後にそれぞれの自宅に移動する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：6日23:00
- 時間ステップ：1時間


## ルールと役割の定義

サンプル１との違いは，以下のとおりである．

- 移動ルール  
移動ルールは，コンストラクタで，次のルールの実行までの時間，次のルールの実行ステージ，次のルール名を指定した場合，出発地から目的地へ移動後に，次のルールの実行を予約する．

- 父親役割  
leave_homeルールは，自宅にいるならば，会社に移動し，32時間後のエージェント移動ステージにreturn_homeルールを実行するように予約する．この変更に伴い，父親役割のコンストラクタでは，return_homeの実行を予約していない．

移動ルールのソースをいかに示す．

`TRuleOfMoving.java`

```java
public class TRuleOfMoving extends TAgentRule {

    /** 出発地 */
    private String fSource;

    /** 目的地 */
    private String fDestination;

    /** 次のルールを実行するまでの時間 */
    private int fTimeToNextRule;

    /** 次のルールを実行するステージ */
    private String fStageOfNextRule;

    /** 次に実行するルール名 */
    private String fNextRule;

    /**
     * コンストラクタ．
     * 
     * @param ruleName        このルールの名前
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     */
    public TRuleOfMoving(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fTimeToNextRule = -1;
        fStageOfNextRule = null;
        fNextRule = null;
    }

    /**
     * コンストラクタ．
     * 
     * @param ruleName        このルールの名前
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     * @param timeToNextRule  次のルールを実行するまでの時間
     * @param stageOfNextRule 次のルールを実行するステージ
     * @param nextRule        次に実行するルール
     */
    public TRuleOfMoving(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot,
            int timeToNextRule, String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fTimeToNextRule = timeToNextRule;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // 出発地にいたら，
            moveTo(spotManager.getSpotDB().get(fDestination)); // 目的地へ移動する．
            if (fNextRule != null) { // 次に実行するルールが定義されていたら
                int day = currentTime.getDay(); // 次のルールを実行する日
                int hour = currentTime.getHour() + fTimeToNextRule; // 次のルールを実行する時間
                int minute = currentTime.getMinute(); // 次のルールを実行する分
                getRule(fNextRule).setTimeAndStage(day, hour, minute, fStageOfNextRule); // 臨時実行ルールとして予約
            }
        }
    }
}
```

父親役割のソースを以下に示す．

`TFatherRole.java`

```java
/**
 * 父親役割． 9時に会社に出社して，その32時間後に帰宅する．
 */
public class TFatherRole extends TRole {

    /** 役割名 */
    public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     */
    public TFatherRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        // 自宅にいるならば，会社に移動し，32時間後のエージェント移動ステージにreturn_homeルールを実行するように予約する．
        registerRule(
                new TRuleOfMoving(LEAVE_HOME, this, home, TSpotTypes.COMPANY, 32, TStages.AGENT_MOVING, RETURN_HOME));
        // 会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.COMPANY, home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
    }
}
```
