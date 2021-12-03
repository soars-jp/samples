package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 乗り物を削除するルール
 */
public class TDeletingTransportationRule extends TRule {

    /** 乗り物管理 */
    private TTransportationManager fTransportationManager;

    /** 乗り物の名前 */
    private String fTransportationName;

    /** 終着駅 */
    private String fStation;

    /**
     * コンストラクタ
     * 
     * @param ruleName              ルール名
     * @param ownerRole             このルールを持つロール
     * @param time                  ルールの発火時刻
     * @param stage                 ルールの発火ステージ
     * @param station               終着駅
     * @param TransportationManager 乗り物管理
     */
    public TDeletingTransportationRule(String ruleName, TTransportationRole ownerRole, String station,
            TTransportationManager TransportationManager) {
        super(ruleName, ownerRole);
        fTransportationName = ownerRole.getOwner().getName();
        fStation = station;
        fTransportationManager = TransportationManager;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        TTransportation Transportation = fTransportationManager.getTransportationDB().get(fTransportationName);
        if (Transportation == null) {
            throw new RuntimeException("Error: " + fTransportationName + " does not exist.");
        }
        if (Transportation.isAt(fStation)) {
            Transportation.moveTo(null);
            spotManager.getSpotDB().remove(Transportation.getSpotName());
            Transportation.setInServiceFlag(false);
        }
    }
}
