package jp.soars.examples.sample11;

import java.util.ArrayList;
import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.utils.random.ICRandom;

/**
 * 終了時刻までレイヤー内のスポットをランダムに移動を繰り返すルール．
 */
public class TRandomlyMovingRule extends TAgentRule {

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** レイヤー名 */
    private String fLayerName;

    /**
     * コンストラクタ
     *
     * @param ruleName       このルールの名前
     * @param ownerRole      このルールを持つ役割
     * @param spotList       ルールで移動する候補地
     * @param layerName      このルールで指定するレイヤー名
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, ArrayList<TSpot> spotList, String layerName) {
        super(ruleName, ownerRole);
        fSpotList = spotList;
        fLayerName = layerName;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        ICRandom rand = getOwnerRole().getRandom();
        String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
        // ランダムに目的地を選択する
        moveTo(spotManager.getSpotDB(fLayerName).get(destination));
    }

}
