package jp.soars.utils.transport;

import jp.soars.core.TRole;
import jp.soars.core.TTime;
import jp.soars.utils.csv.TCCsvData;
import jp.soars.utils.random.ICRandom;

public class TTransportRole extends TRole {

    /** 役割名 */
    public static final String ROLE_NAME = "TransportRole";

    /**
     * コンストラクタ
     * 
     * @param name         名前
     * @param owner        この役割をもつ列車
     * @param schedule     運行スケジュール
     * @param trainManager 列車管理
     * @param rand         乱数生成器
     */
    public TTransportRole(TTransport owner, TCCsvData schedule, TTransportManager transportManager, ICRandom rand) {
        super(ROLE_NAME, owner);
        for (int i = 0; i < schedule.getNoOfRows(); ++i) {
            if (i == 0) { // 始発駅
                TTime time = new TTime(schedule.getElement(i, "Time"));
                String curStation = schedule.getElement(i, "Station");
                String nextStation = schedule.getElement(i + 1, "Station");
                new TNewTransportRule(this, curStation, transportManager).setTimeAndStage(true, time,
                        TTransport.TStages.NEW_TRANSPORT);
                new TTransportLeavingRule(this, curStation, nextStation, transportManager).setTimeAndStage(true,
                        time.clone().add("0:01"), TTransport.TStages.TRANSPORT_LEAVING);
            } else if (i == schedule.getNoOfRows() - 1) { // 終着駅
                TTime arrivalTime = new TTime(schedule.getElement(i, "Time"));
                String prevStation = schedule.getElement(i - 1, "Station");
                String curStation = schedule.getElement(i, "Station");
                new TTransportArrivingRule(this, prevStation, curStation, true, transportManager).setTimeAndStage(true,
                        arrivalTime, TTransport.TStages.TRANSPORT_ARRIVING);
                new TDeletingTransportRule(this, curStation, transportManager).setTimeAndStage(true,
                        arrivalTime.clone().add("0:01"), TTransport.TStages.DELETING_TRANSPORT);
            } else {
                TTime arrivalTime = new TTime(schedule.getElement(i, "Time"));
                TTime departureTime = arrivalTime.clone().add("0:01");
                String prevStation = schedule.getElement(i - 1, "Station");
                String curStation = schedule.getElement(i, "Station");
                String nextStation = schedule.getElement(i + 1, "Station");
                new TTransportArrivingRule(this, prevStation, curStation, false, transportManager).setTimeAndStage(true,
                        arrivalTime, TTransport.TStages.TRANSPORT_ARRIVING);
                new TTransportLeavingRule(this, curStation, nextStation, transportManager).setTimeAndStage(true,
                        departureTime, TTransport.TStages.TRANSPORT_LEAVING);
            }
        }
    }
}
