# サンプル10：ステージ実行ルールの指定

## シナリオとシミュレーション条件

以下のシナリオを考える．

- 1000人の父親は，それぞれ自宅を持つ．
- 1000人の父親は，9:00に自宅からそれぞれの会社に移動する．
- 1000人の父親は，17:00にそれぞれの自宅に移動する．
- 毎時刻，父親がいる場所がグローバル共有変数に集計される．グローバル共有変数には「その時刻までにすべてのエージェントが自宅または職場にいた時間の総計」が記録されることになる．

シミュレーション条件は以下の通りである．

- 時刻ステップ間隔：1時間
- シミュレーション期間：7日間

## ステージの定義

sample10では以下のステージを定義する．

- AgentMoving：エージェント移動ステージ
- Aggregation：集計ステージ

`TStages.java`

```java
public class TStages {
    /** エージェント移動ステージ */
    public static final String AGENT_MOVING = "AgentMoving";
    /** 集計ステージ */
    public static final String AGGREGATION = "aggregation";
}
```

## エージェントタイプの定義

sample10では以下のエージェントタイプを定義する．

- Father：父親

`TAgentTypes.java`

```java
public class TAgentTypes {
    /** 父親エージェント */
    public static final String FATHER = "father";
}
```

## スポットタイプの定義

sample10では以下のスポットタイプを定義する．

- Home：自宅
- Company：会社

`TSpotTypes.java`

```java
public class TSpotTypes {
    /** 自宅 */
    public static final String HOME = "home";
    /** 会社 */
    public static final String COMPANY = "company";
}
```

## ルールの定義

sample10では以下のルールを定義する．

- TRuleOfAgentMoving：エージェント移動ルール
- TRuleOfAggregation：集計ルール

`TRuleOfAgentMoving.java`

```java
public final class TRuleOfAgentMoving extends TAgentRule {

    /** 出発地 */
    private final String fSource;

    /** 目的地 */
    private final String fDestination;

    /**
     * コンストラクタ
     * @param name ルール名
     * @param owner このルールをもつ役割
     * @param source 出発地
     * @param destination 目的地
     */
    public TRuleOfAgentMoving(String name, TRole owner, String source, String destination) {
        super(name, owner);
        fSource = source;
        fDestination = destination;
    }

    /**
     * ルールを実行する．
     * @param currentTime 現在時刻
     * @param currentStage 現在ステージ
     * @param spotManager スポット管理
     * @param agentManager エージェント管理
     * @param globalSharedVariables グローバル共有変数集合
     */
    @Override
    public final void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, 
            TAgentManager agentManager, Map<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // 出発地にいるなら
            TSpot spot = spotManager.getSpotDB().get(fDestination); // スポット管理から目的地のスポットを取得．
            moveTo(spot); // 目的地へ移動する．
        }
    }
}
```

集計ルールは現在いる場所に応じてグローバル共有変数を更新する．

`TRuleOfAggregation.java`

```java
public class TRuleOfAggregation extends TAgentRule {

    /** 自宅 */
    private String fHome;
    /** 職場 */
    private String fWorkplace;

    /** グローバル共有変数で集計するキー */
    public static final String HOME_KEY = "home_key";
    public static final String WORKPLACE_KEY = "workplace_key";

    /**
     * コンストラクタ
     *
     * @param ruleName  このルールの名前
     * @param ownerRole このルールをもつ役割
     * @param home      自宅
     * @param workplace 職場
     */
    public TRuleOfAggregation(String ruleName, TRole ownerRole, String home, String workplace) {
        super(ruleName, ownerRole);
        fHome = home;
        fWorkplace = workplace;
    }

    /**
     * 現在いる場所に応じてグローバル共有変数を更新する．
     * ConcurrentHashMapのcomputeメソッドはatomicであることが保証されており，並列化に対応．
     */
    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHome)) {
            globalSharedVariables.compute(HOME_KEY, (k, v) -> v = (int) v + 1);
        } else if (isAt(fWorkplace)) {
            globalSharedVariables.compute(WORKPLACE_KEY, (k, v) -> v = (int) v + 1);
        }
    }
}
```

## 役割の定義

sample10では以下の役割を定義する．

- TRoleOfFather：父親役割

父親役割で，集計ルールはsetStageメソッドによってステージ実行ルールとして予約する．
ステージ実行ルールとして登録するためには，メインクラスでステージを定期実行ステージとして登録しておく必要がある．

`TFatherRole.java`

```java
public class TFatherRole extends TRole {

    /** 役割名 */
    public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /** 集計 */
    public static final String AGGREGATION = "aggregation";

    /**
     * コンストラクタ
     *
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     * @param workplace  職場
     */
    public TFatherRole(TAgent ownerAgent, String home, String workplace) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        // 自宅にいるならば，会社に移動する．
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, workplace));
        // 会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, workplace, home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
        // 毎日17時，エージェントステージにRETURN_HOMEルールが発火するように予約する．
        getRule(RETURN_HOME).setTimeAndStage(17, 0, TStages.AGENT_MOVING);
        // 集計ルールの登録
        registerRule(new TRuleOfAggregation(AGGREGATION, this, home, workplace));
        // 集計ルールを毎時刻実行されるように予約する．
        getRule(AGGREGATION).setStage(TStages.AGGREGATION);
    }

}
```

## メインクラスの定義

変更点は以下の通り．

- グローバル共有変数に初期値を設定する．
- 集計ステージを定期実行ステージとして登録する．
  - 定期実行ステージとして登録するには，ルール収集器のmakeStageAlwaysExecutedメソッドを利用する．
- 集計ステージを並列化ステージとして登録する．
- グローバル共有変数のログ出力．

`TMain.java`

```java
public class TMain {

        public static void main(String[] args) throws IOException {
                // ログディレクトリへのパス
                String logDir = "logs/sample10";
                // ステージの初期化
                List<String> stages = List.of(TStages.AGENT_MOVING, TStages.AGGREGATION); // ステージは，エージェント移動と集計．
                // 乱数発生器
                int interval = 60; // １ステップの分数
                long seed = 0; // シード値
                TModel model = new TModel(stages, interval, seed);
                // 集計ステージを定期実行ステージとして登録
                model.getRuleAggregator().makeStageAlwaysExecuted(TStages.AGGREGATION);
                model.beginRuleLogger(logDir + File.separator + "ruleLog.csv");// ルールログを開始する．
                // 初期値設定
                model.getGlobalSharedVariableSet().put(TRuleOfAggregation.HOME_KEY, 0);
                model.getGlobalSharedVariableSet().put(TRuleOfAggregation.WORKPLACE_KEY, 0);
                // スポットの初期化
                int noOfHomes = 1000; // 家の数
                int noOfCompany = 10; // 会社の数
                TSpotManager spotManager = model.getSpotManager(); // スポット管理
                spotManager.createSpots(TSpotTypes.HOME, noOfHomes); // noOfHomes個の家スポットを生成する．名前は，home1, home2, //
                                                                     // home3となる．
                spotManager.createSpots(TSpotTypes.COMPANY, noOfCompany); // 10個の会社スポットを生成する，名前は，company1, company2,
                                                                          // company3 となる
                // エージェントの初期化
                TAgentManager agentManager = model.getAgentManager(); // エージェント管理
                ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); // noOfHomes体の父親エージェントを生成する．
                for (int i = 0; i < fathers.size(); ++i) {
                        TAgent father = fathers.get(i); // i番目のエージェントを取り出す．
                        String home = TSpotTypes.HOME + (i + 1); // i番目のエージェントの自宅のスポット名を生成する．
                        String workplace = TSpotTypes.COMPANY + ((i % noOfCompany) + 1);
                        TFatherRole fatherRole = new TFatherRole(father, home, workplace); // 父親役割を生成する．
                        father.activateRole(fatherRole.getName()); // 父親役割をアクティブ化する．
                        father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); // 初期位置を自宅に設定する．
                }

                // スポットログ用PrintWriter
                PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spotLog.csv");
                printWriter.print("CurrentTime");
                for (int i = 0; i < agentManager.getAgents().size(); i++) {
                        TAgent agent = agentManager.getAgents().get(i);
                        printWriter.print("," + agent.getName());
                }
                printWriter.println();

                // グローバル共有変数のログ用PrintWriter
                String pathOfGlobalSharedVariableSetLog = logDir + File.separator
                                + "globalSharedVariableSetLog.csv";
                PrintWriter globalSharedVariableSetLogPW = new PrintWriter(
                                new BufferedWriter(new FileWriter(pathOfGlobalSharedVariableSetLog)));
                globalSharedVariableSetLogPW.println("CurrentTime," +
                                TRuleOfAggregation.HOME_KEY + "," +
                                TRuleOfAggregation.WORKPLACE_KEY);

                // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
                TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
                while (model.getTime().isLessThan(simulationPeriod)) {
                        TTime currentTime = model.getTime();// 時刻を表示する．
                        printWriter.print(currentTime);
                        globalSharedVariableSetLogPW.print(currentTime);
                        model.execute();// モデルの実行
                        for (int i = 0; i < agentManager.getAgents().size(); i++) {
                                TAgent agent = agentManager.getAgents().get(i);
                                printWriter.print("," + agent.getCurrentSpotName());// 各エージェントが位置しているスポット名を表示する．
                        }
                        printWriter.println();
                        // グローバル共有変数のログ出力
                        globalSharedVariableSetLogPW.println("," +
                                        model.getGlobalSharedVariableSet().get(TRuleOfAggregation.HOME_KEY) + "," +
                                        model.getGlobalSharedVariableSet().get(TRuleOfAggregation.WORKPLACE_KEY));

                }
                printWriter.close();// スポットログを終了する．
                globalSharedVariableSetLogPW.close();
                model.endRuleLogger();// ルールログを終了する．
        }
}
```
