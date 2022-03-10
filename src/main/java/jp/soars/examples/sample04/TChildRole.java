package jp.soars.examples.sample04;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/** 子供役割 */
public class TChildRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     */
    public TChildRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す
        // 自宅にいるなら学校に移動する
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, TSpotTypes.SCHOOL + "1"));
        // 学校にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.SCHOOL + "1", home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
        getRule(RETURN_HOME).setTimeAndStage(15, 0, TStages.AGENT_MOVING);
    }
}
