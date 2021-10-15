package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TTime;

public class TSickPersonRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "SickPersonRole";
    /** 家を出発する */
    public static final String GO_HOSPITAL = "go_hospital";

    public static final String RECOVER = "recover";

    public TSickPersonRole(TAgent ownerAgent, String home, int medicTTime) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRuleOfMoving(GO_HOSPITAL, this, home, TSpotTypes.HOSPITAL, medicTTime, TStages.AGENT_MOVING,
                RECOVER));
        registerRule(new TRecoveringFromSickRule(RECOVER, this, TSpotTypes.HOSPITAL, home));
        getRule(GO_HOSPITAL).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
    }
}
