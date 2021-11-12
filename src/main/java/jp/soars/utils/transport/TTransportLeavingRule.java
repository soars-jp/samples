package jp.soars.utils.transport;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TTransportLeavingRule extends TRule {
    /** ルール名 */
    public static final String RULE_NAME = "TransportLeaving";

    /** 乗り物管理 */
    private TTransportManager fTransportManager;

    /** 乗り物名 */
    private String fTransportName;

    /** 出発駅 */
    private String fSourceStation;

    /** 到着駅 */
    private String fDestinationStation;

    /**
     * コンストラクタ
     * 
     * @param ownerRole    このルールをもつ役割
     * @param srcStation   出発駅
     * @param dstStation   到着駅
     * @param trainManager 列車管理
     */
    public TTransportLeavingRule(TTransportRole ownerRole, String srcStation, String dstStation,
            TTransportManager transportManager) {
        super(RULE_NAME, ownerRole);
        fTransportName = ownerRole.getOwner().getName();
        fSourceStation = srcStation;
        fDestinationStation = dstStation;
        fTransportManager = transportManager;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransport transport = fTransportManager.getTransportDB().get(fTransportName);
        if (transport == null) {
            throw new RuntimeException("Error: " + fTransportName + " does not exist.");
        }
        if (transport.isAt(fSourceStation)) {
            String linkSpotName = fSourceStation + "-" + fDestinationStation;
            transport.moveTo(linkSpotName);
        }
    }
}
