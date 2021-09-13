package jp.soars.examples.sample01;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 父親役割．
 * 9時に会社に出社して，その8時間後に帰宅する．
 */
public class TFatherRole extends TRole {
	
	/** 役割名 */
	public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /**
     * コンストラクタ
     * @param ownerAgent この役割を持つエージェント
     * @param home 自宅
     */
    public TFatherRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); //親クラスのコンストラクタを呼び出す．
        //自宅にいるならば，会社に移動する．
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, TSpotTypes.COMPANY));
        //会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, TSpotTypes.COMPANY, home));
        //毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
        //毎日17時，エージェントステージにRETURN_HOMEルールが発火するように予約する．
        getRule(RETURN_HOME).setTimeAndStage(17, 0, TStages.AGENT_MOVING);
    }
   
}