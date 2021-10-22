package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TTime;

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
     */
    public TSickPersonRole(TAgent ownerAgent, String home, int medicTTime) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRuleOfMoving(GO_HOSPITAL, this, home, TSpotTypes.HOSPITAL, medicTTime, TStages.AGENT_MOVING,
                RECOVER));// 10時に自宅から病院に移動する
        registerRule(new TRecoveringFromSickRule(RECOVER, this, TSpotTypes.HOSPITAL, home));
        // 病院に到着してから，時間が診察時間経過したら，自宅に戻って，役割を基本役割に戻す．
        getRule(GO_HOSPITAL).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
    }
}
