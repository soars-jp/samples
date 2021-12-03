package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 乗り物到着ルール
 */
public class TTransportationArrivingRule extends TRule {

    /** 乗り物管理 */
    private TTransportationManager fTransportationManager;

    /** 乗り物名 */
    private String fTransportationName;

    /** 出発駅 */
    private String fSourceStation;

    /** 到着駅 */
    private String fDestinationStation;

    /** 終着駅かを示すフラグ */
    private boolean fTerminalFlag;

    /**
     * コンストラクタ
     * 
     * @param ruleName              ルール名
     * @param ownerRule             このルールを持つ役割
     * @param srcStation            出発駅
     * @param dstStation            到着駅
     * @param isTerminal            到着駅が終着駅か？
     * @param transportationManager 乗り物管理
     */
    public TTransportationArrivingRule(String ruleName, TTransportationRole ownerRule, String srcStation,
            String dstStation,
            boolean isTerminal,
            TTransportationManager transportationManager) {
        super(ruleName, ownerRule);
        fTransportationName = ownerRule.getOwner().getName();
        fSourceStation = srcStation;
        fDestinationStation = dstStation;
        fTransportationManager = transportationManager;
        fTerminalFlag = isTerminal;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransportation transportation = fTransportationManager.getTransportationDB().get(fTransportationName);
        if (transportation == null) {
            throw new RuntimeException("Error: " + fTransportationName + " does not exist.");
        }
        String linkSpotName = fSourceStation + "-" + fDestinationStation;
        if (transportation.isAt(linkSpotName)) {
            transportation.moveTo(fDestinationStation);
            if (!fTerminalFlag) { // 終点でない場合
                TStation station = (TStation) spotManager.getSpotDB().get(fDestinationStation);
                // fDestinationStation駅で待っているエージェントに，乗り物が到着したことを通知する．
                station.notifyAllThatTransportationArrives(transportation, currentTime, currentStage,
                        spotManager.getSpotDB(),
                        agentManager.getAgentDB(), globalSharedVariables);
            }
            // fDestinationStation駅で降車予定のエージェントに，fDestination駅に到着したことを知らせる．
            transportation.notifyAllThatTransportationArrives(fDestinationStation, currentTime, currentStage,
                    spotManager.getSpotDB(), agentManager.getAgentDB(), globalSharedVariables);
        }
    }

}
