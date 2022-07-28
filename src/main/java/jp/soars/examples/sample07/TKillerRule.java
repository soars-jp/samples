package jp.soars.examples.sample07;

import java.util.List;
import java.util.Map;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.utils.random.ICRandom;

public class TKillerRule extends TAgentRule {

    /** ルール名 */
    public static String RULE_NAME = "KillerRule";

    /** デバッグ情報として削除(しようとした)エージェント名とスポット名を出力する */
    private String fSpotName = "";
    private String fAgentName = "";

    /**
     * 削除ルール
     * 
     * @param ownerRole ルールを持つ役割
     */
    public TKillerRule(TRole ownerRole) {
        super(RULE_NAME, ownerRole);
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager,
            TAgentManager agentManager, Map<String, Object> globalSharedVariables) {
        ICRandom rand = getOwnerRole().getRandom();
        // ダミースポットをランダムに１つ削除
        List<TSpot> dummySpotList = spotManager.getSpots(TSpotTypes.DUMMY_SPOT);
        TSpot spot = dummySpotList.get(rand.nextInt(dummySpotList.size()));
        fSpotName = spot.getName();
        if (spot.getAgents().isEmpty()) { // エージェントがいるスポットを消そうとするとエラー
            spotManager.deleteSpot(spot);
        }
        // ダミーエージェントをランダムに１つ削除
        List<TAgent> dummyAgentList = agentManager.getAgents(TAgentTypes.DUMMY_AGENT);
        TAgent agent = dummyAgentList.get(rand.nextInt(dummyAgentList.size()));
        agentManager.deleteAgent(agent);
        fAgentName = agent.getName();
    }

    @Override
    public String debugInfo() {
        return "spot:" + fSpotName + " agent:" + fAgentName;
    }
}
