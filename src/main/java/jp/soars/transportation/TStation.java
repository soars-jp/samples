package jp.soars.transportation;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TRule;
import jp.soars.core.TRuleAggregator;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;
import jp.soars.utils.random.ICRandom;

/**
 * 駅クラス
 */
public class TStation extends TSpot {
    /** エージェント名をキー，駅に乗り物が到着した際に通知するルールを値とするハッシュマップ */
    private HashMap<String, TGettingOnTransportationRule> fRulesToNotifyThatTransportationArrives;

    /** ルールが発火するステージ */
    private HashMap<TRule, String> fStageMap;

    /**
     * コンストラクタ
     * 
     * @param name                      駅名
     * @param ruleAggregator            ルール収集器
     * @param random                    乱数発生器
     * @param expectedMaxNumberOfAgents このスポットに同時に滞在する最大エージェント数の予測値
     */
    public TStation(String name, TRuleAggregator ruleAggregator, ICRandom random, int expectedMaxNumberOfAgents) {
        super(name, ruleAggregator, random, expectedMaxNumberOfAgents);
        fRulesToNotifyThatTransportationArrives = new HashMap<>();
        fStageMap = new HashMap<TRule, String>();
    }

    /**
     * 乗り物が到着したことを通知するルールを登録する．
     * 
     * @param agentName エージェント名
     * @param rule      ルール
     * @param stage     ステージ名
     */
    public void addRule(String agentName, TGettingOnTransportationRule rule, String stage) {
        fRulesToNotifyThatTransportationArrives.put(agentName, rule);
        fStageMap.put(rule, stage);
    }

    /**
     * 指定したエージェントが登録しているルールを削除する．
     * 
     * @param agentName
     */
    public void removeRule(String agentName) {
        fRulesToNotifyThatTransportationArrives.remove(agentName);
    }

    /**
     * 乗り物が到着したことを，乗客と駅にいる人に通知する．
     * 
     * @param notifier              到着した乗り物
     * @param currentTime           現在時刻
     * @param currentStage          現在ステージ
     * @param spotSet               スポット集合
     * @param agentSet              エージェント集合
     * @param globalSharedVariables グローバル共有変数集合
     */
    public void notifyAllThatTransportationArrives(TTransportation notifier, TTime currentTime,
            String currentStage,
            HashMap<String, TSpot> spotSet, HashMap<String, TAgent> agentSet,
            HashMap<String, Object> globalSharedVariables) {
        for (String agentName : fRulesToNotifyThatTransportationArrives.keySet()) {
            TGettingOnTransportationRule rule = fRulesToNotifyThatTransportationArrives.get(agentName);
            String line = notifier.getLine(); // 路線
            String direction = notifier.getDirection(); // 方面
            String type = notifier.getType(); // タイプ
            String destination = notifier.getDestination(); // 行き先
            // 路線名，方向，種類，目的地が一致していれば，乗車ルールを同時刻のAgentMovingステージに登録する
            if (rule.getLine().equals(line) && rule.getDirection().equals(direction)
                    && (rule.getTypes().contains(TGettingOnTransportationRule.ANY)
                            || rule.getTypes().contains(type))
                    && (rule.getDestinations().contains(TGettingOnTransportationRule.ANY)
                            || rule.getDestinations().contains(destination))) {
                rule.setSpotNameOfTransportation(notifier.getName());
                rule.setTimeAndStage(false, currentTime, "AgentMoving");
                fRulesToNotifyThatTransportationArrives.remove(agentName);
            }
        }
    }
}
