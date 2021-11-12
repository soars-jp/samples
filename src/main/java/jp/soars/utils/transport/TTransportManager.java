package jp.soars.utils.transport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.soars.core.TRuleAggregator;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.utils.csv.TCCsvData;
import jp.soars.utils.random.ICRandom;

public class TTransportManager {
    /** 列車集合 */
    private HashMap<String, TTransport> fTransportDB = new HashMap<>();

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
     * @param transportDBDirectory      乗り物情報DBディレクトリ
     * @param spotManager               スポット管理
     * @param ruleAggregator            ルール収集器
     * @param rand                      乱数生成器
     * @param expectedMaxNumberOfAgents このスポットに同時に滞在する最大エージェント数の予測値
     * @throws IOException
     */
    public TTransportManager(String transportDBDirectory, TSpotManager spotManager, TRuleAggregator ruleAggregator,
            ICRandom rand, int expectedMaxNumberOfAgents) throws IOException {
        fSpotManager = spotManager;
        fRuleAggregator = ruleAggregator;
        fRandom = rand;
        TCCsvData lines = new TCCsvData(transportDBDirectory + File.separator + "lines.csv");
        for (int i = 0; i < lines.getNoOfRows(); ++i) {
            initializeLine(transportDBDirectory, lines.getElement(i, "Line"), ruleAggregator, rand,
                    expectedMaxNumberOfAgents);
        }
        TCCsvData stations = new TCCsvData(transportDBDirectory + File.separator + "stations.csv");
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
     * @param transportName 乗り物名
     * @param schedule      スケジュール
     * @param soruce        始発駅
     * @param departureTime 出発駅
     * @param destination
     * @param arrivalTime
     */
    private void checkSchedule(String transportName, TCCsvData schedule, String source, TTime departureTime,
            String destination, TTime arrivalTime) {
        if (!source.equals(schedule.getElement(0, "Station"))
                || !departureTime.isEqualTo((schedule.getElement(0, "Time")))
                || !destination.equals(schedule.getElement(schedule.getNoOfRows() - 1, "Station"))
                || !arrivalTime.isEqualTo((schedule.getElement(schedule.getNoOfRows() - 1, "Time")))) {
            throw new RuntimeException(
                    transportName + " is incosistent between the train list file and the train schedule file.");
        }
    }

    /**
     * スポット名に変換する
     * 
     * @param line          路線名
     * @param direction     方面
     * @param transportName 乗り物名
     * @return
     */
    public static String convertToSpotName(String line, String direction, String transportName) {
        return line + "." + direction + "." + transportName;
    }

    /**
     * 路線を初期化する．
     * 
     * @param transportDBDirectory 乗り物DBディレクトリ
     * @param line                 路線名
     * @param ruleAggregator       ルール収集器
     */
    private void initializeLine(String transportDBDirectory, String line, TRuleAggregator ruleAggregator, ICRandom rand,
            int expectedMaxNumberOfAgents) throws IOException {
        String baseDir = transportDBDirectory + File.separator + line + File.separator;
        TCCsvData transports = new TCCsvData(baseDir + "trains.csv");
        for (int i = 0; i < transports.getNoOfRows(); ++i) {
            if (!line.equals(transports.getElement(i, "Line"))) {
                throw new RuntimeException(
                        "Error: The directory name (" + line + ") must be the same as the line name ("
                                + transports.getElement(i, "Line") + ") in train.csv.");
            }
            String direction = transports.getElement(i, "Direction"); // 方面
            String trainName = transports.getElement(i, "TrainName"); // 列車名
            String type = transports.getElement(i, "Type"); // タイプ
            String source = transports.getElement(i, "Source"); // 始発駅
            TTime departureTime = new TTime(transports.getElement(i, "DepartureTime")); // 出発時刻
            String destination = transports.getElement(i, "Destination"); // 終着駅
            TTime arrivalTime = new TTime(transports.getElement(i, "ArrivalTime")); // 到着時刻
            TCCsvData schedule = new TCCsvData(
                    baseDir + File.separator + direction + File.separator + trainName + ".csv"); // 運行スケジュール
            checkSchedule(trainName, schedule, source, departureTime, destination, arrivalTime);
            TTransport transport = new TTransport(line, direction, trainName, type, source, departureTime, destination,
                    arrivalTime, schedule, ruleAggregator, rand, expectedMaxNumberOfAgents);
            TTransportRole role = new TTransportRole(transport, schedule, this, rand);
            transport.addRole(role);
            transport.activateRole(role.getName());
            fTransportDB.put(transport.getSpotName(), transport);
            fSpotNameList.add(transport.getSpotName());
        }
    }

    /**
     * 列車集合を返す．
     * 
     * @return 列車集合
     */
    public HashMap<String, TTransport> getTransportDB() {
        return fTransportDB;
    }

    /**
     * 乗り物のスポット名のリストを返す．
     * 
     * @return 乗り物のスポット名のリスト
     */
    public ArrayList<String> getSpotNamesOfTransports() {
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
