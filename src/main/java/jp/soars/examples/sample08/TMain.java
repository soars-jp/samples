package jp.soars.examples.sample08;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.transportation.TTransportation;
import jp.soars.transportation.TTransportationManager;
import jp.soars.utils.random.ICRandom;
import jp.soars.utils.random.TCJava48BitLcg;

/**
 * メインクラス
 */
public class TMain {
    /**
     * スポットを生成する
     * 
     * @param spotManager
     * @param noOfHomes
     */
    private static void createSpots(TSpotManager spotManager, int noOfSpots) {
        spotManager.createSpots(TSpotTypes.HOME, noOfSpots);
        spotManager.createSpot(TSpotTypes.COMPANY);
        spotManager.createSpot(TSpotTypes.MIDWAY_SPOT);
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param args
     * @throws IOException
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager) {
        int noOfAgents = 1;
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfAgents);
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1));
            father.addRole(fatherRole);
            father.activateRole(fatherRole.getName());
            father.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
        }
    }

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample08";

        // ステージとその実行順序の定義：
        // 始発列車のスポット集合への登録 => 列車到着 => エージェント移動 => 列車出発 => 終着列車のスポット集合からの削除
        List<String> stages = List.of(TTransportation.TStages.NEW_TRANSPORTATION,
                TTransportation.TStages.TRANSPORTATION_ARRIVING,
                TStages.AGENT_MOVING, TTransportation.TStages.TRANSPORTATION_LEAVING,
                TTransportation.TStages.DELETING_TRANSPORTATION);
        // モデルの生成
        int interval = 1; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        // スポットの初期化
        int noOfSpots = 1; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots);
        createFatherAgents(agentManager, spotManager);
        /** スポットに滞在する人数の予測値 */
        int expectedMaxNumberOfAgents = 1;
        TTransportationManager transportationManager = new TTransportationManager("transportationDB", spotManager,
                model.getRuleAggregator(), model.getRandom(), false, expectedMaxNumberOfAgents);
        TTime simulationPeriod = new TTime("2/0:00"); // シミュレーション終了時刻
        PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spot.csv");
        while (model.getTime().isLessThan(simulationPeriod)) {
            printWriter.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                printWriter.print(a.getCurrentSpotName() + "\t"); //
                // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
        }
        printWriter.close();
    }
}