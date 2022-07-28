package jp.soars.examples.sample06;

import java.util.ArrayList;
import java.util.Map;

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

    /** 出発スポット */
    private String fSpot;

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** 次のルールを実行するまでの時間 */
    private TTime fTimeToNextRule;

    /** 次に実行するルール */
    private TRandomlyMovingRule fRepeatedRule;

    /** 次の実行時刻を計算するためのワークメモリ */
    private TTime fNextTime;

    /**
     * コンストラクタ
     * 
     * @param ruleName  このルールの名前
     * @param ownerRole このルールを持つ役割
     * @param endTime   終了時刻
     * @param homeSpot  ホームスポット
     * @param spotList  ルールで移動する候補地
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, TTime endTime, String homeSpot,
            ArrayList<TSpot> spotList) {
        super(ruleName, ownerRole);
        fEndTime = endTime;
        fHomeSpot = homeSpot;
        fSpotList = spotList;
        fSpot = homeSpot;
        fTimeToNextRule = new TTime();
        fRepeatedRule = null;
        fNextTime = new TTime();
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, TSpotManager spotManager, TAgentManager agentManager,
            Map<String, Object> globalSharedVariables) {

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
                                fSpotList);
                    }
                    r = fRepeatedRule;
                }
                r.setSpot(destination);// 現在の命令の目的地を次のルールの出発地にする
                fTimeToNextRule.initialize(0, rand.nextInt(1, 3), 0);
                // 次回の発火時刻を1時間後から3時間後にランダムに設定
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                // 終了時刻を超えていたら終了時刻に実行する
                if (fNextTime.getHour() > fEndTime.getHour() || (fNextTime.getHour() == fEndTime.getHour()
                        && fNextTime.getMinute() > fEndTime.getMinute())) {
                    fNextTime.initialize(fNextTime.getDay(), fEndTime.getHour(), fEndTime.getMinute());
                }
                r.setTimeAndStage(false, fNextTime, getStage()); // 臨時実行ルールとして予約
            }
        }
    }

    /**
     * 出発地を設定
     * 
     * @param spot 出発地
     */
    public void setSpot(String destination) {
        fSpot = destination;
    }
}
