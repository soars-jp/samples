package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 病人役割
 */
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
     * @param backRole   回復後に設定する役割
     */
    public TSickPersonRole(TAgent ownerAgent, String home, int medicTTime, String backRole) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRuleOfMoving(GO_HOSPITAL, this, home, TSpotTypes.HOSPITAL
                + "1", medicTTime, TStages.AGENT_MOVING,
                RECOVER));// 10時に自宅から病院に移動する
        registerRule(new TRecoveringFromSickRule(RECOVER, this, TSpotTypes.HOSPITAL + "1", home, backRole));
        // 病院に到着してから，時間が診察時間経過したら，自宅に戻って，役割を戻す．
        getRule(GO_HOSPITAL).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
    }
}
