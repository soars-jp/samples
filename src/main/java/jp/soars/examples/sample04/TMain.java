package jp.soars.examples.sample04;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * @param spotManager スポット管理
     * @param noOfHomes   自宅の数
     */
    private static void createSpots(TSpotManager spotManager, int noOfHomes) {
        spotManager.createSpots(TSpotTypes.HOME, noOfHomes);// noOfHomes個の家スポットを生成する．名前は，home1, home2, ...となる．
        spotManager.createSpot(TSpotTypes.COMPANY);// 1個の会社スポットを生成する，名前は，company (=TSpots.COMPANY)となる．
        spotManager.createSpot(TSpotTypes.SCHOOL);// 1個の学校スポットを生成する．
        spotManager.createSpot(TSpotTypes.HOSPITAL);// 1個の病院スポットを生成する．
    }

    /**
     * 父親エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfHomes    自宅数
     */
    private static void createFatherAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> fathers = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        // noOfHomes体の父親エージェントを生成する．名前は，father1,father2, ...となる．
        for (int i = 0; i < fathers.size(); i++) {
            TAgent father = fathers.get(i);// i番目のエージェントを取り出す．
            TCommonRole commonRole = new TCommonRole(father, TSpotTypes.HOME + (i + 1));
            // 共通役割を生成する．
            TFatherRole fatherRole = new TFatherRole(father, TSpotTypes.HOME + (i + 1)); // 父親役割を生成する．
            // 父親役割を生成する．
            fatherRole.addChildRole(commonRole);// 共通役割を設定する．
            father.addRole(fatherRole);// 父親役割を設定する．
            father.activateRole(fatherRole.getName());// 父親役割を有効にする
            TSickPersonRole sickPersonRole = new TSickPersonRole(father, TSpotTypes.HOME + (i + 1), 2,
                    fatherRole.getName());
            // 病人役割を生成する．診察時間は2時間とする．
            father.addRole(sickPersonRole);
            // 病人役割を追加する．
            father.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
            // i+1番目の父親エージェントの初期位置を，i+1番目の家スポットに設定する．
        }
    }

    /**
     * 子供エージェントを生成する
     * 
     * @param agentManager エージェント管理
     * @param spotManager  スポット管理
     * @param noOfHomes    自宅数
     */
    private static void createChildAgents(TAgentManager agentManager, TSpotManager spotManager, int noOfHomes) {
        ArrayList<TAgent> children = agentManager.createAgents(TAgentTypes.FATHER, noOfHomes);
        // noOfHomes体の子供エージェントを生成する．名前は，child1, child2, ...となる．
        for (int i = 0; i < children.size(); i++) {
            TAgent child = children.get(i);// i番目のエージェントを取り出す．
            TCommonRole commonRole = new TCommonRole(child, TSpotTypes.HOME + (i + 1));
            // 共通役割を生成する．
            TChildRole childRole = new TChildRole(child, TSpotTypes.HOME + (i + 1));
            // 子供役割を生成する．
            childRole.addChildRole(commonRole);// 共通役割を設定する．
            child.addRole(childRole);// 子供役割を設定する．
            child.activateRole(childRole.getName());// 子供役割を有効にする
            TSickPersonRole sickPersonRole = new TSickPersonRole(child, TSpotTypes.HOME + (i + 1), 3,
                    childRole.getName());
            // 病人役割を生成する．診察時間は3時間とする．
            child.addRole(sickPersonRole);
            // 病人役割を追加する．
            child.initializeCurrentSpot(spotManager.getSpotDB().get(TSpotTypes.HOME + (i + 1)));
            // i+1番目の子供エージェントの初期位置を，i+1番目の家スポットに設定する．
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
        int noOfHomes = 3; // 家の数
        TSpotManager spotManager = model.getSpotManager(); // スポット管理
        TAgentManager agentManager = model.getAgentManager(); // エージェント管理

        createSpots(spotManager, noOfHomes);// スポットの初期化
        createFatherAgents(agentManager, spotManager, noOfHomes);// 大人エージェントの初期化
        createChildAgents(agentManager, spotManager, noOfHomes);// 子供エージェントの初期化
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