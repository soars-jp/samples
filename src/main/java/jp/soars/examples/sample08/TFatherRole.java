package jp.soars.examples.sample08;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.transportation.TGettingOffTransportationRule;
import jp.soars.transportation.TGettingOnTransportationRule;

/**
 * 父親役割
 */
public class TFatherRole extends TRole {

        /** 役割名 */
        public static final String ROLE_NAME = "FatherRole";

        /** 家を出発する */
        public static final String LEAVE_HOME = "leave_home";

        /** 家に到着する */
        public static final String REACH_HOME = "reach_home";

        /** 駅に到着する（出勤） */
        public static final String REACH_STATION = "reach_station";

        /** 駅に到着する（帰宅） */
        public static final String REACH_STATION_BACK = "reach_station_back";

        /** 電車に乗る（出勤） */
        public static final String GETON_TRANSPORTATION = "geton_transportation";

        /** 電車に乗る（帰宅） */
        public static final String GETON_TRANSPORTATION_BACK = "geton_transportation_back";

        /** 電車から降りる（出勤） */
        public static final String GETOFF_TRANSPORTATION = "getoff_transportation";

        /** 電車から降りる（帰宅） */
        public static final String GETOFF_TRANSPORTATION_BACK = "getoff_transportation_back";

        /** 駅から会社に向かう */
        public static final String GO_COMPANY = "go_company";

        /** 会社に到着する */
        public static final String REACH_COMPANY = "reach_company";

        /** 会社に出発する */
        public static final String LEAVE_COMPANY = "leave_company";

        /** 家に帰る */
        public static final String GO_HOME = "go_home";

        /** 自宅 */
        private String fHome;

        /**
         * コンストラクタ
         * 
         * @param ownerAgent この役割を持つエージェント
         * @param home       自宅
         */
        public TFatherRole(TAgent ownerAgent, String home) {
                super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
                fHome = home;
                moveFromHomeToCompany();
                moveFromCompanyToHome();
        }

        /**
         * 出勤
         */
        private void moveFromHomeToCompany() {
                String line = "line1"; // 乗車する列車の路線
                String direction = "inbound"; // 乗車する列車の方面
                String trainName = "001"; // 乗車する列車名
                String srcStation = "station2"; // 乗車駅
                String dstStation = "station8"; // 降車駅
                // 6:55に自宅を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_HOME, this, fHome, TSpotTypes.MIDWAY_SPOT + "1"));
                getRule(LEAVE_HOME).setTimeAndStage(6, 55, TStages.AGENT_MOVING);
                // 7:00に乗車駅に到着する
                registerRule(new TRuleOfMoving(REACH_STATION, this, TSpotTypes.MIDWAY_SPOT + "1", srcStation));
                getRule(REACH_STATION).setTimeAndStage(7, 0, TStages.AGENT_MOVING);
                // 7:05に電車にのる
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION, this, srcStation, line,
                                direction,
                                trainName));
                getRule(GETON_TRANSPORTATION).setTimeAndStage(7, 5, TStages.AGENT_MOVING);
                // 7:30に電車から降りる
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION, this, dstStation, line,
                                direction,
                                trainName));
                getRule(GETOFF_TRANSPORTATION).setTimeAndStage(7, 30, TStages.AGENT_MOVING);
                // 7:33に降車駅を出発して会社に向かう．
                registerRule(new TRuleOfMoving(GO_COMPANY, this, dstStation, TSpotTypes.MIDWAY_SPOT + "1"));
                getRule(GO_COMPANY).setTimeAndStage(7, 33, TStages.AGENT_MOVING);
                // 7:43に会社に到着する
                registerRule(new TRuleOfMoving(REACH_COMPANY, this, TSpotTypes.MIDWAY_SPOT + "1", TSpotTypes.COMPANY
                                + "1"));
                getRule(REACH_COMPANY).setTimeAndStage(7, 43, TStages.AGENT_MOVING);
        }

        /**
         * 帰宅
         */
        private void moveFromCompanyToHome() {
                String line = "line1"; // 乗車する列車の路線
                String direction = "outbound"; // 乗車する列車の方面
                String trainName = "002"; // 乗車する列車名
                String srcStation = "station8"; // 乗車駅
                String dstStation = "station2"; // 降車駅
                // 17:55に会社を出発して乗車駅に向かう．
                registerRule(new TRuleOfMoving(LEAVE_COMPANY, this, TSpotTypes.COMPANY
                                + "1", TSpotTypes.MIDWAY_SPOT + "1"));
                getRule(LEAVE_COMPANY).setTimeAndStage(17, 55, TStages.AGENT_MOVING);
                // 18:05に乗車駅に到着する．
                registerRule(new TRuleOfMoving(REACH_STATION_BACK, this, TSpotTypes.MIDWAY_SPOT + "1", srcStation));
                getRule(REACH_STATION_BACK).setTimeAndStage(18, 5, TStages.AGENT_MOVING);
                // 18:10に乗車駅で指定された列車に乗車する．
                registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION_BACK, this, srcStation, line,
                                direction,
                                trainName));
                getRule(GETON_TRANSPORTATION_BACK).setTimeAndStage(18, 10, TStages.AGENT_MOVING);
                // 18:35に降車駅で列車から降車する．
                registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION_BACK, this, dstStation, line,
                                direction,
                                trainName));
                getRule(GETOFF_TRANSPORTATION_BACK).setTimeAndStage(18, 35, TStages.AGENT_MOVING);
                // 18:40に降車駅を出発して自宅に向かう．
                registerRule(new TRuleOfMoving(GO_HOME, this, dstStation, TSpotTypes.MIDWAY_SPOT + "1"));
                getRule(GO_HOME).setTimeAndStage(18, 40, TStages.AGENT_MOVING);
                // 18:45に自宅に到着する．
                registerRule(new TRuleOfMoving(REACH_HOME, this, TSpotTypes.MIDWAY_SPOT + "1", fHome));
                getRule(REACH_HOME).setTimeAndStage(18, 45, TStages.AGENT_MOVING);
        }
}