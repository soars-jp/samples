package jp.soars.transportation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.soars.core.TRuleAggregator;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.utils.csv.TCCsvData;
import jp.soars.utils.random.ICRandom;

/**
 * 乗り物管理クラス
 */
public class TTransportationManager {
    /** 列車集合 */
    private HashMap<String, TTransportation> fTransportationDB = new HashMap<>();

    /** 駅集合 */
    private HashMap<String, TStation> fStationDB = new HashMap<>();

    /** スポット管理 */
    private TSpotManager fSpotManager;

    /** 乱数生成器 */
    private ICRandom fRandom;

    /** ルール収集器 */
    private TRuleAggregator fRuleAggregator;

    /** 列車のスポット名リスト */
    private ArrayList<String> fSpotNameList = new ArrayList<>();

    /** 駅名リスト */
    private ArrayList<String> fStationList = new ArrayList<>();

    /**
     * コンストラクタ
     * 
     * @param transportationDBDirectory 乗り物情報DBディレクトリ
     * @param spotManager               スポット管理
     * @param ruleAggregator            ルール収集器
     * @param rand                      乱数生成器
     * @param expectedMaxNumberOfAgents このスポットに同時に滞在する最大エージェント数の予測値
     * @throws IOException
     */
    public TTransportationManager(String transportationDBDirectory, TSpotManager spotManager,
            TRuleAggregator ruleAggregator,
            ICRandom rand, int expectedMaxNumberOfAgents) throws IOException {
        fSpotManager = spotManager;
        fRuleAggregator = ruleAggregator;
        fRandom = rand;
        TCCsvData lines = new TCCsvData(transportationDBDirectory + File.separator + "lines.csv");
        for (int i = 0; i < lines.getNoOfRows(); ++i) {
            initializeLine(transportationDBDirectory, lines.getElement(i, "Line"), ruleAggregator, rand,
                    expectedMaxNumberOfAgents);
        }
        TCCsvData stations = new TCCsvData(transportationDBDirectory + File.separator + "stations.csv");
        initializeStationDB(stations, rand, expectedMaxNumberOfAgents);
        for (String stationName : fStationDB.keySet()) {
            fSpotManager.getSpotDB().put(stationName, fStationDB.get(stationName));
        }
    }

    /**
     * 駅DBを初期化する．
     * 
     * @param stations                  駅定義
     * @param random                    乱数発生器
     * @param expectedMaxNumberOfAgents このスポットに同時に滞在する最大エージェント数の予測値
     * @throws IOException
     */
    private void initializeStationDB(TCCsvData stations, ICRandom random, int expectedMaxNumberOfAgents) {
        for (int i = 0; i < stations.getNoOfRows(); ++i) {
            String stationName = stations.getElement(i, "Station");
            fStationDB.put(stationName, new TStation(stationName, fRuleAggregator, random, expectedMaxNumberOfAgents));
            fStationList.add(stationName);
        }
    }

    /**
     * スケジュールの整合性をチェックする．
     * 
     * @param transportationName 乗り物名
     * @param schedule           スケジュール
     * @param soruce             始発駅
     * @param departureTime      出発駅
     * @param destination
     * @param arrivalTime
     */
    private void checkSchedule(String transportationName, TCCsvData schedule, String source, TTime departureTime,
            String destination, TTime arrivalTime) {
        if (!source.equals(schedule.getElement(0, "Station"))
                || !departureTime.isEqualTo((schedule.getElement(0, "Time")))
                || !destination.equals(schedule.getElement(schedule.getNoOfRows() - 1, "Station"))
                || !arrivalTime.isEqualTo((schedule.getElement(schedule.getNoOfRows() - 1, "Time")))) {
            throw new RuntimeException(
                    transportationName + " is incosistent between the train list file and the train schedule file.");
        }
    }

    /**
     * スポット名に変換する
     * 
     * @param line               路線名
     * @param direction          方面
     * @param transportationName 乗り物名
     * @return
     */
    public static String convertToSpotName(String line, String direction, String transportationName) {
        return line + "." + direction + "." + transportationName;
    }

    /**
     * 路線を初期化する．
     * 
     * @param transportationDBDirectory 乗り物DBディレクトリ
     * @param line                      路線名
     * @param ruleAggregator            ルール収集器
     */
    private void initializeLine(String transportationDBDirectory, String line, TRuleAggregator ruleAggregator,
            ICRandom rand,
            int expectedMaxNumberOfAgents) throws IOException {
        String baseDir = transportationDBDirectory + File.separator + line + File.separator;
        TCCsvData transportations = new TCCsvData(baseDir + "trains.csv");
        for (int i = 0; i < transportations.getNoOfRows(); ++i) {
            if (!line.equals(transportations.getElement(i, "Line"))) {
                throw new RuntimeException(
                        "Error: The directory name (" + line + ") must be the same as the line name ("
                                + transportations.getElement(i, "Line") + ") in train.csv.");
            }
            String direction = transportations.getElement(i, "Direction"); // 方面
            String trainName = transportations.getElement(i, "TrainName"); // 列車名
            String type = transportations.getElement(i, "Type"); // タイプ
            String source = transportations.getElement(i, "Source"); // 始発駅
            TTime departureTime = new TTime(transportations.getElement(i, "DepartureTime")); // 出発時刻
            String destination = transportations.getElement(i, "Destination"); // 終着駅
            TTime arrivalTime = new TTime(transportations.getElement(i, "ArrivalTime")); // 到着時刻
            TCCsvData schedule = new TCCsvData(
                    baseDir + File.separator + direction + File.separator + trainName + ".csv"); // 運行スケジュール
            checkSchedule(trainName, schedule, source, departureTime, destination, arrivalTime);
            TTransportation transportation = new TTransportation(line, direction, trainName, type, source,
                    departureTime,
                    destination,
                    arrivalTime, schedule, ruleAggregator, rand, expectedMaxNumberOfAgents);
            TTransportationRole role = new TTransportationRole(transportation, schedule, this, rand);
            transportation.addRole(role);
            transportation.activateRole(role.getName());
            fTransportationDB.put(transportation.getSpotName(), transportation);
            fSpotNameList.add(transportation.getSpotName());
        }
    }

    /**
     * 列車集合を返す．
     * 
     * @return 列車集合
     */
    public HashMap<String, TTransportation> getTransportationDB() {
        return fTransportationDB;
    }

    /**
     * 乗り物のスポット名のリストを返す．
     * 
     * @return 乗り物のスポット名のリスト
     */
    public ArrayList<String> getSpotNamesOfTransportations() {
        return fSpotNameList;
    }

    /**
     * 駅名リストを返す．
     * 
     * @return 駅名リスト
     */
    public ArrayList<String> getStationList() {
        return fStationList;
    }

    /**
     * 駅集合を返す．
     * 
     * @return 駅集合
     */
    public HashMap<String, TStation> getStationDB() {
        return fStationDB;
    }

}
