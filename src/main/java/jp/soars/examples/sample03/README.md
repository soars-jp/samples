# サンプル3：サンプル1の拡張（独自ルールの定義）

サンプル1を，父親が確率的に会社に移動するように修正する．そのため，確率的移動ルール（TStocasticallyMovingRule）を定義する．

## サンプルプログラムの実行

サンプル3のプログラムは，soars.examples.sample03パッケージにある．実行方法は，以下の通りである．

    java soars.examples.sample03.TMain


## シナリオとシミュレーション条件

以下のシナリオを考える．
- 3人の父親(father1, father2, father3)は，それぞれ自宅(home1, home2, home3)を持つ．
- 3人の父親は，9 時か（50％）10 時か（30％）11 時に（20％） 自宅から同じ会社(company)に移動する．
- 3人の父親は，出社して8時間後にそれぞれの自宅に移動する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：6日23:00
- 時間ステップ：1時間


## ルールと役割の定義

エージェントのルールを定義する場合はTAgentRuleクラスを継承し，スポットのルールを定義する場合はTRuleクラスを継承した上で，コンストラクタとdoItメソッドを定義する．

ここでは，確率的移動ルール（TStocasticallyMovingRule）を定義する．コンストラクタは，このルールをもつ役割，出発地，目的地，次のルールを実行するまでの時間，次のルールを実行するステージ，次に実行するルール名，移動確率を引数としてとっている．doItメソッドは，スポット条件と移動確率条件が満たされたら移動先に移動し，指定された臨時実行ルールを予約する．

`TStocasticallyMovingRule.java`

```java
package jp.soars.examples.sample03;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

public class TStocasticallyMovingRule extends TAgentRule {

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

    /** 移動確率 */
    private double fProbability;

    /**
     * コンストラクタ． 絶対時刻を指定する．
     * 
     * @param ruleName
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     * @param timeToNextRule  次のルールを実行するまでの時間
     * @param stageOfNextRule 次のルールを実行するステージ
     * @param nextRule        次に実行するルール名
     * @param probability     移動確率
     */
    public TStocasticallyMovingRule(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot,
            int timeToNextRule, String stageOfNextRule, String nextRule, double probability) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fProbability = probability;
        fTimeToNextRule = timeToNextRule;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource) && getRandom().nextDouble() <= fProbability) { // スポット条件および移動確率条件が満たされたら，
            moveTo(spotSet.get(fDestination)); // 目的地へ移動する．
            if (fNextRule != null) {
                int day = currentTime.getDay();// 次のルールを実行する日付
                int hour = currentTime.getHour() + fTimeToNextRule; // 次のルールを実行する時間
                int minute = currentTime.getMinute(); // 次のルールを実行する分
                getRule(fNextRule).setTimeAndStage(day, hour, minute, fStageOfNextRule); // 臨時実行ルールとして予約
            }
        }
        return;
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

        /** 確率的に家から出発する */
        public static final String STOCASTICALLY_LEAVE_HOME_9 = "StocasticallyMoving_9";
        public static final String STOCASTICALLY_LEAVE_HOME_10 = "StocasticallyMoving_10";
        public static final String STOCASTICALLY_LEAVE_HOME_11 = "StocasticallyMoving_11";

        /**
         * コンストラクタ
         * 
         * @param ownerAgent この役割を持つエージェント
         * @param home       自宅
         */
        public TFatherRole(TAgent ownerAgent, String home) {
                super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
                // 自宅にいるならば，会社に移動し，32時間後のエージェント移動ステージにreturn_homeルールを実行するように予約する．
                registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_9, this, home, TSpotTypes.COMPANY, 8,
                                TStages.AGENT_MOVING, RETURN_HOME, 0.5));
                registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_10, this, home, TSpotTypes.COMPANY,
                                8, TStages.AGENT_MOVING, RETURN_HOME, 0.6));
                registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_11, this, home, TSpotTypes.COMPANY,
                                8, TStages.AGENT_MOVING, RETURN_HOME, 1.0));
                // 会社にいるならば，自宅に移動する．
                registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.COMPANY, home));
                // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
                getRule(STOCASTICALLY_LEAVE_HOME_9).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
                getRule(STOCASTICALLY_LEAVE_HOME_10).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
                getRule(STOCASTICALLY_LEAVE_HOME_11).setTimeAndStage(11, 0, TStages.AGENT_MOVING);
        }
}
```
