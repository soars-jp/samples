package jp.soars.utils.transport;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TTransportArrivingRule extends TRule {

    /** ルール名 */
    public static final String RULE_NAME = "TransportArriving";

    /** 乗り物管理 */
    private TTransportManager fTransportManager;

    /** 乗り物名 */
    private String fTransportName;

    /** 出発駅 */
    private String fSourceStation;

    /** 到着駅 */
    private String fDestinationStation;

    /** 終着駅かを示すフラグ */
    private boolean fTerminalFlag;

    /**
     * コンストラクタ
     * 
     * @param ownerRule        このルールを持つ役割
     * @param srcStation       出発駅
     * @param dstStation       到着駅
     * @param isTerminal       到着駅が終着駅か？
     * @param transportManager 乗り物管理
     */
    public TTransportArrivingRule(TTransportRole ownerRule, String srcStation, String dstStation, boolean isTerminal,
            TTransportManager transportManager) {
        super(RULE_NAME, ownerRule);
        fTransportName = ownerRule.getOwner().getName();
        fSourceStation = srcStation;
        fDestinationStation = dstStation;
        fTransportManager = transportManager;
        fTerminalFlag = isTerminal;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransport transport = fTransportManager.getTransportDB().get(fTransportName);
        if (transport == null) {
            throw new RuntimeException("Error: " + fTransportName + " does not exist.");
        }
        String linkSpotName = fSourceStation + "-" + fDestinationStation;
        if (transport.isAt(linkSpotName)) {
            transport.moveTo(fDestinationStation);
            if (!fTerminalFlag) { // 終点でない場合
                TStation station = (TStation) spotManager.getSpotDB().get(fDestinationStation);
                // fDestinationStation駅で待っているエージェントに，電車が到着したことを通知する．
                station.notifyAllThatTransportArrives(transport, currentTime, currentStage, spotManager.getSpotDB(),
                        agentManager.getAgentDB(), globalSharedVariables);
            }
            // fDestinationStation駅で降車予定のエージェントに，fDestination駅に到着したことを知らせる．
            transport.notifyAllThatTransportArrives(fDestinationStation, currentTime, currentStage,
                    spotManager.getSpotDB(), agentManager.getAgentDB(), globalSharedVariables);
        }
    }

}
