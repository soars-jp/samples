package jp.soars.examples.sample05;

import java.util.ArrayList;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

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
        // 親クラスのコンストラクタを呼び出す．
        TRandomlyMovingRule rule = new TRandomlyMovingRule(RANDOM, this, new TTime("18:00"), initialSpot.getName(),
                spotList, new TTime("0:15"));
        // 8時から18時までの間15分ごとに移動するルールを設定する
        registerRule(rule);
        getRule(RANDOM).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
    }
}
