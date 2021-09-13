package jp.soars.examples.ex01;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 父親役割．
 * 9時に会社に出社して，その8時間後に帰宅する．
 */
public class TFatherRole extends TRole {
	
	/** 役割名 */
	public static final String ROLE_NAME = "FatherRole";

    /** ルール：出社する． */
    public static final String RULE_GO_TO_COMPANY = "GoToCompany";

    /** ルール：帰宅する． */
    public static final String RULE_GO_HOME = "GoHome";

    /**
     * コンストラクタ
     * @param ownerAgent この役割を持つエージェント
     * @param home 自宅
     */
    public TFatherRole(TAgent ownerAgent, String home) {
        super(ROLE_NAME, ownerAgent); //親クラスのコンストラクタを呼び出す．
        //8時間後に帰宅する出社ルールを登録する．
        registerRule(new TRuleOfLeavingHome(RULE_GO_TO_COMPANY, this, home, TSpotTypes.COMPANY, 8));
        //帰宅ルールを登録する．
        registerRule(new TRuleOfGoingHome(RULE_GO_HOME, this, home));
        //(9時，エージェント移動ステージ)に出社する．
        getRule(RULE_GO_TO_COMPANY).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
    }
   
}