package jp.soars.utils.transport;

import java.util.HashMap;

import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TGettingOffTransportRule extends TAgentRule {
    /** 降車駅 */
    private String fStation;

    /** 乗車中の乗り物のスポット名 */
    private String fSpotNameOfTransport;

    /** 次のルールを実行するまでの時間 */
    private TTime fTimeToNextRule;

    /** 次のルールを実行するステージ */
    private String fStageOfNextRule;

    /** 次に実行するルール名 */
    private String fNextRule;

    /** 次の実行時刻を計算するためのワークメモリ */
    private TTime fNextTime;

    /**
     * コンストラクタ．
     * 
     * @param ownerRole     このルールを持つ役割
     * @param station       降車駅
     * @param line          路線
     * @param direction     方面
     * @param transportName 乗り物の名前
     */
    public TGettingOffTransportRule(String ruleName, TRole ownerRole, String station, String line, String direction,
            String transportName) {
        super(ruleName, ownerRole);
        fStation = station;
        fSpotNameOfTransport = TTransportManager.convertToSpotName(line, direction, transportName);
        fTimeToNextRule = null;
        fStageOfNextRule = null;
        fNextRule = null;
        fNextTime = null;
    }

    /**
     * コンストラクタ
     * 
     * @param ruleName        ルール名
     * @param ownerRole       このルールを持つ役割
     * @param station         降車駅
     * @param timeToNextRule  次のルールを実行するまでの時間
     * @param stageOfNextRule 次のルールを実行するステージ
     * @param nextRule        次に実行するルール
     */
    public TGettingOffTransportRule(String ruleName, TRole ownerRole, String station, TTime timeToNextRule,
            String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fStation = station;
        fSpotNameOfTransport = null;
        fTimeToNextRule = timeToNextRule;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
        fNextTime = new TTime();
    }

    /**
     * コンストラクタ． ただし，発火条件を指定するためには，setRelativeTimeAndStageメソッドを使うこと．
     * ここで，引数のpreceedentRuleには対となるTGettingOnTransportRuleオブジェクト，relativeTimeには"0:00"を与えること．
     * 
     * @param ownerRole このルールをもつロール
     * @param station   降車駅
     */
    public TGettingOffTransportRule(String ruleName, TRole ownerRole, String station) {
        super(ruleName, ownerRole);
        fStation = station;
        fSpotNameOfTransport = null;
        fTimeToNextRule = null;
        fStageOfNextRule = null;
        fNextRule = null;
        fNextTime = null;
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        // 乗り物に乗っていて，乗り物が降車駅に着いたら，駅に移動する．
        HashMap<String, TSpot> spotSet = spotManager.getSpotDB();
        if (spotSet.containsKey(fSpotNameOfTransport)
                && ((TTransport) spotSet.get(fSpotNameOfTransport)).isAt(fStation)) {
            getAgent().moveTo(spotSet.get(fStation));
            if (fNextRule != null) { // 次に実行するルールが定義されていたら
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                getRule(fNextRule).setTimeAndStage(false, fNextTime, fStageOfNextRule);
            }
        }
    }

    /**
     * 駅名を取得する
     * 
     * @return
     */
    public String getStation() {
        return fStation;
    }

    /**
     * 乗り物の名前をセットする
     * 
     * @param transportName
     */
    public void setSpotNameOfTransport(String transportName) {
        fSpotNameOfTransport = transportName;
    }
}
