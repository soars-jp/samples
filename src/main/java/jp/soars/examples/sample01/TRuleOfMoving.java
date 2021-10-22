package jp.soars.examples.sample01;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

/**
 * 移動ルール．
 */
public class TRuleOfMoving extends TAgentRule {

    /** 出発地 */
    private String fSource;

    /** 目的地 */
    private String fDestination;

    /**
     * コンストラクタ．
     * 
     * @param ruleName        このルールの名前
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     */
    public TRuleOfMoving(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
    }

    @Override
    public void doIt(TTime currentTime, String stage, HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // スポット条件が満たされたら，
            moveTo(spotSet.get(fDestination)); // 目的地へ移動する．
        }
    }

}
