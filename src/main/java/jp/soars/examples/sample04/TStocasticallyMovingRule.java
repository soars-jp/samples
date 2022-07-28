package jp.soars.examples.sample04;

import java.util.Map;

import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpotManager;
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
     * コンストラクタ
     * 
     * @param ruleName        ルール名
     * @param ownerRole       ルールを持つ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     * @param probability     移動確率
     */
    public TStocasticallyMovingRule(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot,
            double probability) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fProbability = probability;
        fTimeToNextRule = -1;
        fStageOfNextRule = null;
        fNextRule = null;
    }

    /**
     * コンストラクタ． 絶対時刻を指定する．次のルールを指定する
     * 
     * @param ruleName        ルール名
     * @param ownerRole       ルールを持つ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     * @param timeToNextRule  次のルールを実行するまでの時間
     * @param stageOfNextRule 次のルールを実行するステージ
     * @param nextRule        次のルール名
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
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            Map<String, Object> globalSharedVariables) {
        if (isAt(fSource) && getRandom().nextDouble() <= fProbability) { // スポット条件および移動確率条件が満たされたら，
            moveTo(spotManager.getSpotDB().get(fDestination)); // 目的地へ移動する．
            if (fNextRule != null) {
                int day = currentTime.getDay();
                int hour = currentTime.getHour() + fTimeToNextRule; // 次のルールを実行する時間
                int minute = currentTime.getMinute(); // 次のルールを実行する分
                getRule(fNextRule).setTimeAndStage(day, hour, minute, fStageOfNextRule); // 臨時実行ルールとして予約
            }
        }
        return;
    }
}
