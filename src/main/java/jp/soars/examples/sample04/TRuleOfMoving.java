package jp.soars.examples.sample04;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 移動ルール．
 */
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
