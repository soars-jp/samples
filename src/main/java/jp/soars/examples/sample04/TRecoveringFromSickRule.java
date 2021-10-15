package jp.soars.examples.sample04;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

public class TRecoveringFromSickRule extends TAgentRule {

    /** 病院 */
    private String fHospital;
    /** 自宅 */
    private String fHome;

    /**
     * コンストラクタ
     * 
     * @param ruleName
     * @param ownerRole
     * @param home
     */
    public TRecoveringFromSickRule(String ruleName, TRole ownerRole, String hospital, String home) {
        super(ruleName, ownerRole);
        fHome = home;
        fHospital = hospital;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHospital)) { // スポット条件および移動確率条件が満たされたら，
            moveTo(spotSet.get(fHome)); // 目的地へ移動する．
            TAgent agent = getAgent();
            agent.activateRole(agent.getBaseRole().getName());
        }
        return;
    }
}
