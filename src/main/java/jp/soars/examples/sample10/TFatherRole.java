package jp.soars.examples.sample10;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

/**
 * 父親役割． 9時に会社に出社して，その8時間後に帰宅する．
 */
public class TFatherRole extends TRole {

    /** 役割名 */
    public static final String ROLE_NAME = "FatherRole";

    /** 家を出発する */
    public static final String LEAVE_HOME = "leave_home";

    /** 家に帰る */
    public static final String RETURN_HOME = "return_home";

    /** 集計 */
    public static final String AGGREGATION = "aggregation";

    /**
     * コンストラクタ
     *
     * @param ownerAgent この役割を持つエージェント
     * @param home       自宅
     * @param workplace  職場
     */
    public TFatherRole(TAgent ownerAgent, String home, String workplace) {
        super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
        // 自宅にいるならば，会社に移動する．
        registerRule(new TRuleOfMoving(LEAVE_HOME, this, home, workplace));
        // 会社にいるならば，自宅に移動する．
        registerRule(new TRuleOfMoving(RETURN_HOME, this, workplace, home));
        // 毎日9時，エージェントステージにLEAVE_HOMEルールが発火するように予約する．
        getRule(LEAVE_HOME).setTimeAndStage(9, 0, TStages.AGENT_MOVING);
        // 毎日17時，エージェントステージにRETURN_HOMEルールが発火するように予約する．
        getRule(RETURN_HOME).setTimeAndStage(17, 0, TStages.AGENT_MOVING);
        // 集計ルールの登録
        registerRule(new TRuleOfAggregation(AGGREGATION, this, home, workplace));
        // 集計ルールを毎時刻実行されるように予約する．
        getRule(AGGREGATION).setStage(TStages.AGGREGATION);
    }

}
