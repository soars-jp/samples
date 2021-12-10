package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 共通役割
 */
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
        super(ROLE_NAME, ownerAgent);// TAgentRuleのコンストラクタを呼び出す．
        // 健康状態決定ルール（6時，健康決定ステージ，自宅において，25%の確率で病気になる）を生成する．
        registerRule(new TDeterminingHealthRule(DETERMINE_HEALTH, this, home, 0.25));
        getRule(DETERMINE_HEALTH).setTimeAndStage(6, 0, TStages.AGENT_MOVING);
    }
}
