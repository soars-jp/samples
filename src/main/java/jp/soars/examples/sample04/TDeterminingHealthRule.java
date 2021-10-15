package jp.soars.examples.sample04;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

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
     * @param ruleName
     * @param ownerRole
     * @param spot
     * @param probabilityToBeSick
     */
    public TDeterminingHealthRule(String ruleName, TRole ownerRole, String spot, double probabilityToBeSick) {
        super(ruleName, ownerRole);
        fSick = false;
        fSpot = spot;
        fProbability = probabilityToBeSick;
    }

    public boolean isSick() {
        return fSick;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSpot)) {
            if (getRandom().nextDouble() <= fProbability) { // スポット条件および移動確率条件が満たされたら，
                fSick = true;
                getAgent().activateRole("SickPersonRole");
            } else {
                fSick = false;
            }
        }
        return;
    }
}
