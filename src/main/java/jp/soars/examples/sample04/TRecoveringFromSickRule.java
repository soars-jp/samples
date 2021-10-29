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

    /** 回復後に設定する役割 */
    public static String fBackRole;

    /**
     * コンストラクタ
     * 
     * @param ruleName  このルールの名前
     * @param ownerRole このルールをもつ役割
     * @param hospital  病院
     * @param home      自宅
     */
    public TRecoveringFromSickRule(String ruleName, TRole ownerRole, String hospital, String home, String backRole) {
        super(ruleName, ownerRole);
        fHome = home;
        fHospital = hospital;
        fBackRole = backRole;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHospital)) { // 病院にいるなら
            moveTo(spotSet.get(fHome)); // 家に戻って．
            TAgent agent = getAgent();
            agent.activateRole(fBackRole);// 役割を基本役割にもどす
        }
        return;
    }
}