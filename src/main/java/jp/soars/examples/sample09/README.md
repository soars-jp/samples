### サンプル9：サンプル8の修正（列車への乗車の相対時刻指定）

サンプル9では，父親の数を5人に増やしたうえで，父親役割のルールを絶対時刻指定から相対時刻指定に修正する．
修正後のシナリオは以下のとおりである．

- 自宅から会社に移動するシナリオ
    - 父親は，6:55に父親ごとに設定された駅へ向けて自宅を出発する．
    - 父親は，5分後に父親ごとに設定された駅に到着する．
    - 父親は，line1線上り(inbound)方面の最初に来た列車に乗車する．
    - 父親は，駅(station8)で列車から降車する．
    - 父親は，3分後に会社(company)に向けて駅(station8)を出発する．
    - 父親は，10分後に会社に到着する．

- 会社から自宅に移動するシナリオ
    - 父親は，17:55に駅(station8)へ向けて会社を出発する．
    - 父親は，10分後に駅(station8)に到着する．
    - 父親は，line1線下り(outbound)方面の最初に来た列車に乗車する．
    - 父親は，父親ごとに設定された駅で列車から降車する．
    - 父親は，5分後に自宅に向けて父親ごとに設定された駅を出発する．
    - 父親は，5分後に自宅に到着する．

以下では，サンプル8との差分を説明する．

#### サンプルプログラムの実行

サンプルプログラムは以下で実行できる．

     java jp.soars.examples.sample09.TMain

実行すると，logs/sample09の下に以下のログファイルが生成される．生成されるログファイルは，sample08のものと同じである．

#### 役割の定義

サンプル8と同様に，「列車に乗る」ルールとしてtransportation.TGettingOnTransportationRuleクラス，「列車から降りる」ルールとしてtransportation.TGettingOffTransportationRuleクラスを利用して父親役割(TFatherRole)を定義する．ただし，駅に到着後，路線条件・方面条件・列車タイプ条件・行き先条件を満たす最初に来た列車に乗車する場合のコンストラクタを用いる．

TGettingOnTransportationRuleクラスのコンストラクタは以下のとおりである．
```java
TGettingOnTransportationRule(String ruleName, TRole ownerRole, String station, String line, String direction,Set<String> transportationTypes, Set<String>transportationDestinations, String stageOfNextRule,String nextRule)
```
ここで，引数のruleNameにはルール名，引数のownerRoleにはこのルールを持つ役割を指定する．
stationには乗車駅，lineには乗車路線，directionには乗車方面を指定する．
transportationTypesには，乗車したい列車タイプを指定する．列車タイプは，transportationDBディレクトリの各路線ディレクトリの下にあるtrains.csvの中のTypeに書かれているものを指定する．列車タイプには，複数のタイプを指定することができる．TGettingOnTransportationRule.ANY (="*")を指定すると全ての列車タイプとマッチする．
transportationDestinationsには，乗車したい列車の行き先を指定する．行き先は，transportationDBディレクトリの各路線ディレクトリの下にあるtrains.csvの中のTypeに書かれているものを指定する．行き先には，複数の行き先を指定することができる．TGettingOnTransportationRule.ANY (="*")を指定すると全ての行き先とマッチする．
stageOfNextRuleには，降車ルールを実行するステージを指定し，nextRuleには，降車ルールの名前を指定する．

TGettingOffTransportationRuleクラスのコンストラクタは以下のとおりである．
```java
TGettingOffTransportationRule(String ruleName, TRole ownerRole, String station, TTime timeToNextRule,String stageOfNextRule, String nextRule)
```
ここで，引数ruleNameにはルール名，引数のownerRoleには，このルールを持つ役割を指定する．
stationには降車駅を指定する．timeToNextRuleには次のルールを実行するインターバル，stageOfNextRuleには次に実行するルールを実行するステージ，nextRuleには次に実行するルールの名前を指定する．

エージェントが駅へ移動して電車を待つルールとして，TRuleOfMovingStationクラスを定義する．TRuleOfMovingStationクラスのソースを以下に示す．
TRuleOfMovingStationクラスは，doItメソッドでエージェントの移動と駅に乗車ルールの登録を行っている．
登録された乗車ルールは，条件を満たす列車が到着した際に同時刻のAgentMovingステージに登録される．


`TRuleOfMovingStation.java`

```java
/**
 * 移動ルール．
 */
public class TRuleOfMovingStation extends TAgentRule {

    /** 出発地 */
    private String fSource;

    /** 目的地 */
    private String fDestination;

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
    public TRuleOfMovingStation(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
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
    public TRuleOfMovingStation(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot,
            String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // 出発地にいたら，
            moveTo(spotManager.getSpotDB().get(fDestination)); // 目的地へ移動する．
            if (fNextRule != null) {
                ((TStation) spotManager.getSpotDB().get(fDestination)).addRule(getAgent().getName(),
                        (TGettingOnTransportationRule) getRule(fNextRule), fStageOfNextRule);
            }
        }
    }

}
```

父親役割のソースを以下に示す．コンストラクタから，自宅から会社に列車で移動するルール群を生成するためのmoveFromHomeToCompanyメソッド，会社から自宅に列車で移動するルール群を生成するためのmoveFromCompanyToHomeメソッドを呼び出している．詳細は，ソース中のコメントを参照されたい．
stageOfNextRuleには，次に実行するルールを実行するステージを指定し，nextRuleには，次に実行するルールの名前を指定する．

`TFatherRole.java`

```java
package jp.soars.examples.sample09;

import java.util.Set;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TTime;
import jp.soars.transportation.TGettingOffTransportationRule;
import jp.soars.transportation.TGettingOnTransportationRule;

/**
 * 父親役割
 */
public class TFatherRole extends TRole {

        /** 役割名 */
        public static final String ROLE_NAME = "FatherRole";

        /** 家を出発する */
        public static final String LEAVE_HOME = "leave_home";

        /** 家に到着する */
        public static final String REACH_HOME = "reach_home";

        /** 駅に到着する（出勤） */
        public static final String REACH_STATION = "reach_station";

        /** 駅に到着する（帰宅） */
        public static final String REACH_STATION_BACK = "reach_station_back";

        /** 電車に乗る（出勤） */
        public static final String GETON_TRANSPORTATION = "geton_transportation";

        /** 電車に乗る（帰宅） */
        public static final String GETON_TRANSPORTATION_BACK = "geton_transportation_back";

        /** 電車から降りる（出勤） */
        public static final String GETOFF_TRANSPORTATION = "getoff_transportation";

        /** 電車から降りる（帰宅） */
        public static final String GETOFF_TRANSPORTATION_BACK = "getoff_transportation_back";

        /** 駅から会社に向かう */
        public static final String GO_COMPANY = "go_company";

        /** 会社に到着する */
        public static final String REACH_COMPANY = "reach_company";

        /** 会社に出発する */
        public static final String LEAVE_COMPANY = "leave_company";

        /** 家に帰る */
        public static final String GO_HOME = "go_home";
        /** 自宅 */
        private String fHome;
        /** 使用する電車 */
        private String fLine;
        /** 乗車駅 */
        private String fSrcStation;
        /** 降車駅 */
        private String fDstStation;

        /**
         * コンストラクタ
         * 
         * @param ownerAgent この役割を持つエージェント
         * @param home       自宅
         * @param line       通勤に使用する電車
         * @param srcStation 乗車駅
         * @param dstStation 降車駅
         */
        public TFatherRole(TAgent ownerAgent, String home, String line, String srcStation, String dstStation) {
                super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
                fHome = home;
                fLine = line;
                fSrcStation = srcStation;
                fDstStation = dstStation;
                moveFromHomeToCompany();
                moveFromCompanyToHome();
        }

        private void moveFromHomeToCompany() {
                String direction = "inbound"; // 乗車する列車の方面
                Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
                Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
                // 6:55に自宅を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_HOME, this, fHome, TSpotTypes.MIDWAY_SPOT, new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, REACH_STATION));
                getRule(LEAVE_HOME).setTimeAndStage(6, 55, TStages.AGENT_MOVING);
                // 7:00に乗車駅に到着する
                registerRule(new TRuleOfMovingStation(REACH_STATION, this, TSpotTypes.MIDWAY_SPOT, fSrcStation,
                                TStages.AGENT_MOVING, GETON_TRANSPORTATION));
                // 最初に来た電車にのる
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION, this, fSrcStation, fLine, direction,
                                trainTypes,
                                trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION));
                // 電車から降りる
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION, this, fDstStation,
                                new TTime(0, 0, 3),
                                TStages.AGENT_MOVING, GO_COMPANY));
                // // 7:33に降車駅を出発して会社に向かう．
                registerRule(new TRuleOfMoving(GO_COMPANY, this, fDstStation, TSpotTypes.MIDWAY_SPOT,
                                new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_COMPANY));
                // // 7:43に会社に到着する
                registerRule(new TRuleOfMoving(REACH_COMPANY, this, TSpotTypes.MIDWAY_SPOT, TSpotTypes.COMPANY));
        }

        private void moveFromCompanyToHome() {
                String direction = "outbound"; // 乗車する列車の方面
                Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
                Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
                // 17:55に会社を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_COMPANY, this, TSpotTypes.COMPANY, TSpotTypes.MIDWAY_SPOT,
                                new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_STATION_BACK));
                getRule(LEAVE_COMPANY).setTimeAndStage(17, 55, TStages.AGENT_MOVING);
                // 18:05に乗車駅に到着する．
                registerRule(new TRuleOfMovingStation(REACH_STATION_BACK, this, TSpotTypes.MIDWAY_SPOT, fDstStation,
                                TStages.AGENT_MOVING, GETON_TRANSPORTATION_BACK));
                // 18:10に乗車駅で指定された列車に乗車する．
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION_BACK, this, fDstStation, fLine,
                                direction,
                                trainTypes, trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION_BACK));
                // 降車駅で列車から降車する．
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION_BACK, this, fSrcStation,
                                new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, GO_HOME));
                // 降車駅を出発して自宅に向かう．
                registerRule(new TRuleOfMoving(GO_HOME, this, fSrcStation, TSpotTypes.MIDWAY_SPOT, new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, REACH_HOME));
                // 自宅に到着する．
                registerRule(new TRuleOfMoving(REACH_HOME, this, TSpotTypes.MIDWAY_SPOT, fHome));
        }
}
```
#### メインクラスの定義

メインクラスは，mainメソッドの他に，スポットを生成するcreateSpotsメソッド，父親エージェントを生成するcreateFatherAgentsメソッドを持つ．

列車については，以下のようにtransportation.TTransportationManagerクラスのオブジェクトを生成すればよい．transportation.TTransportationManagerクラスのオブジェクトを生成すると，列車情報が読み込まれて，列車に関するルールがルール収集器に登録される．コンストラクタの引数は，順に，列車情報が収められているディレクトリ，スポット管理，ルール収集器，乱数発生器である．

その他の詳細については，ソース中のコメントを参照されたい．メインクラスのソースコードを以下に示す．

`TMain.java`

```java
/**
 * メインクラス．
 */
public class TMain {
    /**
     * スポットを生成する
     * 
     * @param spotManager スポット管理
     * @param noOfHomes   自宅数
     */
    private static void createSpots(TSpotManager spotManager, int noOfHomes) {
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes);
        spotManager.createSpots(TSpotTypes.COMPANY, 1);
        spotManager.createSpots(TSpotTypes.MIDWAY_SPOT, 1);
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfAgents   エージェント数
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfAgents) {
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfAgents);
        // それぞれの父親はstation2,station3,station4,..から通勤する
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1), "line1", "station" + (i + 2),
                    "station8");
            father.addRole(fatherRole);
            father.activateRole(fatherRole.getName());
            father.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
        }
    }

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample09";
        // ステージとその実行順序の定義：
        // 始発列車のスポット集合への登録 => 列車到着 => エージェント移動 => 列車出発 => 終着列車のスポット集合からの削除
        List<String> stages = List.of(TTransportation.TStages.NEW_TRANSPORTATION,
                TTransportation.TStages.TRANSPORTATION_ARRIVING,
                TStages.AGENT_MOVING, TTransportation.TStages.TRANSPORTATION_LEAVING,
                TTransportation.TStages.DELETING_TRANSPORTATION);
        // モデルの生成
        int interval = 1; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        // スポットの初期化
        int noOfSpots = 5; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理
        createSpots(spotManager, noOfSpots);
        // noOfSpotsと同じ数の父親を生成する
        createFatherAgents(agentManager, spotManager, noOfSpots);
        /** スポットに滞在する人数の予測値 */
        int expectedMaxNumberOfAgents = 5;
        new TTransportationManager("transportationDB", spotManager,
                model.getRuleAggregator(), model.getRandom(), false, expectedMaxNumberOfAgents);
        // エージェントの初期化
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("2/0:00"); // シミュレーション終了時刻
        PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spot.csv");
        while (model.getTime().isLessThan(simulationPeriod)) {
            printWriter.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                printWriter.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
        }
        printWriter.close();
    }
}
```