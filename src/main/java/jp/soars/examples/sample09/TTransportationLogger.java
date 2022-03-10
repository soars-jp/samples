package jp.soars.examples.sample09;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import jp.soars.core.TTime;
import jp.soars.transportation.TTransportation;
import jp.soars.transportation.TTransportationManager;

public class TTransportationLogger {
    /** 出力ストリーム */
    private PrintWriter fOut;

    /** 列車管理 */
    private TTransportationManager fTransportationManager;

    /**
     * 各列車が位置するスポットをCSVファイルに出力する．
     * 
     * @param filename              出力ファイル名
     * @param transportationManager 列車管理
     * @throws FileNotFoundException
     */
    public TTransportationLogger(String filename, TTransportationManager transportationManager) throws FileNotFoundException {
        fTransportationManager = transportationManager;
        fOut = new PrintWriter(filename);
        fOut.print("Time");
        ArrayList<String> names = transportationManager.getSpotNamesOfTransportations();
        for (String name : names) {
            fOut.print(",");
            fOut.print(name);
        }
        fOut.println();
    }

    /**
     * ログを出力する．
     * 
     * @param t 時刻
     * @throws IOException
     */
    public void output(TTime t) throws IOException {
        fOut.print(t);
        ArrayList<String> names = fTransportationManager.getSpotNamesOfTransportations();
        HashMap<String, TTransportation> trainDB = fTransportationManager.getTransportationDB();
        for (String name : names) {
            TTransportation train = trainDB.get(name);
            fOut.print(",");
            if (train.isInService()) {
                fOut.print(train.getCurrentSpotName());
            }
        }
        fOut.println();
    }

    /**
     * ログをクローズする．
     */
    public void close() {
        fOut.close();
    }

}
