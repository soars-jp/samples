package jp.soars.examples.ex01;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TMessage;
import jp.soars.core.TModel;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * メインクラス
 */
public class TMain {

    /**
     * メインメソッド
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        TMessage.setWarningFlag(true); // Warningの出力
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        int interval = 60;
        long seed = 0L;
        TModel model = new TModel(stages, interval, seed);
        model.beginRuleLogger("logs/ex01/rule_log.csv"); // ルールロガーを開始する．
        // スポットの初期化
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes, 10);
        spotManager.createSpots(TSpotTypes.COMPANY, 10);
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); // noOfHomes体の父親エージェントを生成する．名前は，father1,
                                                                                              // father2, ...となる．
        for (int i = 0; i < fathers.size(); ++i) {
            TAgent father = fathers.get(i); // i番目のエージェントを取り出す．
            String home = TSpotTypes.HOME + (i + 1); // i番目のエージェントの自宅のスポット名を生成する．
            TFatherRole fatherRole = new TFatherRole(father, home); // 父親役割を生成する．デフォルトでは，9時に出社，8時間後に帰宅．
            if (i == 1) { // 1番目のエージェントだけ，4時間後に帰宅となるように，RULE_GO_TO_COMPANYを上書きする．
                TRuleOfLeavingHome ruleOfGoingToCompany = new TRuleOfLeavingHome(TFatherRole.RULE_GO_TO_COMPANY,
                        fatherRole, home, TSpotTypes.COMPANY, 4);
                fatherRole.overwriteRule(ruleOfGoingToCompany);
            }
            father.activateRole(fatherRole.getName()); // 父親役割をアクティブ化する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); // 初期位置を自宅に設定する．
        }
        // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00");
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + " ");
            model.execute(); // モデルの実行
            for (TAgent a : fathers) {
                System.out.print(a.getCurrentSpotName() + " ");
            }
            System.out.println();
        }
        model.endRuleLogger(); // ルールロガーを終了する．
    }

}