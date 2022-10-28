package jp.soars.examples.sample10;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

public class TMain {

        public static void main(String[] args) throws IOException {
                // ログディレクトリへのパス
                String logDir = "logs/sample10";
                // ステージの初期化
                List<String> stages = List.of(TStages.AGENT_MOVING, TStages.AGGREGATION); // ステージは，エージェント移動と集計．
                // 乱数発生器
                int interval = 60; // １ステップの分数
                long seed = 0; // シード値
                TModel model = new TModel(stages, interval, seed);
                // 集計ステージを定期実行ステージとして登録
                model.getRuleAggregator().makeStageAlwaysExecuted(TStages.AGGREGATION);
                model.beginRuleLogger(logDir + File.separator + "ruleLog.csv");// ルールログを開始する．
                // 初期値設定
                model.getGlobalSharedVariableSet().put(TRuleOfAggregation.HOME_KEY, 0);
                model.getGlobalSharedVariableSet().put(TRuleOfAggregation.WORKPLACE_KEY, 0);
                // スポットの初期化
                int noOfHomes = 100_000; // 家の数
                int noOfCompany = 100; // 会社の数
                TSpotManager spotManager = model.getSpotManager(); // スポット管理
                spotManager.createSpots(TSpotTypes.HOME, noOfHomes); // noOfHomes個の家スポットを生成する．名前は，home1, home2, //
                                                                     // home3となる．
                spotManager.createSpots(TSpotTypes.COMPANY, noOfCompany); // 1000個の会社スポットを生成する，名前は，company1, company2,
                                                                          // company3 となる
                // エージェントの初期化
                TAgentManager agentManager = model.getAgentManager(); // エージェント管理
                ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes); // noOfHomes体の父親エージェントを生成する．
                for (int i = 0; i < fathers.size(); ++i) {
                        TAgent father = fathers.get(i); // i番目のエージェントを取り出す．
                        String home = TSpotTypes.HOME + (i + 1); // i番目のエージェントの自宅のスポット名を生成する．
                        String workplace = TSpotTypes.COMPANY + ((i % noOfCompany) + 1);
                        TFatherRole fatherRole = new TFatherRole(father, home, workplace); // 父親役割を生成する．
                        father.activateRole(fatherRole.getName()); // 父親役割をアクティブ化する．
                        father.initializeCurrentSpot(spotManager.getSpotDB().get(home)); // 初期位置を自宅に設定する．
                }

                // スポットログ用PrintWriter
                PrintWriter printWriter = new PrintWriter(logDir + File.separator + "spotLog.csv");
                printWriter.print("CurrentTime");
                for (int i = 0; i < agentManager.getAgents().size(); i += 10) {
                        TAgent agent = agentManager.getAgents().get(i);
                        printWriter.print("," + agent.getName());
                }
                printWriter.println();

                // グローバル共有変数のログ用PrintWriter
                String pathOfGlobalSharedVariableSetLog = logDir + File.separator
                                + "globalSharedVariableSetLog.csv";
                PrintWriter globalSharedVariableSetLogPW = new PrintWriter(
                                new BufferedWriter(new FileWriter(pathOfGlobalSharedVariableSetLog)));
                globalSharedVariableSetLogPW.println("CurrentTime," +
                                TRuleOfAggregation.HOME_KEY + "," +
                                TRuleOfAggregation.WORKPLACE_KEY);

                // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
                TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
                while (model.getTime().isLessThan(simulationPeriod)) {
                        TTime currentTime = model.getTime();// 時刻を表示する．
                        printWriter.print(currentTime);
                        globalSharedVariableSetLogPW.print(currentTime);
                        model.execute();// モデルの実行
                        for (int i = 0; i < agentManager.getAgents().size(); i += 10) {
                                TAgent agent = agentManager.getAgents().get(i);
                                printWriter.print("," + agent.getCurrentSpotName());// 各エージェントが位置しているスポット名を表示する．
                        }
                        printWriter.println();
                        // グローバル共有変数のログ出力
                        globalSharedVariableSetLogPW.println("," +
                                        model.getGlobalSharedVariableSet().get(TRuleOfAggregation.HOME_KEY) + "," +
                                        model.getGlobalSharedVariableSet().get(TRuleOfAggregation.WORKPLACE_KEY));

                }
                printWriter.close();// スポットログを終了する．
                globalSharedVariableSetLogPW.close();
                model.endRuleLogger();// ルールログを終了する．
        }
}
