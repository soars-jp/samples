package jp.soars.examples.ex01;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

public class TRuleOfLeavingHome extends TAgentRule {

    /** 自宅 */
    private String fHomeSpot;

    /** 目的地 */
    private String fDestination;

    /** 目的地での滞在時間 */
    private int fStayingDuration;

    /**
     * コンストラクタ．
     * @param ownerRole このルールをもつ役割
     * @param homeSpot 自宅
     * @param destinationSpot 目的地
     * @param stayingDuration 目的地での滞在時間
     */
    public TRuleOfLeavingHome(String ruleName, TRole ownerRole, String homeSpot, String destinationSpot, int stayingDuration) {
        super(ruleName, ownerRole);
        fHomeSpot = homeSpot;
        fDestination = destinationSpot;
        fStayingDuration = stayingDuration;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet,
                        HashMap<String, TAgent> agentSet, HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHomeSpot)) { // 自宅にいたら，
            moveTo(spotSet.get(fDestination)); // 目的地へ移動する．
            int day = currentTime.getDay();
            int hour = currentTime.getHour() + fStayingDuration;
            int minute = currentTime.getMinute();
            getRule(TFatherRole.RULE_GO_HOME).setTimeAndStage(day, hour, minute, TStages.AGENT_MOVING);
        }
    }

}
