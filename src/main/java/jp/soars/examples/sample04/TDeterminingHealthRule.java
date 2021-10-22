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
