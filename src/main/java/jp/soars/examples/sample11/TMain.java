package jp.soars.examples.sample11;

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

/**
 * メインクラス
 */
public class TMain {

    /**
     * スポットを生成する
     *
     * @param spotManager スポット管理
     * @param noOfHomes   スポット数
     * @param layers      レイヤのリスト
     */
    private static void createSpots(TSpotManager spotManager, int noOfSpots, List<String> layers) {
        for (String layerName : layers) {
            spotManager.createSpots(TSpotTypes.SPOT, noOfSpots, layerName);
            // 指定したレイヤ（layerName）に対して，noOfSpots個のスポットを生成
        }
    }

    /**
     * メインメソッド．
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ログを収集するディレクトリ
        String logDir = "logs/sample11";
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 10; // １ステップの分数
        long seed = 0; // 乱数シード
        List<String> layers = List.of("layer1", "layer2");
        TModel model = new TModel(stages, interval, seed, layers);
        model.getRuleAggregator().makeStageAlwaysExecuted(TStages.AGENT_MOVING);
        int noOfSpots = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfSpots, layers);// スポットの初期化

        // エージェントの初期化
        int noOfAgents = 2;// エージェント数
        ArrayList<TAgent> agents = agentManager.createAgents(TAgentTypes.AGENT, noOfAgents);
        // エージェント管理
        for (int i = 0; i < agents.size(); i++) {
            String layerName = layers.get(i);// i番目のレイヤを取り出す．
            ArrayList<TSpot> spotList = spotManager.getSpotLayers(layerName);
            TAgent agent = agents.get(i);// i番目のエージェントを取り出す．
            TSpot initialSpot = spotList.get(model.getRandom().nextInt(spotList.size()));
            // 初期スポットをランダムに選ぶ．
            TAgentRole agentRole = new TAgentRole(agent, initialSpot, spotList, layerName);
            // エージェント役割を生成する．
            agent.addRole(agentRole);// エージェント役割を設定する．
            agent.activateRole(agentRole.getName());// エージェント役割を有効にする．
            agent.initializeCurrentSpot(initialSpot); // 初期位置を設定する．
        }
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("2/0:00"); // シミュレーション終了時刻
        PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spot.csv");
        while (model.getTime().isLessThan(simulationPeriod)) {
            printWriter.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                printWriter.print(a.getCurrentSpotName() + ","); //
                // 各エージェントが位置しているスポット名を表示する．
            }
            printWriter.println();
        }
        printWriter.close();
    }
}
