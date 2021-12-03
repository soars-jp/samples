package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 乗り物出発ルール
 */
public class TTransportationLeavingRule extends TRule {

    /** 乗り物管理 */
    private TTransportationManager fTransportationManager;

    /** 乗り物名 */
    private String fTransportationName;

    /** 出発駅 */
    private String fSourceStation;

    /** 到着駅 */
    private String fDestinationStation;

    /**
     * コンストラクタ
     * 
     * @param ruleName              ルール名
     * @param ownerRole             このルールをもつ役割
     * @param srcStation            出発駅
     * @param dstStation            到着駅
     * @param transportationManager 乗り物管理
     */
    public TTransportationLeavingRule(String ruleName, TTransportationRole ownerRole, String srcStation,
            String dstStation,
            TTransportationManager transportationManager) {
        super(ruleName, ownerRole);
        fTransportationName = ownerRole.getOwner().getName();
        fSourceStation = srcStation;
        fDestinationStation = dstStation;
        fTransportationManager = transportationManager;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransportation transportation = fTransportationManager.getTransportationDB().get(fTransportationName);
        if (transportation == null) {
            throw new RuntimeException("Error: " + fTransportationName + " does not exist.");
        }
        if (transportation.isAt(fSourceStation)) {
            String linkSpotName = fSourceStation + "-" + fDestinationStation;
            transportation.moveTo(linkSpotName);
        }
    }
}
