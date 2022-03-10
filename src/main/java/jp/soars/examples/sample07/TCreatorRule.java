package jp.soars.examples.sample07;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TCreatorRule extends TAgentRule {
    /** ルール名 */
    public static String RULE_NAME = "CreatorRule";

    /** デバッグ情報として作成したエージェント名とスポット名を出力する */
    private String fSpotName = "";
    private String fAgentName = "";

    /**
     * コンストラクタ
     * 
     * @param ownerRole ルールを持つ役割
     */
    public TCreatorRule(TRole ownerRole) {
        super(RULE_NAME, ownerRole);
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager,
            TAgentManager agentManager, HashMap<String, Object> globalSharedVariables) {
        // 新たなスポットとエージェントの作成
        TSpot newSpot = spotManager.createSpots(TSpotTypes.DUMMY_SPOT, 1).get(0);
        TAgent newAgent = agentManager.createAgents(TAgentTypes.DUMMY_AGENT, 1).get(0);
        TSpot newHome = spotManager.createSpots(TSpotTypes.HOME, 1).get(0);
        newAgent.initializeCurrentSpot(newHome);
        fSpotName = newSpot.getName();
        fAgentName = newAgent.getName();
        // スポット・エージェントの作成は定時実行できないので，次に発火する時刻を設定
        TTime nextTime = new TTime(currentTime).add("24:00"); // 24時間後に実行されるように設定
        this.setTimeAndStage(false, nextTime, getStage());
    }

    @Override
    public String debugInfo() {
        return "spot:" + fSpotName + " agent:" + fAgentName;
    }
}
