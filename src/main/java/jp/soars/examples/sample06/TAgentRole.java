package jp.soars.examples.sample06;

import java.util.ArrayList;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

/**
 * エージェント役割
 */
public class TAgentRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "AgentRole";

    /** ランダムに移動する */
    public static final String RANDOM = "RandomlyMoving";

    /**
     * コンストラクタ
     * 
     * @param ownerAgent  この役割を持つエージェント
     * @param initialSpot 初期スポット
     * @param spotList    全スポットのリスト
     */
    public TAgentRole(TAgent ownerAgent, TSpot initialSpot, ArrayList<TSpot> spotList) {
        super(ROLE_NAME, ownerAgent);
        TRandomlyMovingRule rule = new TRandomlyMovingRule(RANDOM, this, new TTime("18:00"), initialSpot.getName(),
                spotList);

        registerRule(rule);
        getRule(RANDOM).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
    }
}
