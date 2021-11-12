package jp.soars.utils.transport;

import java.util.HashMap;
import java.util.Set;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TGettingOnTransportRule extends TAgentRule {
    /** 乗車駅 */
    private String fStation;

    /** 路線名 */
    private String fLine;

    /** 方面 */
    private String fDirection;

    /** 乗り物名 */
    private String fTransportName;

    /** 乗り物のスポット名 */
    private String fSpotNameOfTransport;

    /** 乗り物タイプ．複数可． */
    private Set<String> fTransportTypes;

    /** 乗り物の行き先．複数可． */
    private Set<String> fTransportDestinations;

    /** 次のルールを実行するステージ */
    private String fStageOfNextRule;

    /** 降車ルール名 */
    private String fNextRule;
    /** 乗り物タイプ，乗り物の行き先にANYを指定すると，全てのタイプ，行き先とマッチする． */
    public static final String ANY = "*";

    /**
     * コンストラクタ
     * 
     * @param ruleName      ルール名
     * @param ownerRole     ルールを保持する役割名
     * @param station       駅名
     * @param line          路線名
     * @param direction     行き先
     * @param transportName 乗り物の名前
     */
    public TGettingOnTransportRule(String ruleName, TRole ownerRole, String station, String line, String direction,
            String transportName) {
        super(ruleName, ownerRole);
        fStation = station;
        fLine = line;
        fDirection = direction;
        fTransportName = transportName;
        fSpotNameOfTransport = TTransportManager.convertToSpotName(line, direction, transportName);
        fTransportTypes = null;
        fTransportDestinations = null;
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
    public TGettingOnTransportRule(String ruleName, TRole ownerRole, String station, String line, String direction,
            Set<String> transportTypes, Set<String> transportDestinations, String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fStation = station;
        fLine = line;
        fDirection = direction;
        fTransportName = null;
        fSpotNameOfTransport = null;
        fTransportTypes = transportTypes;
        fTransportDestinations = transportDestinations;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        HashMap<String, TSpot> spotSet = spotManager.getSpotDB();

        if (isAt(fStation) && spotSet.containsKey(fSpotNameOfTransport)
                && ((TTransport) spotSet.get(fSpotNameOfTransport)).isAt(fStation)) {
            getAgent().moveTo(spotSet.get(fSpotNameOfTransport));
            if (fNextRule != null) {
                ((TGettingOffTransportRule) getRule(fNextRule)).setSpotNameOfTransport(fSpotNameOfTransport);
                ((TTransport) spotSet.get(fSpotNameOfTransport)).addRuleToNotify((TAgentRule) getRule(fNextRule),
                        ((TGettingOffTransportRule) getRule(fNextRule)).getStation(), getAgent().getName(),
                        fStageOfNextRule, "");
            }
        }
    }

    /**
     * 乗車中乗り物のスポット名を返す．
     * 
     * @return 乗車中の乗り物のスポット名
     */
    public String getSpotNameOfTransport() {
        return fSpotNameOfTransport;
    }

    /**
     * 乗車中の乗り物の名前を返す．
     * 
     * @return 乗車中の乗り物の名前
     */
    public String getTransportName() {
        return fTransportName;
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
        return fTransportTypes;
    }

    /**
     * 目的地タイプ
     */
    public Set<String> getDestinations() {
        return fTransportDestinations;
    }

    public void setSpotNameOfTransport(String transportName) {
        fSpotNameOfTransport = transportName;
    }
}
