package jp.soars.examples.sample05;

import java.util.ArrayList;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TSpot;
import jp.soars.core.TTime;

public class TAgentRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "AgentRole";

    /** ランダムに移動する */
    public static final String RANDOM = "RandomlyMoving";

    public TAgentRole(TAgent ownerAgent, TSpot initialSpot, ArrayList<TSpot> spotList) {
        super(ROLE_NAME, ownerAgent);
        TRamdomlyMovingRule rule = new TRamdomlyMovingRule(RANDOM, this, new TTime("18:00"), initialSpot, spotList,
                new TTime("0:15"), TStages.AGENT_MOVING, RANDOM);

        registerRule(rule);
        getRule(RANDOM).setTimeAndStage(8, 0, TStages.AGENT_MOVING);
    }
}
