package jp.soars.examples.sample07;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import jp.soars.core.TAgent;
import jp.soars.core.TAgentManager;
import jp.soars.core.TModel;
import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;

/**
 * スポット，エージェントの動的追加・削除のサンプルです．
 * シミュレーションステップ：60分
 * シミュレーション時間：3日間
 */
public class TMain {

    /** グローバル共有情報名 */
    public static final String DUMMY_SPOT_LIST = "dummySpotList";
    public static final String DUMMY_AGENT_LIST = "dummyAgentList";

    /**
     * メインメソッド
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        //ステージの初期化
        List<String> stages = List.of(TStages.DYNAMIC_REMOVAL, TStages.DYNAMIC_ADDITION);
        //モデルの生成
        int interval = 60;
        long seed = 0;
        TModel model = new TModel(stages, interval, seed);
        //スポットの初期化
        int noOfSpots = 11;
        TSpotManager spotManager = model.getSpotManager();
        spotManager.createSpots(TSpotTypes.HOME, noOfSpots);
        //エージェントの初期化
        int noOfCreateAgents = 3;
        TAgentManager agentManager = model.getAgentManager();
        //スポット・エージェントを作成するエージェントを作成
        List<TAgent> agents = agentManager.createAgents(TAgentTypes.CREATOR, noOfCreateAgents);
        for(int i=0; i<agents.size(); i++){
            TAgent agent = agents.get(i);
            String home = TSpotTypes.HOME + (i+1);
            TCreatorRole role = new TCreatorRole(agent);
            agent.activateRole(role.getName());
            agent.initializeCurrentSpot(spotManager.getSpotDB().get(home));
        }
        //スポット・エージェントを削除するエージェントを作成
        int noOfDeleteAgents = 3;
        agents = agentManager.createAgents(TAgentTypes.KILLER, noOfDeleteAgents);
        for(int i=0; i<agents.size(); i++){
            TAgent agent = agents.get(i);
            String home = TSpotTypes.HOME + (i+4);
            TKillerRole role = new TKillerRole(agent);
            agent.activateRole(role.getName());
            agent.initializeCurrentSpot(spotManager.getSpotDB().get(home));
        }
        //グローバル共有情報(ダミーからランダムに選んで削除したいのでリストを作成して共有する)
        HashMap<String, Object> globalSharedVariableSet = model.getGlobalSharedVariableSet();
        int noOfDummies = 5;
        List<TSpot> dummySpotList = spotManager.createSpots(TSpotTypes.DUMMY_SPOT, noOfDummies);
        List<TAgent> dummyAgentList = agentManager.createAgents(TAgentTypes.DUMMY_AGENT, noOfDummies);
        for(int i=0; i<noOfDummies; i++){
            TAgent agent = dummyAgentList.get(i);
            String home = TSpotTypes.HOME + (i+7);
            agent.initializeCurrentSpot(spotManager.getSpotDB().get(home));
        }
        globalSharedVariableSet.put(DUMMY_SPOT_LIST, dummySpotList);
        globalSharedVariableSet.put(DUMMY_AGENT_LIST, dummyAgentList);
        //ロギング開始設定
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(cl.getTime()); //日付を取得
        model.getRuleAggregator().beginRuleLogger("logs/" + date + ".csv"); //ログ出力先設定
        TTime simulationPeriod = new TTime("3/0:00"); //シミュレーション終了時刻
        while (model.getTime().isLessThan(simulationPeriod)) {
            System.out.print(model.getTime() + "\t"); //時刻を表示する．
            model.execute(); //モデルの実行
            for (TAgent a : agentManager.getAgents()) {
                System.out.print(a.getName() + "\t");
            }
            for (TSpot s : spotManager.getSpots()) {
                System.out.print(s.getName() + "\t");
            }
            System.out.println();
        }     
        model.getRuleAggregator().endRuleLogger();  
    }
}
