package jp.soars.examples.sample09;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jp.soars.core.TSpot;
import jp.soars.core.TSpotManager;
import jp.soars.core.TTime;
import jp.soars.transportation.TTransportation;

public class TTransportationLogger {
    /** 出力ストリーム */
    private PrintWriter fOut;
    private TSpotManager fSpotManager;

    /**
     * 各列車が位置するスポットをCSVファイルに出力する．
     * 
     * @param filename              出力ファイル名
     * @param transportationManager 列車管理
     * @throws FileNotFoundException
     */
    public TTransportationLogger(String filename, TSpotManager spotManager) throws FileNotFoundException {
        fSpotManager = spotManager;
        fOut = new PrintWriter(filename);
        fOut.print("Time");
        ArrayList<TSpot> spots = spotManager.getSpots("local");
        for(TSpot spot : spots){
            fOut.print(",");
            fOut.print(spot.getName());
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
        ArrayList<TSpot> spots = fSpotManager.getSpots("local");

        for(TSpot spot : spots){
            TTransportation train = (TTransportation) spot;
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
