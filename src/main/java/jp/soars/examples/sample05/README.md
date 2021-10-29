### サンプル5：繰り返し動作（ルールの中でルールの発火条件を指定）

#### サンプルプログラムの実行

サンプル5のプログラムは，jp.soars.examples.sample05パッケージにある．実行方法は，以下の通りである．

    java jp.soars.examples.sample05.TMain


#### シナリオとシミュレーション条件

以下のシナリオを考える．
- 300体のエージェント(agent1〜agent300)が，10個のスポット(spot1からspot10)上をランダムに動き回る．
- エージェントは，初期化時に，ランダムに選択したスポットを，自分のホームとする．
- エージェントは，0時から8時まで，ホームに滞在する．
- エージェントは，8時から18時まで，15分おきにランダムに決定したスポットへ移動する．
- エージェントは，18時にホームに戻る．
- エージェントは，18時から24時までホームに滞在する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：1日目24:00
- 時間ステップ：15分

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
    /** 終了時刻 */
    private TTime fEndTime;

    /** ホームスポット */
    private String fHomeSpot;

    /** 出発地 */
    private String fSpot;

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** 次のルールを実行するまでの時間 */
    private TTime fTimeToNextRule;

    /** 2回目以降に繰り返し実行されるルール */
    private TRandomlyMovingRule fRepeatedRule;

    /** 次の実行時刻を計算するためのワークメモリ */
    private TTime fNextTime;

    /**
     * コンストラクタ
     * 
     * @param ruleName       このルールの名前
     * @param ownerRole      このルールを持つ役割
     * @param endTime        終了時刻
     * @param homeSpot       ホームスポット
     * @param spotList       ルールで移動する候補地
     * @param timeToNextRule 次のルールを実行するまでの時間
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, TTime endTime, String homeSpot,
            ArrayList<TSpot> spotList, TTime timeToNextRule) {
        super(ruleName, ownerRole);
        fEndTime = endTime;
        fHomeSpot = homeSpot;
        fSpotList = spotList;
        fSpot = homeSpot;
        fTimeToNextRule = timeToNextRule;
        fRepeatedRule = null;
        fNextTime = new TTime();
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, HashMap<String, TSpot> spotSet,
            HashMap<String, TAgent> agentSet, HashMap<String, Object> globalSharedVariables) {

        if (isAt(fSpot)) { // スポット条件が満たされたら
            if (currentTime.isEqualTo(fEndTime)) {// 終了時刻ならば
                moveTo(spotSet.get(fHomeSpot));// ホームスポットへ移動する
            } else {
                ICRandom rand = getOwnerRole().getRandom();
                String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
                // ランダムに目的地を選択する
                moveTo(spotSet.get(destination));
                // 目的地に移動する
                TRandomlyMovingRule r = this;
                // 自分が臨時実行ルールならば，次回実行するルールとして自分を使い回す．
                // 臨時実行ルールであればgetTime().isDailyTime()はfalseを返す
                if (getTime().isDailyTime()) {
                    if (fRepeatedRule == null) { // 次に実行するルールが定義されていたら
                        fRepeatedRule = new TRandomlyMovingRule(getName(), getOwnerRole(), fEndTime, fHomeSpot,
                                fSpotList, fTimeToNextRule);
                    }
                    r = fRepeatedRule;
                }
                r.setSpot(destination);// 現在の命令の目的地を次のルールの出発地にする
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                // 次回の発火時刻を設定
                r.setTimeAndStage(false, fNextTime, getStage()); // 臨時実行ルールとして予約
            }
        }
    }

    /**
     * 出発地を設定
     * 
     * @param spot 出発地
     */
    public void setSpot(String spot) {
        fSpot = spot;
    }
}
```

#### 役割の定義

エージェントの役割として，TAgentRoleクラスを作成する．TAgentRoleクラスは，コンストラクタでTRandomlyMovingRuleクラスのオブジェクトを以下の条件で生成しているだけである．

- 18:00にホームへ戻る．ホームは，TAgentRoleの引数として与えられるinitialSpotである．
- 毎日8:00，エージェント移動ステージに発火する．

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
    public TAgentRole(TAgent ownerAgent, TSpot initialSpot, ArrayList<TSpot> spotList) {
        super(ROLE_NAME, ownerAgent);
        // 親クラスのコンストラクタを呼び出す．
        TRandomlyMovingRule rule = new TRandomlyMovingRule(RANDOM, this, new TTime("18:00"), initialSpot.getName(),
                spotList, new TTime("0:15"));
        // 8時から18時までの間15分ごとに移動するルールを設定する
        registerRule(rule);
        getRule(RANDOM).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
    }
}
```

#### メインクラスの定義

メインクラスを定義する．メインクラスは，メインメソッドのみをもつ．

メインメソッド中の「スポットの初期化」では，スポットを10個生成している．最後に，エージェント役割TAgentRoleに渡す必要がある全スポット名のリストspotListを取得している．

メインメソッド中の「エージェントの初期化」では，300体のエージェントを生成している．

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
    private static void createSpots(TSpotManager spotManager, int noOfSpots) {
        spotManager.createSpots(TSpotTypes.SPOT, noOfSpots);
        // noOfSpots個のスポットを生成
    }

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample05";
        // 乱数生成
        ICRandom rand = new TCJava48BitLcg();
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 15; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        int noOfSpots = 10; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots);// スポットの初期化
        ArrayList<TSpot> spotList = spotManager.getSpots();

        // エージェントの初期化
        int noOfAgents = 300;// エージェント数
        ArrayList<TAgent> agents = agentManager.createAgents(TAgentTypes.AGENT, noOfAgents);
        // エージェント管理
        for (int i = 0; i < agents.size(); i++) {
            TAgent agent = agents.get(i);// i番目のエージェントを取り出す．
            TSpot initialSpot = spotList.get(rand.nextInt(spotList.size()));
            // 初期スポットをランダムに選ぶ．
            TAgentRole agentRole = new TAgentRole(agent, initialSpot, spotList);
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
                printWriter.print(a.getCurrentSpotName() + "\t"); //
                // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
        }
        printWriter.close();
    }
}
```