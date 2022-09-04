package jp.soars.examples.sample09;

import java.util.HashMap;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TAgentRule;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * 移動ルール．
 */
public class TRuleOfMoving extends TAgentRule {

    /** 出発地 */
    private String fSource;

    /** 目的地 */
    private String fDestination;

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
     * @param ruleName        このルールの名前
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     */
    public TRuleOfMoving(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fTimeToNextRule = null;
        fStageOfNextRule = null;
        fNextRule = null;
        fNextTime = null;
    }

    /**
     * コンストラクタ．
     * 
     * @param ruleName        このルールの名前
     * @param ownerRole       このルールをもつ役割
     * @param sourceSpot      出発地
     * @param destinationSpot 目的地
     * @param timeToNextRule  次のルールを実行するまでの時間
     * @param stageOfNextRule 次のルールを実行するステージ
     * @param nextRule        次に実行するルール
     */
    public TRuleOfMoving(String ruleName, TRole ownerRole, String sourceSpot, String destinationSpot,
            TTime timeToNextRule, String stageOfNextRule, String nextRule) {
        super(ruleName, ownerRole);
        fSource = sourceSpot;
        fDestination = destinationSpot;
        fTimeToNextRule = timeToNextRule;
        fStageOfNextRule = stageOfNextRule;
        fNextRule = nextRule;
        fNextTime = new TTime();
    }

    @Override
    public void doIt(TTime currentTime, String stage, TSpotManager spotManager, TAgentManager agentManager,
            HashMap<String, Object> globalSharedVariables) {
        if (isAt(fSource)) { // 出発地にいたら，
            moveTo(spotManager.getSpotDB().get(fDestination)); // 目的地へ移動する．
            if (fNextRule != null) { // 次に実行するルールが定義されていたら
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                getRule(fNextRule).setTimeAndStage(false, fNextTime, fStageOfNextRule);
            }
        }
    }

}
