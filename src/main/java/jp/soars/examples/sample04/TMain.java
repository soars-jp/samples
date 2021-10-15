package jp.soars.examples.sample04;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jdk.nashorn.api.tree.SpreadTree;
import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

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
    private static void createSpots(TSpotManager spotManager, int noOfHomes) {
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes);
        spotManager.createSpot(TSpotTypes.COMPANY);
        spotManager.createSpot(TSpotTypes.SCHOOL);
        spotManager.createSpot(TSpotTypes.HOSPITAL);
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param agentManager
     * @param spotManager
     * @param noOfHomes
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);
            TCommonRole commonRole = new TCommonRole(father, TSpotTypes.HOME + (i + 1));
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1)); // 父親役割を生成する．
            fatherRole.addChildRole(commonRole);
            father.addRole(fatherRole);
            father.setBaseRole(fatherRole);
            father.activateRole(fatherRole.getName());
            TSickPersonRole sickPersonRole = new TSickPersonRole(father, TSpotTypes.HOME + (i + 1), 3);
            father.addRole(sickPersonRole);
            father.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
        }
    }

    /**
     * 子供エージェントを生成する
     * 
     * @param args
     * @throws IOException
     */
    private static void createChildAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> children = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        for (int i = 0; i < children.size(); i++) {
            TAgent child = children.get(i);
            TCommonRole commonRole = new TCommonRole(child, TSpotTypes.HOME + (i + 1));
            TChildRole childRole = new TChildRole(child, TSpotTypes.HOME + (i + 1)); // 父親役割を生成する．
            childRole.addChildRole(commonRole);
            child.addRole(childRole);
            child.setBaseRole(childRole);
            child.activateRole(childRole.getName());
            TSickPersonRole sickPersonRole = new TSickPersonRole(child, TSpotTypes.HOME + (i + 1), 2);
            child.addRole(sickPersonRole);
            child.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
        }
    }

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
        // スポットの初期化
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        // エージェントの初期化
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfHomes);
        createFatherAgents(agentManager, spotManager, noOfHomes);
        createChildAgents(agentManager, spotManager, noOfHomes);
        // メインループ： 0日0時0分から6日23時まで1時間単位でまわす．
        TTime simulationPeriod = new TTime("7/0:00"); // シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); // 時刻を表示する．
            model.execute(); // モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                System.out.print(a.getCurrentSpotName() + "\t"); // 各エージェントが位置しているスポット名を表示する．
            }
            System.out.println();
        }
    }

}