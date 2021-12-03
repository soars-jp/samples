package jp.soars.examples.sample07;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;

public class TKillerRole extends TRole{
    /** 役割名 */
	public static final String ROLE_NAME = "KillerRole";

    public TKillerRole(TAgent ownerAgent){
        super(ROLE_NAME, ownerAgent);
        //ルールの作成
        TKillerRule r = new TKillerRule(this);
        //発火時刻の設定とルール収集器に登録，16時の定時実行ルールとする．
        r.setTimeAndStage(16, 0, TStages.DYNAMIC_REMOVAL);
        //役割クラスのルール集合に登録
        registerRule(r);
    }
}
