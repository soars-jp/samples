package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

public class TCommonRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "CommonRole";
    /** ルール名 */
    public static final String DETERMINE_HEALTH = "DetermineHealth";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent
     * @param home
     */
    public TCommonRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent);
        // 健康状態決定ルール
        registerRule(new TDeterminingHealthRule(DETERMINE_HEALTH, this, home, 0.25));
        getRule(DETERMINE_HEALTH).setTimeAndStage(6, 0, TStages.AGENT_MOVING);
    }
}
