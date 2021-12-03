### サンプル9：サンプル8の修正（列車への乗車の相対時刻指定）

サンプル9では，サンプル8のシナリオと実行結果が同じになるように，父親役割のルールを絶対時刻指定から相対時刻指定に修正する．
修正後のシナリオは以下のとおりである．

- 自宅から会社に移動するシナリオ
    - 父親は，6:55に駅(station2)へ向けて自宅を出発する．
    - 父親は，5分後に駅(station2)に到着する．
    - 父親は，line1線上り(inbound)方面の最初に来た列車に乗車する．
    - 父親は，駅(station8)で列車から降車する．
    - 父親は，3分後に会社(company)に向けて駅(station8)を出発する．
    - 父親は，10分後に会社に到着する．

- 会社から自宅に移動するシナリオ
    - 父親は，17:55に駅(station8)へ向けて会社を出発する．
    - 父親は，10分後に駅(station8)に到着する．
    - 父親は，line1線下り(outbound)方面の最初に来た列車に乗車する．
    - 父親は，駅(station2)で列車から降車する．
    - 父親は，5分後に自宅に向けて駅(station2)を出発する．
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

        /**
         * コンストラクタ
         * 
         * @param ownerAgent この役割を持つエージェント
         * @param home       自宅
         */
        public TFatherRole(TAgent ownerAgent, String home) {
                super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
                fHome = home;
                moveFromHomeToCompany();
                moveFromCompanyToHome();
        }

        private void moveFromHomeToCompany() {
                String line = "line1"; // 乗車する列車の路線
                String direction = "inbound"; // 乗車する列車の方面
                String srcStation = "station2"; // 乗車駅
                String dstStation = "station8"; // 降車駅
                Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
                Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
                // 6:55に自宅を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_HOME, this, fHome, TSpotTypes.MIDWAY_SPOT, new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, REACH_STATION));
                getRule(LEAVE_HOME).setTimeAndStage(6, 55, TStages.AGENT_MOVING);
                // 7:00に乗車駅に到着する
                registerRule(new TRuleOfMovingStation(REACH_STATION, this, TSpotTypes.MIDWAY_SPOT, srcStation,
                                TStages.AGENT_MOVING, GETON_TRANSPORTATION));
                // 最初に来た電車にのる
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION, this, srcStation, line, direction,
                                trainTypes,
                                trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION));
                // 電車から降りる
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION, this, dstStation,
                                new TTime(0, 0, 3),
                                TStages.AGENT_MOVING, GO_COMPANY));
                // // 7:33に降車駅を出発して会社に向かう．
                registerRule(new TRuleOfMoving(GO_COMPANY, this, dstStation, TSpotTypes.MIDWAY_SPOT,
                                new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_COMPANY));
                // // 7:43に会社に到着する
                registerRule(new TRuleOfMoving(REACH_COMPANY, this, TSpotTypes.MIDWAY_SPOT, TSpotTypes.COMPANY));
        }

        private void moveFromCompanyToHome() {
                String line = "line1"; // 乗車する列車の路線
                String direction = "outbound"; // 乗車する列車の方面
                String srcStation = "station8"; // 乗車駅
                String dstStation = "station2"; // 降車駅
                Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
                Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
                // 17:55に会社を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_COMPANY, this, TSpotTypes.COMPANY, TSpotTypes.MIDWAY_SPOT,
                                new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_STATION_BACK));
                getRule(LEAVE_COMPANY).setTimeAndStage(17, 55, TStages.AGENT_MOVING);
                // 18:05に乗車駅に到着する．
                registerRule(new TRuleOfMovingStation(REACH_STATION_BACK, this, TSpotTypes.MIDWAY_SPOT, srcStation,
                                TStages.AGENT_MOVING, GETON_TRANSPORTATION_BACK));
                // 18:10に乗車駅で指定された列車に乗車する．
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION_BACK, this, srcStation, line,
                                direction,
                                trainTypes, trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION_BACK));
                // 18:35に降車駅で列車から降車する．
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION_BACK, this, dstStation,
                                new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, GO_HOME));
                // 18:40に降車駅を出発して自宅に向かう．
                registerRule(new TRuleOfMoving(GO_HOME, this, dstStation, TSpotTypes.MIDWAY_SPOT, new TTime(0, 0, 5),
                                TStages.AGENT_MOVING, REACH_HOME));
                // 18:45に自宅に到着する．
                registerRule(new TRuleOfMoving(REACH_HOME, this, TSpotTypes.MIDWAY_SPOT, fHome));
        }
}
```