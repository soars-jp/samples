package jp.soars.utils.transport;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TNewTransportRule extends TRule {
    /** ルール名 */
    public static final String RULE_NAME = "NewTransport";

    /** 乗り物管理 */
    private TTransportManager fTransportManager;

    /** 乗り物名 */
    private String fTransportName;

    /** 始発駅 */
    private String fStation;

    /**
     * コンストラクタ
     * 
     * @param ownerRole        このルールをもつロール
     * @param time             このルールの発火時間
     * @param stage            このルールの発火ステージ
     * @param station          始発駅
     * @param transportManager 乗り物管理
     */
    public TNewTransportRule(TTransportRole ownerRole, String station, TTransportManager transportManager) {
        super(RULE_NAME, ownerRole);
        fTransportName = ownerRole.getOwner().getName();
        fStation = station;
        fTransportManager = transportManager;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransport transport = fTransportManager.getTransportDB().get(fTransportName);
        if (transport == null) {
            throw new RuntimeException("Error: " + fTransportName + " does not exist.");
        }
        transport.moveTo(fStation);
        transport.setInServiceFlag(true);
        spotManager.getSpotDB().put(transport.getSpotName(), transport);
        // fDestinationStation駅で待っているエージェントに，電車が到着したことを通知する．
        TStation station = (TStation) spotManager.getSpotDB().get(fStation);
        station.notifyAllThatTransportArrives(transport, currentTime, currentStage, spotManager.getSpotDB(),
                agentManager.getAgentDB(), globalSharedVariables);
    }
}
