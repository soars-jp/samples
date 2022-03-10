package jp.soars.examples.sample07;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

public class TCreatorRole extends TRole {
    /** 役割名 */
    public static final String ROLE_NAME = "CreatorRole";

    /**
     * 生成役割
     * 
     * @param ownerAgent 役割を持つエージェント
     */
    public TCreatorRole(TAgent ownerAgent) {
        super(ROLE_NAME, ownerAgent);
        // ルールの作成
        TCreatorRule r = new TCreatorRule(this);
        // スポット・エージェントの作成は定時実行ルールにすることはできない．初回実行は0日目8時とする
        r.setTimeAndStage(0, 8, 0, TStages.DYNAMIC_ADDITION);
        // 役割クラスのルール集合に登録
        registerRule(r);
    }
}
