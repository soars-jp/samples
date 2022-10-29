package jp.soars.examples.sample11;

import java.util.ArrayList;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;

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
    public TAgentRole(TAgent ownerAgent, TSpot initialSpot, ArrayList<TSpot> spotList, String layerName) {
        super(ROLE_NAME, ownerAgent);
        // 親クラスのコンストラクタを呼び出す．
        registerRule(new TRandomlyMovingRule(RANDOM, this, spotList, layerName));
        getRule(RANDOM).setStage(TStages.AGENT_MOVING);
    }
}
