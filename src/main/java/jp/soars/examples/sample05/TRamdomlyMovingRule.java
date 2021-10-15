package jp.soars.examples.sample05;

import java.util.ArrayList;
import java.util.HashMap;
import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;
import jp.soars.utils.random.ICRandom;

public class TRamdomlyMovingRule extends TAgentRule {
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

    /** 次のルールを実行するステージ */
    private String fStageOfNextRule;

    /** 次に実行するルール名 */
    private String fNextRule;

    /**
     * コンストラクタ
     * 
     * @param ruleName
     * @param ownerRole
     * @param endTime
     * @param intervalTime
     * @param homeSpot
     * @param timeToNextRule
     * @param stageOfNextRule
     * @param nextRule
     */
    public TRamdomlyMovingRule(String ruleName, TRole ownerRole, TTime endTime, TSpot homeSpot,
            ArrayList<TSpot> spotList, TTime timeToNextRule, String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fEndTime = endTime;
        fHomeSpot = homeSpot.getName();
        fSpotList = spotList;
        fNextRule = nextRule;
        fSpot = homeSpot.getName();
        fTimeToNextRule = timeToNextRule;
        fStageOfNextRule = stageOfNextRule;
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, HashMap<String, TSpot> spotSet,
            HashMap<String, TAgent> agentSet, HashMap<String, Object> globalSharedVariables) {
        System.out.println(fSpot);

        if (isAt(fSpot)) { // スポット条件が満たされたら
            if (currentTime.isEqualTo(fEndTime)) {
                moveTo(spotSet.get(fHomeSpot));
                fSpot = fHomeSpot;
            } else {
                ICRandom rand = getOwnerRole().getRandom();
                String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
                moveTo(spotSet.get(destination));
                fSpot = destination;
                if (fNextRule != null) { // 次に実行するルールが定義されていたら
                    /*
                     * int day = currentTime.getDay() + fTimeToNextRule.getDay(); // 次のルールを実行する日 int
                     * hour = currentTime.getHour() + fTimeToNextRule.getHour(); // 次のルールを実行する時間 int
                     * minute = currentTime.getMinute() + fTimeToNextRule.getMinute(); //
                     * 次のルールを実行する分 if (minute >= 60) { hour += minute / 60; minute %= 60; } if (hour
                     * >= 24) { day += hour / 24; hour %= 24; }
                     */
                    TTime nextTime = currentTime.add(fTimeToNextRule);
                    System.out.println(nextTime.toString());
                    getRule(fNextRule).setTimeAndStage(nextTime.getDay(), nextTime.getHour(), nextTime.getMinute(),
                            fStageOfNextRule); // 臨時実行ルールとして予約
                }
            }
        }
    }
}
