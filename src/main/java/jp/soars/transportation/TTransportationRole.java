package jp.soars.transportation;

import jp.soars.core.TRole;
import jp.soars.core.TTime;
import jp.soars.utils.csv.TCCsvData;
import jp.soars.utils.random.ICRandom;

/**
 * 乗り物役割クラス
 */
public class TTransportationRole extends TRole {

        /** 役割名 */
        public static final String ROLE_NAME = "TransportationRole";

        /** 乗り物生成 */
        public static final String NEW_TRANSPORTATION = "NewTransportation";

        /** 駅を離れる */
        public static final String LEAVE = "TransportationLeaving";

        /** 駅に着く */
        public static final String ARRIVE = "TransportationArriving";

        /** 乗り物削除 */
        public static final String DELETE = "DeletingTransportation";

        /**
         * コンストラクタ
         * 
         * @param name         名前
         * @param owner        この役割をもつ列車
         * @param schedule     運行スケジュール
         * @param trainManager 列車管理
         * @param rand         乱数生成器
         */
        public TTransportationRole(TTransportation owner, TCCsvData schedule,
                        TTransportationManager transportationManager,
                        ICRandom rand) {
                super(ROLE_NAME, owner);
                for (int i = 0; i < schedule.getNoOfRows(); ++i) {
                        if (i == 0) { // 始発駅
                                TTime time = new TTime(schedule.getElement(i, "Time"));
                                String curStation = schedule.getElement(i, "Station");
                                String nextStation = schedule.getElement(i + 1, "Station");
                                registerRule(new TNewTransportationRule(NEW_TRANSPORTATION, this, curStation,
                                                transportationManager));
                                getRule(NEW_TRANSPORTATION).setTimeAndStage(true, time,
                                                TTransportation.TStages.NEW_TRANSPORTATION);
                                registerRule(new TTransportationLeavingRule(LEAVE + curStation, this, curStation,
                                                nextStation,
                                                transportationManager));
                                getRule(LEAVE + curStation).setTimeAndStage(true,
                                                time.clone().add("0:01"),
                                                TTransportation.TStages.TRANSPORTATION_LEAVING);
                        } else if (i == schedule.getNoOfRows() - 1) { // 終着駅
                                TTime arrivalTime = new TTime(schedule.getElement(i, "Time"));
                                String prevStation = schedule.getElement(i - 1, "Station");
                                String curStation = schedule.getElement(i, "Station");
                                registerRule(new TTransportationArrivingRule(ARRIVE + curStation, this, prevStation,
                                                curStation, true,
                                                transportationManager));
                                getRule(ARRIVE + curStation).setTimeAndStage(
                                                true,
                                                arrivalTime,
                                                TTransportation.TStages.TRANSPORTATION_ARRIVING);
                                registerRule(new TDeletingTransportationRule(DELETE, this, curStation,
                                                transportationManager));
                                getRule(DELETE).setTimeAndStage(true,
                                                arrivalTime.clone().add("0:01"),
                                                TTransportation.TStages.DELETING_TRANSPORTATION);
                        } else {
                                TTime arrivalTime = new TTime(schedule.getElement(i, "Time"));
                                TTime departureTime = arrivalTime.clone().add("0:01");
                                String prevStation = schedule.getElement(i - 1, "Station");
                                String curStation = schedule.getElement(i, "Station");
                                String nextStation = schedule.getElement(i + 1, "Station");
                                registerRule(new TTransportationArrivingRule(ARRIVE + curStation, this, prevStation,
                                                curStation, false,
                                                transportationManager));
                                getRule(ARRIVE + curStation).setTimeAndStage(
                                                true,
                                                arrivalTime,
                                                TTransportation.TStages.TRANSPORTATION_ARRIVING);
                                registerRule(new TTransportationLeavingRule(LEAVE + curStation, this, curStation,
                                                nextStation,
                                                transportationManager));
                                getRule(LEAVE + curStation).setTimeAndStage(true,
                                                departureTime,
                                                TTransportation.TStages.TRANSPORTATION_LEAVING);
                        }
                }
        }
}
