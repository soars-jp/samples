package jp.soars.utils.transport;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TRule;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TDeletingTransportRule extends TRule {

    /** ルール名 */
    public static final String RULE_NAME = "DeletingTransport";

    /** 乗り物管理 */
    private TTransportManager fTransportManager;

    /** 乗り物の名前 */
    private String fTransportName;

    /** 終着駅 */
    private String fStation;

    /**
     * コンストラクタ
     * 
     * @param ownerRole        このルールを持つロール
     * @param time             ルールの発火時刻
     * @param stage            ルールの発火ステージ
     * @param station          終着駅
     * @param transportManager 乗り物管理
     */
    public TDeletingTransportRule(TTransportRole ownerRole, String station, TTransportManager transportManager) {
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
        if (transport.isAt(fStation)) {
            transport.moveTo(null);
            spotManager.getSpotDB().remove(transport.getSpotName());
            transport.setInServiceFlag(false);
        }
    }
}
