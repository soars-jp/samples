### サンプル4：サンプル3の拡張（役割の統合と役割の変更）

サンプル3のシナリオを拡張した以下のシナリオを考える．
- 3人の父親(father1, father2, father3)は，それぞれ自宅(home1, home2, home3)を持つ．
- 3人の父親は，9 時か（50％）10 時か（30％）11 時に（20％）自宅から同じ会社(company)に移動する．
- 3人の父親は，出社して8時間後にそれぞれの自宅に移動する．
- 3人の子供(child1, child2, child3)は，それぞれ自宅(home1, home2, home3)を持つ．
- 3人の子供は，8時に自宅から同じ学校(school)に移動する．
- 3人の子供は，15時に学校から自宅に移動する．
- 父親と子供は，6時に自宅で25%の確率で病気になる．
- 病人は，10時に自宅から病院(hospital)に移動する．
- 病人は，父親の場合，病院で2時間診察を受けた後，自宅に戻り，病気が治る．
- 病人は，子供の場合，病院で3時間診察を受けた後，自宅に戻り，病気が治る．

上述のシナリオを実現するため，4つの役割を考える．各役割とそのルールは以下の通りである．
- 共通役割(TCommonRoleクラス)
    - 6時に自宅で25%の確率で病気になる（＝病人役割になる）．
- 父親役割(TFatherRoleクラス)
    - 9 時か（50％）10 時か（30％）11 時に（20％）自宅から同じ会社(company)に移動する．
    - 出社して8時間後にそれぞれの自宅に移動する．
- 子供役割(TChildRoleクラス)
    - 8時に自宅から同じ学校(school)に移動する．
    - 15時に学校から自宅に移動する．
- 病人役割(TSickPersonRoleクラス)
    - 10時に自宅から病院(hospital)に移動する．
    - 病院で2時間(父親の場合）または3時間(子供の場合)診察を受けた後，自宅に戻り，病気が治る（＝基本役割に戻る）．

共通役割は，父親エージェントと子供エージェントに共通するルールをもつ役割であり，父親役割，子供役割に統合して用いられる．父親エージェントと子供エージェントは，それぞれ父親役割と子供役割を基本役割として持ち，病人役割を臨時役割として持つ．

共通役割と病人役割を定義するため，以下の2つの新しいルールを定義する．

- 健康状態決定ルールクラス（TDeterminingHealthRule）
    - 6時に自宅で25%の確率で病気になり，役割を病人役割に変更する．

- 「病気から回復する」ルールクラス（TRecoveringFromSickRule）
    - 「病院に移動する」ルールが発火後，診察時間が過ぎたら，病院から自宅に移動して，役割を基本役割に変更する．

健康状態決定ルールクラスを実行するステージとして，「健康状態決定ステージ（DeterminingHealth）」を新たに定義する．
「病気から回復する」ルールは，病院から自宅への移動を伴うので，「エージェント移動ステージ（AgentMoving）」で実行することにする．

#### サンプルプログラムの実行

サンプル4のプログラムは，soars2.examples.sample04パッケージにある．実行方法は，以下の通りである．

    java soars.examples.sample04.TMain

#### スポットタイプの定義

自宅(home)，会社(company)に加えて，学校(school)，病院(hospital)を定義する．

`TSpotTypes.java`

```java
public class TSpotTypes {
    /** 自宅 */
    public static final String HOME = "home";    
    /** 会社 */
    public static final String COMPANY = "company";    
    /** 学校 */
    public static final String SCHOOL = "school";
    /** 病院 */
    public static final String HOSPITAL = "hospital";
}
```

#### エージェントタイプの定義

父親(father)に加えて，子供(child)を定義する．

`TAgentTypes.java`

```java
public class TAgentTypes {
    /** 父親エージェント */
    public static final String FATHER = "father";
    /** 子供エージェント */
    public static final String CHILD = "child";
}
```

#### ステージの定義

エージェントの移動ステージに加えて，健康状態決定ステージを定義する．

`TStages.java`

```java
public class TStages {
    /** 健康状態決定ステージ */
    public static final String DETERMINING_HEALTH = "DeterminingHealth";    
    /** エージェント移動ステージ */
    public static final String AGENT_MOVING = "AgentMoving";
}
```

#### ルールの定義

まず，健康状態決定ルールクラス（TDetermingHealthRule）を定義する．このクラスは，指定された時刻，ステージ，スポットの条件が満たされた場合，指定された確率で役割を病気役割に変更する．

`TDetermingHealthRule.java`

```java
public class TDeterminingHealthRule extends TAgentRule {

    /** 病気フラグ */
    private boolean fSick;
    /** 発火スポット */
    private String fSpot;

    /** 病気に成る確率 */
    private double fProbability;

    /**
     * コンストラクタ
     * 
     * @param ruleName            このルールの名前
     * @param ownerRole           このルールを持つ役割
     * @param spot                発火スポット
     * @param probabilityToBeSick 病気になる確率
     */
    public TDeterminingHealthRule(String ruleName, TRole ownerRole, String spot, double probabilityToBeSick) {
        super(ruleName, ownerRole);
        fSick = false;
        fSpot = spot;
        fProbability = probabilityToBeSick;
    }

    /**
     * 病気か否かを返す．
     * 
     * @return true:病気である，false:病気でない
     */
    public boolean isSick() {
        return fSick;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSpot)) {
            if (getRandom().nextDouble() <= fProbability) { // スポット条件および移動確率条件が満たされたら，
                fSick = true;// 病気になる
                getAgent().activateRole("SickPersonRole");// 役割を病人役割に変更する
            } else {
                fSick = false;
            }
        }
        return;
    }
}
```

次に，「病気から回復する」ルールクラスを定義する．このクラスは，指定された時刻，ステージ，スポット条件を満たしたら，自宅に戻って，役割を基本役割に戻す．

`TRecoveringFromSickRule.java`

```java
public class TRecoveringFromSickRule extends TAgentRule {

    /** 病院 */
    private String fHospital;
    /** 自宅 */
    private String fHome;

    /**
     * コンストラクタ
     * 
     * @param ruleName  このルールの名前
     * @param ownerRole このルールをもつ役割
     * @param hospital  病院
     * @param home      自宅
     */
    public TRecoveringFromSickRule(String ruleName, TRole ownerRole, String hospital, String home) {
        super(ruleName, ownerRole);
        fHome = home;
        fHospital = hospital;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHospital)) { // 病院にいるなら
            moveTo(spotSet.get(fHome)); // 家に戻って．
            TAgent agent = getAgent();
            agent.activateRole(agent.getBaseRole().getName());// 役割を基本役割にもどす
        }
        return;
    }
}
```

#### 役割の定義

共通役割（TCommonRole），父親役割（TFatherRole），子供役割（TChildRole），病人役割（TSickPersonRole）を定義する．ここで，父親役割（TFatherRole）は，サンプル2のものと同じであるため，説明を省略する．

共通役割（TCommonRole）は，6時に自宅で25%の確率で病気になり，役割を病人役割に変更する．

`TCommonRole.java`

```java
public class TCommonRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "CommonRole";
    /** ルール名 */
    public static final String DETERMINE_HEALTH = "DetermineHealth";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent
     * @param home
     */
    public TCommonRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent);// TAgentRuleのコンストラクタを呼び出す．
        // 健康状態決定ルール（6時，健康決定ステージ，自宅において，25%の確率で病気になる）を生成する．
        registerRule(new TDeterminingHealthRule(DETERMINE_HEALTH, this, home, 0.25));
        getRule(DETERMINE_HEALTH).setTimeAndStage(6, 0, TStages.AGENT_MOVING);
    }
}
```

子供役割(TChildRoleクラス)は，8時に自宅から同じ学校(school)に移動し，15時に学校から自宅に移動する．

`TChildRole.java`

```java
public class TChildRole extends TRole {
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
    public TChildRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す
        // 自宅にいるなら学校に移動する
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, TSpotTypes.SCHOOL));
        // 学校にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.SCHOOL, home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
        getRule(RETURN_HOME).setTimeAndStage(15, 0, TStages.AGENT_MOVING);
    }
}
```

病人役割（TSickPersonRole）は，10時に自宅から病院(hospital)に移動する．病院に移動したら，指定した時間の後，病院から自宅に戻り，役割を基本役割に変更する．

`TSickPersonRole.java`

```java
public class TSickPersonRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "SickPersonRole";
    /** 家を出発するルール名 */
    public static final String GO_HOSPITAL = "go_hospital";

    /** 病気から回復するルール名 */
    public static final String RECOVER = "recover";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     * @param medicTTime 診察時間
     */
    public TSickPersonRole(TAgent ownerAgent, String home, int medicTTime) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRuleOfMoving(GO_HOSPITAL, this, home, TSpotTypes.HOSPITAL, medicTTime, TStages.AGENT_MOVING,
                RECOVER));// 10時に自宅から病院に移動する
        registerRule(new TRecoveringFromSickRule(RECOVER, this, TSpotTypes.HOSPITAL, home));
        // 病院に到着してから，時間が診察時間経過したら，自宅に戻って，役割を基本役割に戻す．
        getRule(GO_HOSPITAL).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
    }
}
```

#### メインクラスの定義

メインクラスのソースコードを以下に示す．メインクラスは，mainメソッドの他に，スポットを生成するcreateSpotsメソッド，父親エージェントを生成するcreateFatherAgentsメソッド，子供エージェントを生成するcreateChildAgentsメソッドを持つ．

`TMain.java`

```java
public class TMain {

    /**
     * スポットを生成する
     * 
     * @param spotManager スポット管理
     * @param noOfHomes   自宅の数
     */
    private static void createSpots(TSpotManager spotManager, int noOfHomes) {
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes);// noOfHomes個の家スポットを生成する．名前は，home1, home2, ...となる．
        spotManager.createSpot(TSpotTypes.COMPANY);// 1個の会社スポットを生成する，名前は，company (=TSpots.COMPANY)となる．
        spotManager.createSpot(TSpotTypes.SCHOOL);// 1個の学校スポットを生成する．
        spotManager.createSpot(TSpotTypes.HOSPITAL);// 1個の病院スポットを生成する．
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfHomes    自宅数
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        // noOfHomes体の父親エージェントを生成する．名前は，father1,father2, ...となる．
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);// i番目のエージェントを取り出す．
            TCommonRole commonRole = new TCommonRole(father, TSpotTypes.HOME + (i + 1));
            // 共通役割を生成する．
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1)); // 父親役割を生成する．
            // 父親役割を生成する．
            fatherRole.addChildRole(commonRole);// 共通役割を設定する．
            father.addRole(fatherRole);// 父親役割を設定する．
            father.setBaseRole(fatherRole);// 父親役割を基本役割に設定する．
            father.activateRole(fatherRole.getName());// 父親役割を有効にする
            TSickPersonRole sickPersonRole = new TSickPersonRole(father, TSpotTypes.HOME + (i + 1), 2);
            // 病人役割を生成する．診察時間は2時間とする．
            father.addRole(sickPersonRole);
            // 病人役割を追加する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
            // i+1番目の父親エージェントの初期位置を，i+1番目の家スポットに設定する．
        }
    }

    /**
     * 子供エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfHomes    自宅数
     */
    private static void createChildAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> children = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        // noOfHomes体の子供エージェントを生成する．名前は，child1, child2, ...となる．
        for (int i = 0; i < children.size(); i++) {
            TAgent child = children.get(i);// i番目のエージェントを取り出す．
            TCommonRole commonRole = new TCommonRole(child, TSpotTypes.HOME + (i + 1));
            // 共通役割を生成する．
            TChildRole childRole = new TChildRole(child, TSpotTypes.HOME + (i + 1));
            // 子供役割を生成する．
            childRole.addChildRole(commonRole);// 共通役割を設定する．
            child.addRole(childRole);// 子供役割を設定する．
            child.setBaseRole(childRole);// 子供役割を基本役割に設定する．
            child.activateRole(childRole.getName());// 子供役割を有効にする
            TSickPersonRole sickPersonRole = new TSickPersonRole(child, TSpotTypes.HOME + (i + 1), 3);
            // 病人役割を生成する．診察時間は3時間とする．
            child.addRole(sickPersonRole);
            // 病人役割を追加する．
            child.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
            // i+1番目の子供エージェントの初期位置を，i+1番目の家スポットに設定する．
        }
    }

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 60; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfHomes);// スポットの初期化
        createFatherAgents(agentManager, spotManager, noOfHomes);// 大人エージェントの初期化
        createChildAgents(agentManager, spotManager, noOfHomes);// 子供エージェントの初期化
        // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                System.out.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            System.out.println();
        }
    }
}
```
