package jp.soars.examples.sample05;

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
 * 終了時刻まで指定された時間間隔でランダムに移動を繰り返し，終了時刻に初期スポットに移動するルール．
 */
public class TRandomlyMovingRule extends TAgentRule {
    /** 終了時刻 */
    private TTime fEndTime;

    /** ホームスポット */
    private String fHomeSpot;

    /** 出発地 */
    private String fSpot;

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** 次のルールを実行するまでの時間 */
    private TTime fTimeToNextRule;

    /** 2回目以降に繰り返し実行されるルール */
    private TRandomlyMovingRule fRepeatedRule;

    /** 次の実行時刻を計算するためのワークメモリ */
    private TTime fNextTime;

    /**
     * コンストラクタ
     * 
     * @param ruleName       このルールの名前
     * @param ownerRole      このルールを持つ役割
     * @param endTime        終了時刻
     * @param homeSpot       ホームスポット
     * @param spotList       ルールで移動する候補地
     * @param timeToNextRule 次のルールを実行するまでの時間
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, TTime endTime, String homeSpot,
            ArrayList<TSpot> spotList, TTime timeToNextRule) {
        super(ruleName, ownerRole);
        fEndTime = endTime;
        fHomeSpot = homeSpot;
        fSpotList = spotList;
        fSpot = homeSpot;
        fTimeToNextRule = timeToNextRule;
        fRepeatedRule = null;
        fNextTime = new TTime();
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {

        if (isAt(fSpot)) { // スポット条件が満たされたら
            if (currentTime.isEqualTo(fEndTime)) {// 終了時刻ならば
                moveTo(spotManager.getSpotDB().get(fHomeSpot));// ホームスポットへ移動する
            } else {
                ICRandom rand = getOwnerRole().getRandom();
                String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
                // ランダムに目的地を選択する
                moveTo(spotManager.getSpotDB().get(destination));
                // 目的地に移動する
                TRandomlyMovingRule r = this;
                // 自分が臨時実行ルールならば，次回実行するルールとして自分を使い回す．
                // 臨時実行ルールであればgetTime().isDailyTime()はfalseを返す
                if (getTime().isDailyTime()) {
                    if (fRepeatedRule == null) { // 次に実行するルールが定義されていたら
                        fRepeatedRule = new TRandomlyMovingRule(getName(), getOwnerRole(), fEndTime, fHomeSpot,
                                fSpotList, fTimeToNextRule);
                    }
                    r = fRepeatedRule;
                }
                r.setSpot(destination);// 現在の命令の目的地を次のルールの出発地にする
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                // 次回の発火時刻を設定
                r.setTimeAndStage(false, fNextTime, getStage()); // 臨時実行ルールとして予約
            }
        }
    }

    /**
     * 出発地を設定
     * 
     * @param spot 出発地
     */
    public void setSpot(String spot) {
        fSpot = spot;
    }
}
