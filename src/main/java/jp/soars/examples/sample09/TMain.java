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
import jp.soars.utils.random.ICRandom;
import jp.soars.utils.random.TCJava48BitLcg;
import jp.soars.utils.transport.TTransport;
import jp.soars.utils.transport.TTransportManager;

/**
 * メインクラス． シミュレーションステップ：60分 シミュレーション期間：７日間
 * シナリオ：３人の父親エージェントが，毎日，9時に自宅を出発して会社に行き，17時に会社を出発して自宅に戻る．
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
        String logDir = "logs/sample09";
        // 乱数生成
        ICRandom rand = new TCJava48BitLcg();
        // ステージとその実行順序の定義：
        // 始発列車のスポット集合への登録 => 列車到着 => エージェント移動 => 列車出発 => 終着列車のスポット集合からの削除
        List<String> stages = List.of(TTransport.TStages.NEW_TRANSPORT, TTransport.TStages.TRANSPORT_ARRIVING,
                TStages.AGENT_MOVING, TTransport.TStages.TRANSPORT_LEAVING, TTransport.TStages.DELETING_TRANSPORT);
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
        int expectedMaxNumberOfAgents = 3;
        TTransportManager transportManager = new TTransportManager("transportDB", spotManager,
                model.getRuleAggregator(), rand, expectedMaxNumberOfAgents);
        // エージェントの初期化
        // メインループ： 0日0時0分から3日23時まで1時間単位でまわす．
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