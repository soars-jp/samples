package jp.soars.examples.ex01;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

public class TRuleOfGoingHome extends TAgentRule {

    /** 自宅 */
    private String fHomeSpot;

    /**
     * コンストラクタ．
     * @param ruleName ルール名
     * @param ownerRole このルールをもつ役割
     * @param homeSpot 自宅スポット名
     */
    public TRuleOfGoingHome(String ruleName, TRole ownerRole, String homeSpot) {
        super(ruleName, ownerRole);
        fHomeSpot = homeSpot;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet,
                        HashMap<String, TAgent> agentSet, HashMap<String, Object> globalSharedVariables) {
        moveTo(spotSet.get(fHomeSpot)); // 自宅へ移動する．
    }

}
