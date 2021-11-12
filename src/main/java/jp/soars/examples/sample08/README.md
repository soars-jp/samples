### サンプル8：列車への乗車（絶対時刻指定）

1人の父親が列車を使って通勤する以下のシナリオを考える．ここでは，父親は，乗車したい列車と，その列車の乗車駅と降車駅への到着時刻を知っているものとする．また，父親(father1)は自宅(home1)に住んでいるものとする．

- 自宅から会社に移動するシナリオ
    - 父親は，6:55に駅(station2)へ向けて自宅を出発する．
    - 父親は，7:00に駅(station2)に到着する．
    - 父親は，7:05に駅(station2)で，line1線上り方面(inbound)の列車(001)に乗車する．
    - 父親は，7:30に駅(station8)で，line1線上り方面(inbound)の列車(001)から降車する．
    - 父親は，7:33に会社(company)に向けて駅(station8)を出発する．
    - 父親は，7:43に会社に到着する．

- 会社から自宅に移動するシナリオ
    - 父親は，17:55に駅(station8)へ向けて会社を出発する．
    - 父親は，18:05に駅(station8)に到着する．
    - 父親は，18:10に駅(station8)で，line1線下り方面(outbound)の列車(002)に乗車する．
    - 父親は，18:35に駅(station2)で，line1線下り方面(outbound)の列車(002)から降車する．
    - 父親は，18:40に自宅に向けて駅(station2)を出発する．
    - 父親は，18:45に自宅に到着する．

line1線の上り方面の列車(001)の運行スケジュールは以下の通りである．

| Station | 001 |
|---------|------|
| station1 | 7:00 |
| station2 | 7:05 |
| station3 | 7:08 |
| station4 | 7:13 |
| station5 |7:17 |
| station6 | 7:20 |
| station7 | 7:26 |
| station8 | 7:30 |
| station9 | 7:34 |
| station10 | 7:40 |

line1線下り方面の列車(001と002)の運航スケジュールは以下の通りである．

| Station | 001 | 002 |
|---------|-----|-----|
| station10 | 6:30 | 18:00 |
| station9 | 6:36 | 18:06 |
| station8 | 6:40 | 18:10 |
| station7 | 6:44 | 18:14 |
| station6 | 6:50 | 18:20 |
| station5 | 6:53 | 18:23 |
| station4 | 6:57 | 18:27 |
| station3 | 7:02 | 18:32 |
| station2 | 7:05 | 18:35 |
| station1 | 7:10 | 18:40 |


#### サンプルプログラムの実行

サンプルプログラムは以下で実行できる．

    java jp.soars.examples.sample08.TMain

#### 駅，路線，列車の運航スケジュール

駅，路線，列車の運航スケジュールの情報は，transportDBディレクトリの下に収められている．ファイル，ディレクトリはそれぞれ以下のとおりである．
- stations.csv  
駅名
- lines.csv  
路線名
- line1ディレクトリ  
路線名がline1である路線の運行情報が収められている．lines.csv中の名前と一致している必要がある．
- line1/trains.csv  
line1線の各列車の情報として，路線名（Line），方面（Direction），列車名（TrainName），列車タイプ（Type），始発駅（Source），始発駅の出発時刻（DepartureTime），終着駅（Destination），終着駅への到着時刻（ArrivalTime）が収められている．
- line1/inboundディレクトリ  
line1線上り方面（inbound）方面の各列車の運行情報のCSVファイルが収められている．ここで，inboundは上記trains.csv中のDirectionの名前と一致している必要がある．
- line1/inbound/001.csv  
line1線上り方面（inbound）方面の列車001の運行スケジュールである．各駅の出発時刻が記載されている．
- line1/outboundディレクトリ  
line1線下り方面（outbound）方面の各列車の運行情報のCSVファイルが収められている．ここで，outboundは上記trains.csv中のDirectionの名前と一致している必要がある．
- line1/outbound/001.csv, 002.csv  
line1線下り方面（outbound）方面の列車001, 002の運行スケジュールである．各駅の出発時刻が記載されている．

上記のファイルは，utils.transport.TTransportManagerクラスで読み込まれるので，フォーマット等を変更したい場合は，utils.transport.TTransportManagerクラスを修正する．

#### スポットタイプの定義

自宅（home)，会社（company）に加えて，移動中（-）を定義する．駅のスポット名は，上述のtransportDB/stations.csvにある駅名が用いられる．
ここで，移動中(-)は，自宅から駅に移動中，駅から会社に移動中，会社から駅に移動中，駅から自宅に移動中のすべてを表す抽象的なスポットとして定義している点に注意されたい．これは，スポット間をスポットとして区別しようとすると，本来のスポット数の2乗の数のスポット間を用意しなければならなくなるためである．

`TSpotTypes.java`

```java
public class TSpotTypes {
    /** 自宅 */
    public static final String HOME = "home";

    /** 出発地と目的地の中間スポット */
    public static final String MIDWAY_SPOT = "-";

    /** 会社 */
    public static final String COMPANY = "company";
}
```

#### ステージの定義

ステージとして，エージェント移動ステージ（AgentMoving）を定義し，列車の乗降もこのステージで行うこととする．

`TStages.java`

```java
public class TStages {
    /** エージェント移動ステージ */
    public static final String AGENT_MOVING = "AgentMoving";
}
```

列車の運行に関するステージは，utils.transport.TTransportクラスで定義されている以下の4つのステージ（始発のスポット集合への登録，到着，出発，終着のスポット集合からの削除）を利用する．

```java
    /** 列車に関連するステージ */
    public class TStages {
        /** 始発のスポット集合への登録 */
        public static final String NEW_TRANSPORT = "NewTransport";
        /** 到着 */
        public static final String TRANSPORT_ARRIVING = "TransportArraiving";
        /** 出発 */
        public static final String TRANSPORT_LEAVING = "TransportLeaving";
        /** 終着のスポット集合からの削除 */
        public static final String DELETING_TRANSPORT = "DeletingTransport";
    }
```

ここでは，列車の乗降をエージェント移動ステージ（AgentMoving）で行うため，ステージの実行順序を，1) 始発のスポット集合への登録，2) 列車の到着，3) エージェントの移動，4) 列車の出発，5) 終着のスポット集合からの削除，とする．具体的には，TMainクラスのmainメソッドにおいて，以下のように宣言する．

```java
 List<String> stages = List.of(TTransport.TStages.NEW_TRANSPORT, TTransport.TStages.TRANSPORT_ARRIVING,
                TStages.AGENT_MOVING, TTransport.TStages.TRANSPORT_LEAVING, TTransport.TStages.DELETING_TRANSPORT);
```

#### 役割の定義

「列車に乗る」ルールとしてutils.transport.TGettingOnTransportRuleクラス，「列車から降りる」ルールとしてutils.transport.TGettingOffTransportRuleクラスが用意されているので，これらを利用して父親役割(TFatherRole)を定義する．

TGettingOnTransportRuleクラスのコンストラクタは以下の通りである．

```java
   public TGettingOnTransportRule(String ruleName, TRole ownerRole, String station, String line, String direction, String transportName)
```
ここで，引数ruleNameにはルール名，引数のownerRoleにはこのルールを持つ役割を指定する．
引数のstationには乗車駅，lineには乗車路線，directionには乗車方面，transportNameには車両名を指定する．
乗車時刻とステージは，setTimeAndStageメソッドで設定する．
路線，駅，時刻，乗車方面，車両名は，transportDBディレクトリ下で定義されているものを指定する必要があることに注意されたい．

TGettingOffTransportRuleクラスのコンストラクタは以下のとおりである．
```java
    public TGettingOffTransportRule(String ruleName, TRole ownerRole, String station, String line, String direction, String transportName) 
```
ここで，引数ruleNameにはルール名，引数のownerRoleにはこのルールを持つ役割を指定する．
引数のstationには降車駅，lineには路線，directionには乗車方面，transportNameには車両名を指定する．
下車時刻とステージは，setTimeAndStageメソッドで設定する．

父親役割のソースコードを以下に示す．コンストラクタから，自宅から会社に列車で移動するルール群を生成するためのmoveFromHomeToCompanyメソッド，および，会社から自宅に列車で移動するルール群を生成するためのmoveFromCompanyToHomeメソッドを呼び出している．詳細は，ソースコード中のコメントを参照されたい．

`TFatherRole.java`

```java
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
        public static final String GETON_TRANSPORT = "geton_transport";

        /** 電車に乗る（帰宅） */
        public static final String GETON_TRANSPORT_BACK = "geton_transport_back";

        /** 電車から降りる（出勤） */
        public static final String GETOFF_TRANSPORT = "getoff_transport";

        /** 電車から降りる（帰宅） */
        public static final String GETOFF_TRANSPORT_BACK = "getoff_transport_back";

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
                String trainName = "001"; // 乗車する列車名
                String srcStation = "station2"; // 乗車駅
                String dstStation = "station8"; // 降車駅
                // 6:55に自宅を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_HOME, this, fHome, TSpotTypes.MIDWAY_SPOT));
                getRule(LEAVE_HOME).setTimeAndStage(6, 55, TStages.AGENT_MOVING);
                // 7:00に乗車駅に到着する
                registerRule(new TRuleOfMoving(REACH_STATION, this, TSpotTypes.MIDWAY_SPOT, srcStation));
                getRule(REACH_STATION).setTimeAndStage(7, 0, TStages.AGENT_MOVING);
                // 7:05に電車にのる
                registerRule(new TGettingOnTransportRule(GETON_TRANSPORT, this, srcStation, line, direction,
                                trainName));
                getRule(GETON_TRANSPORT).setTimeAndStage(7, 5, TStages.AGENT_MOVING);
                // 7:30に電車から降りる
                registerRule(new TGettingOffTransportRule(GETOFF_TRANSPORT, this, dstStation, line, direction,
                                trainName));
                getRule(GETOFF_TRANSPORT).setTimeAndStage(7, 30, TStages.AGENT_MOVING);
                // 7:33に降車駅を出発して会社に向かう．
                registerRule(new TRuleOfMoving(GO_COMPANY, this, dstStation, TSpotTypes.MIDWAY_SPOT));
                getRule(GO_COMPANY).setTimeAndStage(7, 33, TStages.AGENT_MOVING);
                // 7:43に会社に到着する
                registerRule(new TRuleOfMoving(REACH_COMPANY, this, TSpotTypes.MIDWAY_SPOT, TSpotTypes.COMPANY));
                getRule(REACH_COMPANY).setTimeAndStage(7, 43, TStages.AGENT_MOVING);
        }

        private void moveFromCompanyToHome() {
                String line = "line1"; // 乗車する列車の路線
                String direction = "outbound"; // 乗車する列車の方面
                String trainName = "002"; // 乗車する列車名
                String srcStation = "station8"; // 乗車駅
                String dstStation = "station2"; // 降車駅
                // 17:55に会社を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_COMPANY, this, TSpotTypes.COMPANY, TSpotTypes.MIDWAY_SPOT));
                getRule(LEAVE_COMPANY).setTimeAndStage(17, 55, TStages.AGENT_MOVING);
                // 18:05に乗車駅に到着する．
                registerRule(new TRuleOfMoving(REACH_STATION_BACK, this, TSpotTypes.MIDWAY_SPOT, srcStation));
                getRule(REACH_STATION_BACK).setTimeAndStage(18, 5, TStages.AGENT_MOVING);
                // 18:10に乗車駅で指定された列車に乗車する．
                registerRule(new TGettingOnTransportRule(GETON_TRANSPORT_BACK, this, srcStation, line, direction,
                                trainName));
                getRule(GETON_TRANSPORT_BACK).setTimeAndStage(18, 10, TStages.AGENT_MOVING);
                // 18:35に降車駅で列車から降車する．
                registerRule(new TGettingOffTransportRule(GETOFF_TRANSPORT_BACK, this, dstStation, line, direction,
                                trainName));
                getRule(GETOFF_TRANSPORT_BACK).setTimeAndStage(18, 35, TStages.AGENT_MOVING);
                // 18:40に降車駅を出発して自宅に向かう．
                registerRule(new TRuleOfMoving(GO_HOME, this, dstStation, TSpotTypes.MIDWAY_SPOT));
                getRule(GO_HOME).setTimeAndStage(18, 40, TStages.AGENT_MOVING);
                // 18:45に自宅に到着する．
                registerRule(new TRuleOfMoving(REACH_HOME, this, TSpotTypes.MIDWAY_SPOT, fHome));
                getRule(REACH_HOME).setTimeAndStage(18, 45, TStages.AGENT_MOVING);
        }
}
```

#### メインクラスの定義

メインクラスは，mainメソッドの他に，スポットを生成するcreateSpotsメソッド，父親エージェントを生成するcreateFatherAgentsメソッドを持つ．

列車については，以下のようにutils.transport.TTransportManagerクラスのオブジェクトを生成すればよい．utils.transport.TTransportManagerクラスのオブジェクトを生成すると，列車情報が読み込まれて，列車に関するルールがルール収集器に登録される．コンストラクタの引数は，順に，列車情報が収められているディレクトリ，スポット管理，ルール収集器，乱数発生器である．

その他の詳細については，ソース中のコメントを参照されたい．メインクラスのソースコードを以下に示す．

`TMain.java`

```java
public class TMain {
    /**
     * スポットを生成する
     * 
     * @param spotManager
     * @param noOfHomes
     */
    private static void createSpots(TSpotManager spotManager, int noOfSpots) {
        spotManager.createSpots(TSpotTypes.HOME, noOfSpots);
        spotManager.createSpot(TSpotTypes.COMPANY);
        spotManager.createSpot(TSpotTypes.MIDWAY_SPOT);
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param args
     * @throws IOException
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager) {
        int noOfAgents = 1;
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfAgents);
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1));
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
        String logDir = "logs/sample08";
        // 乱数生成
        ICRandom rand = new TCJava48BitLcg();
        // ステージとその実行順序の定義：
        // 始発列車のスポット集合への登録 => 列車到着 => エージェント移動 => 列車出発 => 終着列車のスポット集合からの削除
        List<String> stages = List.of(TTransport.TStages.NEW_TRANSPORT, TTransport.TStages.TRANSPORT_ARRIVING,
                TStages.AGENT_MOVING, TTransport.TStages.TRANSPORT_LEAVING, TTransport.TStages.DELETING_TRANSPORT);
        // モデルの生成
        int interval = 1; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        // スポットの初期化
        int noOfSpots = 1; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots);
        createFatherAgents(agentManager, spotManager);
        /** スポットに滞在する人数の予測値 */
        int expectedMaxNumberOfAgents = 1;
        TTransportManager transportManager = new TTransportManager("transportDB", spotManager,
                model.getRuleAggregator(), rand, expectedMaxNumberOfAgents);
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
