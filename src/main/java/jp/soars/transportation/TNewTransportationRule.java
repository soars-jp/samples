package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 乗り物を新規登録するルール
 */
public class TNewTransportationRule extends TRule {

    /** 乗り物管理 */
    private TTransportationManager fTransportationManager;

    /** 乗り物名 */
    private String fTransportationName;

    /** 始発駅 */
    private String fStation;

    /**
     * コンストラクタ
     * 
     * @param ruleName              ルール名
     * @param ownerRole             このルールをもつロール
     * @param time                  このルールの発火時間
     * @param stage                 このルールの発火ステージ
     * @param station               始発駅
     * @param transportationManager 乗り物管理
     */
    public TNewTransportationRule(String ruleName, TTransportationRole ownerRole, String station,
            TTransportationManager transportationManager) {
        super(ruleName, ownerRole);
        fTransportationName = ownerRole.getOwner().getName();
        fStation = station;
        fTransportationManager = transportationManager;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransportation transportation = fTransportationManager.getTransportationDB().get(fTransportationName);
        if (transportation == null) {
            throw new RuntimeException("Error: " + fTransportationName + " does not exist.");
        }
        transportation.moveTo(fStation);
        transportation.setInServiceFlag(true);
        spotManager.getSpotDB().put(transportation.getSpotName(), transportation);
        // fDestinationStation駅で待っているエージェントに，乗り物が到着したことを通知する．
        TStation station = (TStation) spotManager.getSpotDB().get(fStation);
        station.notifyAllThatTransportationArrives(transportation, currentTime, currentStage, spotManager.getSpotDB(),
                agentManager.getAgentDB(), globalSharedVariables);
    }
}
