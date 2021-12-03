package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRule;
import jp.soars.core.TRuleAggregator;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;
import jp.soars.utils.csv.TCCsvData;
import jp.soars.utils.random.ICRandom;

/**
 * 乗り物クラス
 */
public class TTransportation extends TSpot {
    /** 乗り物に関連するステージ */
    public class TStages {
        /** 始発のスポット集合への登録 */
        public static final String NEW_TRANSPORTATION = "NewTransportation";
        /** 到着 */
        public static final String TRANSPORTATION_ARRIVING = "TransportationArraiving";
        /** 出発 */
        public static final String TRANSPORTATION_LEAVING = "TransportationLeaving";
        /** 終着のスポット集合からの削除 */
        public static final String DELETING_TRANSPORTATION = "DeletingTransportation";
    }

    /** 現在地 */
    private String fCurrentSpotName;

    /** 駅に到着したことを知らせるルールの集合 */
    private HashMap<String, HashMap<String, TRule>> fRulesToNotifyThatTransportationArrivesAtStation = new HashMap<>();

    /** ルールが発火するステージ */
    private HashMap<TRule, String> fStageMap;
    /** 路線 */
    private String fLine;

    /** 方面 */
    private String fDirection;

    /** 乗り物の名前 */
    private String fTransportationName;

    /** 乗り物のタイプ */
    private String fType;

    /** 始発駅 */
    private String fSource;

    /** 出発時刻 */
    private TTime fDepartureTime;

    /** 終着駅 */
    private String fDestination;

    /** 到着時刻 */
    private TTime fArrivalTime;

    /** 運行中か？ */
    private boolean fInService;

    /**
     * コンストラクタ
     * 
     * @param line                      路線名
     * @param direction                 方向
     * @param transportationName        乗り物の名前
     * @param type                      乗り物のタイプ
     * @param source                    始発駅
     * @param departureTime             出発時刻
     * @param destination               終着駅
     * @param arrivalTime               到着時刻
     * @param schedule                  スケジュール
     * @param ruleAggregator            ルール管理
     * @param rand                      乱数
     * @param expectedMaxNumberOfAgents スポットにいる人数の最大値
     */
    public TTransportation(String line, String direction, String transportationName, String type, String source,
            TTime departureTime, String destination, TTime arrivalTime, TCCsvData schedule,
            TRuleAggregator ruleAggregator, ICRandom rand, int expectedMaxNumberOfAgents) {
        super(TTransportationManager.convertToSpotName(line, direction, transportationName), ruleAggregator, rand,
                expectedMaxNumberOfAgents);
        fLine = line;
        fDirection = direction;
        fTransportationName = transportationName;
        fType = type;
        fSource = source;
        fDepartureTime = departureTime;
        fDestination = destination;
        fArrivalTime = arrivalTime;
        fInService = false;
        for (int i = 0; i < schedule.getNoOfRows(); ++i) {
            fRulesToNotifyThatTransportationArrivesAtStation.put(schedule.getElement(i, "Station"),
                    new HashMap<String, TRule>());
        }
        fStageMap = new HashMap<TRule, String>();
    }

    /**
     * 乗り物が指定駅に到着したことを通知するルールを登録する． TGettingOffTransportationnRuleが登録されることを想定している．
     * 
     * @param rule            通知対象のルール
     * @param station         通知を行う駅．TGettingOffTransportationRuleを登録する場合は，降車駅を指定する．
     * @param agentName       ルールを登録するエージェント名
     * @param callBackMessage コールバックメッセージ
     */
    public void addRuleToNotify(TAgentRule rule, String station, String agentName, String stage,
            String callBackMessage) {
        fRulesToNotifyThatTransportationArrivesAtStation.get(station).put(agentName, rule);
        fStageMap.put(rule, stage);
    }

    /**
     * 通知対象のルールを削除する．
     * 
     * @param station   駅
     * @param agentName ルールを登録したエージェント
     */
    public void removeRule(String station, String agentName) {
        fRulesToNotifyThatTransportationArrivesAtStation.get(station).remove(agentName);
    }

    /**
     * 登録されているルールに，乗り物が駅に到着したことを通知する．
     *
     * @param station               到着駅
     * @param currentTime           現在時刻
     * @param currentStage          現在ステージ
     * @param spotSet               スポット集合
     * @param agentSet              エージェント集合
     * @param globalSharedVariables グローバル共有変数集合
     */
    public void notifyAllThatTransportationArrives(String station, TTime currentTime, String currentStage,
            HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        for (String agentName : fRulesToNotifyThatTransportationArrivesAtStation.get(fCurrentSpotName).keySet()) {
            fRulesToNotifyThatTransportationArrivesAtStation.get(fCurrentSpotName).get(agentName).setTimeAndStage(false,
                    currentTime,
                    fStageMap.get(
                            fRulesToNotifyThatTransportationArrivesAtStation.get(fCurrentSpotName).get(agentName)));
            fStageMap.remove(fRulesToNotifyThatTransportationArrivesAtStation.get(fCurrentSpotName).get(agentName));
            fRulesToNotifyThatTransportationArrivesAtStation.get(fCurrentSpotName).remove(agentName);
        }
    }

    /**
     * 現在位置するスポットの名前を返す． スポット名は，駅名または"駅名-駅名"となる．後者の場合は，駅と駅の間に位置することを示す．
     * 
     * @return スポット名
     */
    public String getCurrentSpotName() {
        return fCurrentSpotName;
    }

    /**
     * 引数のスポットに位置するか？
     * 
     * @param spotName スポット名
     * @return true:位置する，false:位置しない
     */
    public boolean isAt(String spotName) {
        return fCurrentSpotName.equals(spotName);
    }

    /**
     * 引数のスポットに移動する．
     * 
     * @param spotName スポット名
     */
    public void moveTo(String spotName) {
        fCurrentSpotName = spotName;
    }

    /**
     * 始発駅を返す．
     * 
     * @return 始発駅
     */
    public String getSource() {
        return fSource;
    }

    /**
     * 始発駅での出発時刻を返す．
     * 
     * @return 始発駅での出発時刻
     */
    public TTime getDepartureTimeAtSource() {
        return fDepartureTime;
    }

    /**
     * 終着駅を返す．
     * 
     * @return 終着駅
     */
    public String getDestination() {
        return fDestination;
    }

    /**
     * 終着駅への到着時刻を返す．
     * 
     * @return 終着駅への到着時刻
     */
    public TTime getArrivalTimeToDestination() {
        return fArrivalTime;
    }

    /**
     * 乗り物のタイプを返す．
     * 
     * @return 乗り物のタイプ
     */
    public String getType() {
        return fType;
    }

    /**
     * 路線名を返す．
     * 
     * @return 路線名
     */
    public String getLine() {
        return fLine;
    }

    /**
     * 方面を返す．
     * 
     * @return 方面
     */
    public String getDirection() {
        return fDirection;
    }

    /**
     * 乗り物の名前を返す．
     * 
     * @return 乗り物の名前
     */
    public String getTransportationName() {
        return fTransportationName;
    }

    /**
     * スポット名を返す．
     * 路線が"line1"，方面が"inbound"，乗り物の名前が"001"のとき，スポット名は"line1.inboud.001"となる．
     * 
     * @return スポット名
     */
    public String getSpotName() {
        return getName();
    }

    /**
     * 運行中か？
     * 
     * @return true:運行中，false:運行中でない
     */
    public boolean isInService() {
        return fInService;
    }

    /**
     * 運行フラグを設定する．
     * 
     * @param inService 運行フラグ
     */
    public void setInServiceFlag(boolean inService) {
        fInService = inService;
    }
}
