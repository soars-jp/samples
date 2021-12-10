package jp.soars.examples.sample06;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.utils.random.ICRandom;
import jp.soars.utils.random.TCJava48BitLcg;

/**
 * メインクラス．
 */
public class TMain {

    /**
     * スポットを生成する
     * 
     * @param spotManager
     * @param noOfHomes
     */
    private static void createSpots(TSpotManager spotManager, int noOfSpots) {
        spotManager.createSpots(TSpotTypes.SPOT, noOfSpots);
    }

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample06";
        // 乱数生成
        ICRandom rand = new TCJava48BitLcg();
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 15; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        // スポットの初期化
        int noOfSpots = 10; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots);
        ArrayList<TSpot> spotList = spotManager.getSpots();

        // エージェントの初期化
        int noOfAgents = 300;
        ArrayList<TAgent> agents = agentManager.createAgents(TAgentTypes.AGENT, noOfAgents);
        for (int i = 0; i < agents.size(); i++) {
            TAgent agent = agents.get(i);
            TSpot initialSpot = spotList.get(rand.nextInt(spotList.size()));
            TAgentRole agentRole = new TAgentRole(agent, initialSpot, spotList);
            agent.addRole(agentRole);
            agent.activateRole(agentRole.getName());
            agent.initializeCurrentSpot(initialSpot);
        }
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("3/0:00"); // シミュレーション終了時刻
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