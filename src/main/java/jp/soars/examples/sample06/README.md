### サンプル6：サンプル5の拡張（次の発火までの相対時刻を毎回ランダムに決定）

サンプル6は，エージェントが各スポットでの滞在時間をランダムに決定するように，サンプル5を拡張したものである．以下では，サンプル5との差分だけを説明する．

#### サンプルプログラムの実行

サンプル6のプログラムは，soars.examples.sample06パッケージにある．実行方法は，以下の通りである．

    java soars.examples.sample06.TMain


#### シナリオとシミュレーション条件

以下のシナリオを考える．
- 300体のエージェント(agent1〜agent300)が，10個のスポット(spot1からspot10)上をランダムに動き回る．
- エージェントは，初期化時に，ランダムに選択したスポットを，自分のホームとする．
- エージェントは，0時から8時まで，ホームに滞在する．
- エージェントは，8時から18時まで，1) ランダムに決定したスポットへ移動する，2) 移動先での滞在時間を{1時間，2時間，3時間}の中からランダムに決定する，の2つの行動を繰り返す．
- エージェントは，18時にホームに戻る．
- エージェントは，18時から24時までホームに滞在する．

シミュレーション条件は以下の通りである．
- 開始時刻：0日目0:00
- 終了時刻：1日目24:00
- 時間ステップ：15分

### ルールの定義

終了時刻まで，1) ランダムに決定したスポットへ移動する，2) 移動先での滞在時間を{1時間，2時間，3時間}の中からランダムに決定する，の2つの行動を繰り返すルールとして，TRandomlyMovingRuleクラスを定義する．

サンプル5のTRandomlyMovingRuleクラスとの主な違いは，doItメソッドで滞在時間を{1時間, 2時間, 3時間}の中からランダムに選択して，次回の発火時刻を指定している．詳細はソースコードを参照されたい．

TRandomlyMovingRuleクラスのソースコードを以下に示す．

`TRandomlyMovingRule.java`

```java
public class TRandomlyMovingRule extends TAgentRule {
    /** 終了時刻 */
    private TTime fEndTime;

    /** ホームスポット */
    private String fHomeSpot;

    /** 出発スポット */
    private String fSpot;

    /** スポットリスト */
    private ArrayList<TSpot> fSpotList;

    /** 次のルールを実行するまでの時間 */
    private TTime fTimeToNextRule;

    /** 次に実行するルール */
    private TRandomlyMovingRule fRepeatedRule;

    /** 次の実行時刻を計算するためのワークメモリ */
    private TTime fNextTime;

    /**
     * コンストラクタ
     * 
     * @param ruleName  このルールの名前
     * @param ownerRole このルールを持つ役割
     * @param endTime   終了時刻
     * @param homeSpot  ホームスポット
     * @param spotList  ルールで移動する候補地
     */
    public TRandomlyMovingRule(String ruleName, TRole ownerRole, TTime endTime, String homeSpot,
            ArrayList<TSpot> spotList) {
        super(ruleName, ownerRole);
        fEndTime = endTime;
        fHomeSpot = homeSpot;
        fSpotList = spotList;
        fSpot = homeSpot;
        fTimeToNextRule = new TTime();
        fRepeatedRule = null;
        fNextTime = new TTime();
    }

    @Override
    public void doIt(TTime currentTime, String currentStage, HashMap<String, TSpot> spotSet,
            HashMap<String, TAgent> agentSet, HashMap<String, Object> globalSharedVariables) {

        if (isAt(fSpot)) { // スポット条件が満たされたら
            if (currentTime.isEqualTo(fEndTime)) {// 終了時刻ならば
                moveTo(spotSet.get(fHomeSpot));// ホームスポットへ移動する
            } else {
                ICRandom rand = getOwnerRole().getRandom();
                String destination = fSpotList.get(rand.nextInt(fSpotList.size())).getName();
                // ランダムに目的地を選択する
                moveTo(spotSet.get(destination));
                // 目的地に移動する
                TRandomlyMovingRule r = this;
                // 自分が臨時実行ルールならば，次回実行するルールとして自分を使い回す．
                // 臨時実行ルールであればgetTime().isDailyTime()はfalseを返す
                if (getTime().isDailyTime()) {
                    if (fRepeatedRule == null) { // 次に実行するルールが定義されていたら
                        fRepeatedRule = new TRandomlyMovingRule(getName(), getOwnerRole(), fEndTime, fHomeSpot,
                                fSpotList);
                    }
                    r = fRepeatedRule;
                }
                r.setSpot(destination);// 現在の命令の目的地を次のルールの出発地にする
                fTimeToNextRule.initialize(0, rand.nextInt(1, 3), 0);
                // 次回の発火時刻を1時間後から3時間後にランダムに設定
                fNextTime.copyFrom(currentTime).add(fTimeToNextRule);
                // 終了時刻を超えていたら終了時刻に実行する
                if (fNextTime.getHour() > fEndTime.getHour() || (fNextTime.getHour() == fEndTime.getHour()
                        && fNextTime.getMinute() > fEndTime.getMinute())) {
                    fNextTime.initialize(fNextTime.getDay(), fEndTime.getHour(), fEndTime.getMinute());
                }
                r.setTimeAndStage(false, fNextTime, getStage()); // 臨時実行ルールとして予約
            }
        }
    }

    /**
     * 出発地を設定
     * 
     * @param spot 出発地
     */
    public void setSpot(String destination) {
        fSpot = destination;
    }
}
```