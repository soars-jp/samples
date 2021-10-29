# サンプル１：最も簡単なプログラム

## サンプルプログラムの実行

サンプル1のプログラムは，jp.soars.examples.sample01パッケージにある．実行方法は，以下の通りである．

    java jp.soars.examples.sample01.TMain


## シナリオとシミュレーション条件

以下のシナリオを考える．
- 3人の父親(father1, father2, father3)は，それぞれ自宅(home1, home2, home3)を持つ．
- 3人の父親は，9:00に自宅から同じ会社(company)に移動する．
- 3人の父親は，17:00にそれぞれの自宅に移動する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：6日23:00
- 時間ステップ：1時間

## スポットタイプの定義

スポットタイプとして，homeとcompanyを定義する．

`TSpotTypes.java`

```java
public class TSpotTypes {
    /** 自宅 */
    public static final String HOME = "home";
    /** 会社 */
    public static final String COMPANY = "company";
}
```

## エージェントタイプの定義

エージェントタイプとして，fatherを考える．

`TAgentTypes.java`

```java
public class TAgentTypes {
    /** 父親エージェント */
    public static final String FATHER = "father";
}
```

## ステージの定義

ステージとして，エージェント移動ステージAgentMovingを定義する．

`TStages.java`

```java
public class TStages {
    /** エージェント移動ステージ */
    public static final String AGENT_MOVING = "AgentMoving";
}
```

## ルールと役割の定義

ルールとしては，soar2.coreパッケージで定義されているルールクラス（TAgentRule）を継承することにより，移動ルールクラス（TRuleOfMoving）を定義する．
移動ルールは，出発地にいるのであれば，目的地に移動するルールである．

役割としては，soar2.coreパッケージで定義されている役割クラス（TRole）を継承することにより，父親役割クラス（TFatherRole）を定義する．
父親役割は，家を出発地，会社を目的地とする移動ルール（leave_home）と，会社を出発地，家を目的地とする移動ルール（return_home）から構成される．
leave_homeルールは毎日9時のAgentMovingステージに実行されるように予約され，return_homeルールは毎日17時のAgentMovingステージに実行されるように予約されている．

詳細は，ソースコード内のコメントを参照されたい．

`TRuleOfMoving.java`

```java
/**
 * 移動ルール．
 */
public class TRuleOfMoving extends TAgentRule {

    /** 出発地 */
    private String fSource;

    /** 目的地 */
    private String fDestination;

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
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // スポット条件が満たされたら，
            moveTo(spotSet.get(fDestination)); // 目的地へ移動する．
        }
    }

}

```

`TFatherRole.java`

```java
/**
 * 父親役割．
 * 9時に会社に出社して，その8時間後に帰宅する．
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
     * @param ownerAgent この役割を持つエージェント
     * @param home 自宅
     */
    public TFatherRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); //親クラスのコンストラクタを呼び出す．
        //自宅にいるならば，会社に移動する．
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, TSpotTypes.COMPANY));
        //会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.COMPANY, home));
        //毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
        //毎日17時，エージェントステージにRETURN_HOMEルールが発火するように予約する．
        getRule(RETURN_HOME).setTimeAndStage(17, 0, TStages.AGENT_MOVING);
    }
   
}
```

親クラスのTAgentRuleクラスのコンストラクタの定義を以下に示す．

`TAgentRuleクラスのコンストラクタ`

```java
/**
 * コンストラクタ．
 * @param ruleName ルール名
 * @param ownerRole このルールを持つロール
 * @param spotName このルールの発火スポット名
 */
public TAgentRule(String ruleName, TRole ownerRole, String spotName)
```

registerRuleメソッドとgetRuleメソッドは，TRoleクラスで定義されているメソッドである．registerRuleメソッドは，ルールを役割に登録するためのメソッドである．getRuleメソッドは，役割からルールを得るためのメソッドである．それぞれの定義を以下に示す．

`TRoleクラスのregisterRuleメソッドとgetRuleメソッド`

```java
/**
 * ルールを登録する．すでに登録済みであれば実行時例外が発生する．
 * @param rule ルール
 */
public void registerRule(TRule rule)

/**
 * ルールを返す．該当のルールがなければ実行時例外が発生する．
 * @param ruleName ルール名
 * @return
 */
public TRule getRule(String ruleName)
```

ルールの発火時刻と発火ステージを設定するためのsetTimeAndStageメソッドの定義を以下に示す．setTimeAndStageメソッドは，TAgentRuleクラスの親クラスであるTRuleクラスで定義されている．

`TRuleクラスのsetTimeAndStageメソッド`

```java
/**
 * 定時実行ルールとして，発火時刻と発火ステージを設定する．
 * 同一時刻，同一ステージへの設定を２回以上することはできないことに注意すること，
 * ２回以上設定された場合は例外が発生する．
 * @param hour 発火時
 * @param minute 発火分
 * @param stage 発火ステージ
 * @return true:成功，false:失敗 
 */
public boolean setTimeAndStage(int hour, int minute, String stage)
```

#### メインクラスの定義

メインクラスのソースコードを以下に示す．メインクラスは，メインメソッドのみをもつ．

メインメソッドでは，まず，ステージの初期化を行っている．ここでは，エージェント移動ステージ(TStages.AGENT_MOVING)のみからなるリストを生成している．

次に，モデル(TModel)の生成を行っている．ここでは，１時間ステップあたりの分数と乱数シードを定義して，モデルのコンストラクタに渡している．

次に，スポットの初期化を行っている．ここでは，モデルからスポット管理（TSpotNanager）を取得して，スポット管理を利用して，3個の自宅と1個の会社を生成している．

次に，エージェントを初期化している．ここでは，モデルからエージェント管理（TAgentManager）と乱数生成器（ICRandom）を取得した後，エージェント管理を利用して3個の父親エージェント(father1, father2, father3)を生成している．父親エージェントには，それぞれ父親役割（TFatherRole）を基本役割が割り当てられている．father1, father2, father3には，自宅としてそれぞれhome1, home2, home3が割り当てられ，初期スポットとして設定されている．

最後に，メインループを実行している．各時刻において，モデル(TModel)のexecuteメソッドを呼び出すことにより，その時刻に実行すべきルールを実行している．また，各時刻において，時刻と各エージェントの位置を画面に表示している．

`TMain.java`

```java
/**
 * メインクラス． シミュレーションステップ：60分 シミュレーション期間：７日間
 * シナリオ：３人の父親エージェントが，毎日，9時に自宅を出発して会社に行き，17時に会社を出発して自宅に戻る．
 */
public class TMain {

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
        // スポットの初期化
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes); // noOfHomes個の家スポットを生成する．名前は，home1, home2, ...となる．
        spotManager.createSpot(TSpotTypes.COMPANY); // 1個の会社スポットを生成する，名前は，company (=TSpots.COMPANY)となる．
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); // noOfHomes体の父親エージェントを生成する．名前は，father1,
                                                                                              // father2, ...となる．
        for (int i = 0; i < fathers.size(); ++i) {
            TAgent father = fathers.get(i); // i番目のエージェントを取り出す．
            String home = TSpotTypes.HOME + (i + 1); // i番目のエージェントの自宅のスポット名を生成する．
            TFatherRole fatherRole = new TFatherRole(father, home); // 父親役割を生成する．
            father.activateRole(fatherRole.getName()); // 父親役割をアクティブ化する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); // 初期位置を自宅に設定する．
        }
        // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : fathers) {
                System.out.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            System.out.println();
        }
    }

}
```

#### ルールロガーを使ったデバッグ

ルールロガー(TRuleLogger)を用いると，ルールの登録／発火／削除を記録するをCSVファイルに記録することができる．
CSVファイルには，ルールの登録／発火／削除の時刻(CurrentTime)，ルールの登録／発火／削除のステージ(CurrentStage)，エージェントルールの登録／発火／削除のスポット(CurrentSpot)，登録(register)／発火(fire)／削除(remove)の種類(Action)，ルールリスト（TodaysRules／FutureRules）の種類(RuleListType)，ルールを生成したエージェント名／スポット名(ObjectName)，ルールを生成した役割名(RoleName)，ルール名(RuleName)，発火時刻条件(FiringTimeCondition)，発火ステージ条件(FiringStageCondition)，発火スポット条件(FiringSpotCondition)，ユーザ定義のデバッグ情報(DebugInfo)が出力される．ユーザ定義のデバッグ情報を出力するためには，TRuleクラスのdebugInfoメソッドをオーバーライドすればよい．

ルールロガーを開始するためには，TModelクラスのbeginRuleLoggerメソッドを呼ぶ．ルールロガーを開始した場合は，プログラムを終了する前に必ずendRuleLoggerクラスを呼んでルールロガーを終了する必要がある．

ルールロガーを利用するメインクラスを以下に示す．差分は，モデル生成においてbeginRuleLoggerメソッドを呼んでいる点，メインループを抜けたところでendRuleLoggerを呼んでいる点である．このプログラムを実行すると，logs/sample01/ruleAction.csvが出来上がる．

`TMainWithRuleLogger.java`

```java
public class TMainWithRuleLog {
    /**
     * メインメソッド．
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	//ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); //ステージは，エージェント移動のみ．
        //モデルの生成
        int interval = 60; //１ステップの分数
        long seed = 0; //乱数シード
        TModel model = new TModel(stages, interval, seed);
        model.beginRuleLogger("logs/sample01/ruleAction.csv");  //ルールの登録／発火／削除を記録するためのロガー（ルールロガー）を開始．
        //スポットの初期化
        int noOfHomes = 3; //家の数        
        TSpotManager spotManager = model.getSpotManager(); //スポット管理
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes); //noOfHomes個の家スポットを生成する．名前は，home1, home2, ...となる．
        spotManager.createSpot(TSpotTypes.COMPANY); //1個の会社スポットを生成する，名前は，company (=TSpots.COMPANY)となる．
        //エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); //エージェント管理
        ICRandom random = model.getRandom(); //乱数発生器
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); //noOfHomes体の父親エージェントを生成する．名前は，father1, father2, ...となる．
        for (int i = 0; i < fathers.size(); ++i) {
            TAgent father = fathers.get(i); //i番目のエージェントを取り出す．
            String home = TSpotTypes.HOME + (i + 1); //i番目のエージェントの自宅のスポット名を生成する．
            TFatherRole fatherRole = new TFatherRole(father, random, home); //父親役割を生成する．
            father.setBaseRole(fatherRole); //父親役割を基本役割に設定する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); //初期位置を自宅に設定する．
        }
        //メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00"); //シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); //時刻を表示する．
            model.execute(); //モデルの実行
            for (TAgent a: fathers) {
                System.out.print(a.getCurrentSpotName() + "\t"); //各エージェントが位置しているスポット名を表示する．
            }
            System.out.println();
        }        
        model.endRuleLogger(); //ルールロガーを終了する．
    }   
}
```