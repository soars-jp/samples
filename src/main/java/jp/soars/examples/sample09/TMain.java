package jp.soars.examples.sample09;

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
import jp.soars.core.generator.IObjectGenerator;
import jp.soars.transportation.TTransportationAndStationGenerator;
import jp.soars.transportation.TTransportationStages;

/**
 * メインクラス．
 */
public class TMain {
    /**
     * スポットを生成する
     * 
     * @param spotManager スポット管理
     * @param noOfHomes   自宅数
     */
    private static void createSpots(TSpotManager spotManager, int noOfHomes) {
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes);
        spotManager.createSpots(TSpotTypes.COMPANY, 1);
        spotManager.createSpots(TSpotTypes.MIDWAY_SPOT, 1);
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfAgents   エージェント数
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfAgents) {
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfAgents);
        // それぞれの父親はstation2,station3,station4,..から通勤する
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1), "line1", "station" + (i + 2),
                    "station8");
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
        String logDir = "logs/sample09";
        // ステージとその実行順序の定義：
        // 始発列車のスポット集合への登録 => 列車到着 => エージェント移動 => 列車出発 => 終着列車のスポット集合からの削除
        List<String> stages = List.of(TTransportationStages.NEW_TRANSPORTATION,
                TTransportationStages.TRANSPORTATION_ARRIVING,
                TStages.AGENT_MOVING, TTransportationStages.TRANSPORTATION_LEAVING,
                TTransportationStages.DELETING_TRANSPORTATION);
        // モデルの生成
        int interval = 1; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        // スポットの初期化
        int noOfSpots = 5; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理
        createSpots(spotManager, noOfSpots);
        // noOfSpotsと同じ数の父親を生成する
        createFatherAgents(agentManager, spotManager, noOfSpots);

        IObjectGenerator generator = new TTransportationAndStationGenerator("transportationDB",
                model.getRuleAggregator(), true, model.getRandom());
        spotManager.createSpots(generator);
        // エージェントの初期化
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("2/0:00"); // シミュレーション終了時刻
        PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spot.csv");
        TTransportationLogger transportationLogger = new TTransportationLogger(
                logDir + File.separator + "transportationSpot.csv", spotManager);
        while (model.getTime().isLessThan(simulationPeriod)) {
            TTime t = model.getTime().clone();
            printWriter.print(t + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                printWriter.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
            transportationLogger.output(t);
        }
        printWriter.close();
        transportationLogger.close();
    }
}