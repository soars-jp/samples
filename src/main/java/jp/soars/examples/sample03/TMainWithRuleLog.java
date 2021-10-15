package jp.soars.examples.sample03;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * デバッグ用ルールロガー出力ありのメインクラス． シミュレーションステップ：60分 シミュレーション期間：７日間
 * シナリオ：３人の父親エージェントが，毎日，9時に自宅を出発して会社に行き，17時に会社を出発して自宅に戻る．
 */
public class TMainWithRuleLog {

    /**
     * メインメソッド．
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // ステージの初期化
        List<String> stages = List.of(TStages.AGENT_MOVING); // ステージは，エージェント移動のみ．
        // モデルの生成
        int interval = 60; // １ステップの分数
        long seed = 0; // 乱数シード
        TModel model = new TModel(stages, interval, seed);
        model.beginRuleLogger("logs/sample02/ruleAction.csv"); // ルールの登録／発火／削除を記録するためのロガー（ルールロガー）を開始．
        // スポットの初期化
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes); // noOfHomes個の家スポットを生成する．名前は，home1, home2, ...となる．
        spotManager.createSpot(TSpotTypes.COMPANY); // 1個の会社スポットを生成する，名前は，company (=TSpots.COMPANY)となる．
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); // noOfHomes体の父親エージェントを生成する．名前は，father1,
                                                                                              // father2, ...となる．
        for (int i = 0; i < fathers.size(); ++i) {
            TAgent father = fathers.get(i); // i番目のエージェントを取り出す．
            String home = TSpotTypes.HOME + (i + 1); // i番目のエージェントの自宅のスポット名を生成する．
            TFatherRole fatherRole = new TFatherRole(father, home); // 父親役割を生成する．
            father.activateRole(fatherRole.getName()); // 父親役割をアクティブ化する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); // 初期位置を自宅に設定する．
        }
        // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : fathers) {
                System.out.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            System.out.println();
        }
        model.endRuleLogger(); // ルールロガーを終了する．
    }

}