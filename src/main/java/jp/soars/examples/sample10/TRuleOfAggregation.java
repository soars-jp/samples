package jp.soars.examples.sample10;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 集計ルール
 */
public class TRuleOfAggregation extends TAgentRule {

    /** 自宅 */
    private String fHome;
    /** 職場 */
    private String fWorkplace;

    /** グローバル共有変数で集計するキー */
    public static final String HOME_KEY = "home_key";
    public static final String WORKPLACE_KEY = "workplace_key";

    /**
     * コンストラクタ
     *
     * @param ruleName  このルールの名前
     * @param ownerRole このルールをもつ役割
     * @param home      自宅
     * @param workplace 職場
     */
    public TRuleOfAggregation(String ruleName, TRole ownerRole, String home, String workplace) {
        super(ruleName, ownerRole);
        fHome = home;
        fWorkplace = workplace;
    }

    /**
     * 現在いる場所に応じてグローバル共有変数を更新する．
     * ConcurrentHashMapのcomputeメソッドはatomicであることが保証されており，並列化に対応．
     */
    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fHome)) {
            globalSharedVariables.compute(HOME_KEY, (k, v) -> v = (int) v + 1);
        } else if (isAt(fWorkplace)) {
            globalSharedVariables.compute(WORKPLACE_KEY, (k, v) -> v = (int) v + 1);
        }
    }
}
