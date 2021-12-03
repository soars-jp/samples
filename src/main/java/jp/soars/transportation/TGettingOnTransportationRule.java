package jp.soars.transportation;

import java.util.HashMap;
import java.util.Set;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 乗り物に乗るルール
 */
public class TGettingOnTransportationRule extends TAgentRule {
    /** 乗車駅 */
    private String fStation;

    /** 路線名 */
    private String fLine;

    /** 方面 */
    private String fDirection;

    /** 乗り物名 */
    private String fTransportationName;

    /** 乗り物のスポット名 */
    private String fSpotNameOfTransportation;

    /** 乗り物タイプ．複数可． */
    private Set<String> fTransportationTypes;

    /** 乗り物の行き先．複数可． */
    private Set<String> fTransportationDestinations;

    /** 次のルールを実行するステージ */
    private String fStageOfNextRule;

    /** 降車ルール名 */
    private String fNextRule;
    /** 乗り物タイプ，乗り物の行き先にANYを指定すると，全てのタイプ，行き先とマッチする． */
    public static final String ANY = "*";

    /**
     * コンストラクタ
     * 
     * @param ruleName           ルール名
     * @param ownerRole          ルールを保持する役割名
     * @param station            駅名
     * @param line               路線名
     * @param direction          行き先
     * @param transportationName 乗り物の名前
     */
    public TGettingOnTransportationRule(String ruleName, TRole ownerRole, String station, String line, String direction,
            String transportationName) {
        super(ruleName, ownerRole);
        fStation = station;
        fLine = line;
        fDirection = direction;
        fTransportationName = transportationName;
        fSpotNameOfTransportation = TTransportationManager.convertToSpotName(line, direction, transportationName);
        fTransportationTypes = null;
        fTransportationDestinations = null;
        fStageOfNextRule = null;
        fNextRule = null;
    }

    /**
     * コンストラクタ
     * 
     * @param ruleName        ルール名
     * @param ownerRole       ルールを保持する役割名
     * @param station         駅名
     * @param line            路線名
     * @param direction       行き先
     * @param stageOfNextRule 次のルールを発火させるまでのインターバル
     * @param nextRule        次のルール名
     */
    public TGettingOnTransportationRule(String ruleName, TRole ownerRole, String station, String line, String direction,
            Set<String> transportationTypes, Set<String> transportationDestinations, String stageOfNextRule,
            String nextRule) {
        super(ruleName, ownerRole);
        fStation = station;
        fLine = line;
        fDirection = direction;
        fTransportationName = null;
        fSpotNameOfTransportation = null;
        fTransportationTypes = transportationTypes;
        fTransportationDestinations = transportationDestinations;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        HashMap<String, TSpot> spotSet = spotManager.getSpotDB();

        if (isAt(fStation) && spotSet.containsKey(fSpotNameOfTransportation)
                && ((TTransportation) spotSet.get(fSpotNameOfTransportation)).isAt(fStation)) {
            getAgent().moveTo(spotSet.get(fSpotNameOfTransportation));
            if (fNextRule != null) {
                ((TGettingOffTransportationRule) getRule(fNextRule))
                        .setSpotNameOfTransportation(fSpotNameOfTransportation);
                ((TTransportation) spotSet.get(fSpotNameOfTransportation)).addRuleToNotify(
                        (TAgentRule) getRule(fNextRule),
                        ((TGettingOffTransportationRule) getRule(fNextRule)).getStation(), getAgent().getName(),
                        fStageOfNextRule, "");
            }
        }
    }

    /**
     * 乗車中乗り物のスポット名を返す．
     * 
     * @return 乗車中の乗り物のスポット名
     */
    public String getSpotNameOfTransportation() {
        return fSpotNameOfTransportation;
    }

    /**
     * 乗車中の乗り物の名前を返す．
     * 
     * @return 乗車中の乗り物の名前
     */
    public String getTransportationName() {
        return fTransportationName;
    }

    /**
     * 路線名を返す
     */
    public String getLine() {
        return fLine;
    }

    /**
     * 行き先を返す
     */
    public String getDirection() {
        return fDirection;
    }

    /**
     * 乗り物タイプ
     */
    public Set<String> getTypes() {
        return fTransportationTypes;
    }

    /**
     * 目的地タイプ
     */
    public Set<String> getDestinations() {
        return fTransportationDestinations;
    }

    public void setSpotNameOfTransportation(String transportationName) {
        fSpotNameOfTransportation = transportationName;
    }
}
