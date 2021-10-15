package jp.soars.examples.sample03;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 父親役割． 9時に会社に出社して，その32時間後に帰宅する．
 */
public class TFatherRole extends TRole {

    /** 役割名 */
    public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /** 確率的に家から出発する */
    public static final String STOCASTICALLY_LEAVE_HOME_9 = "StocasticallyMoving_9";
    public static final String STOCASTICALLY_LEAVE_HOME_10 = "StocasticallyMoving_10";
    public static final String STOCASTICALLY_LEAVE_HOME_11 = "StocasticallyMoving_11";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     */
    public TFatherRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        // 自宅にいるならば，会社に移動し，32時間後のエージェント移動ステージにreturn_homeルールを実行するように予約する．
        registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_9, this, home, TSpotTypes.COMPANY, 8,
                TStages.AGENT_MOVING, RETURN_HOME, 0.5));
        registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_10, this, home, TSpotTypes.COMPANY, 8,
                TStages.AGENT_MOVING, RETURN_HOME, 0.6));
        registerRule(new TStocasticallyMovingRule(STOCASTICALLY_LEAVE_HOME_11, this, home, TSpotTypes.COMPANY, 8,
                TStages.AGENT_MOVING, RETURN_HOME, 1.0));
        // 会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.COMPANY, home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(STOCASTICALLY_LEAVE_HOME_9).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
        getRule(STOCASTICALLY_LEAVE_HOME_10).setTimeAndStage(10, 0, TStages.AGENT_MOVING);
        getRule(STOCASTICALLY_LEAVE_HOME_11).setTimeAndStage(11, 0, TStages.AGENT_MOVING);
    }

}