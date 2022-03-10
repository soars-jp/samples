package jp.soars.examples.sample09;

import java.util.Set;

import jp.soars.core.TAgent;
import jp.soars.core.TRole;
import jp.soars.core.TTime;
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

	/** 使用する電車 */
	private String fLine;

	/** 乗車駅 */
	private String fSrcStation;

	/** 降車駅 */
	private String fDstStation;

	/**
	 * コンストラクタ
	 * 
	 * @param ownerAgent この役割を持つエージェント
	 * @param home       自宅
	 * @param line       通勤に使用する電車
	 * @param srcStation 乗車駅
	 * @param dstStation 降車駅
	 */
	public TFatherRole(TAgent ownerAgent, String home, String line, String srcStation, String dstStation) {
		super(ROLE_NAME, ownerAgent); // 親クラスのコンストラクタを呼び出す．
		fHome = home;
		fLine = line;
		fSrcStation = srcStation;
		fDstStation = dstStation;
		moveFromHomeToCompany();
		moveFromCompanyToHome();
	}

	/**
	 * 通勤ルールを登録
	 */
	private void moveFromHomeToCompany() {
		String direction = "inbound"; // 乗車する列車の方面
		Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
		Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
		// 6:55に自宅を出発して乗車駅に向かう．
		registerRule(new TRuleOfMoving(LEAVE_HOME, this, fHome, TSpotTypes.MIDWAY_SPOT
				+ "1", new TTime(0, 0, 5),
				TStages.AGENT_MOVING, REACH_STATION));
		getRule(LEAVE_HOME).setTimeAndStage(6, 55, TStages.AGENT_MOVING);
		// 7:00に乗車駅に到着する
		registerRule(new TRuleOfMovingStation(REACH_STATION, this, TSpotTypes.MIDWAY_SPOT
				+ "1", fSrcStation,
				TStages.AGENT_MOVING, GETON_TRANSPORTATION));
		// 最初に来た電車にのる
		registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION, this, fSrcStation, fLine, direction,
				trainTypes,
				trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION));
		// 電車から降りる
		registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION, this, fDstStation,
				new TTime(0, 0, 3),
				TStages.AGENT_MOVING, GO_COMPANY));
		// 7:33に降車駅を出発して会社に向かう．
		registerRule(new TRuleOfMoving(GO_COMPANY, this, fDstStation, TSpotTypes.MIDWAY_SPOT
				+ "1",
				new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_COMPANY));
		// 7:43に会社に到着する
		registerRule(new TRuleOfMoving(REACH_COMPANY, this, TSpotTypes.MIDWAY_SPOT
				+ "1", TSpotTypes.COMPANY + "1"));
	}

	/**
	 * 退勤ルールを登録
	 */
	private void moveFromCompanyToHome() {
		String direction = "outbound"; // 乗車する列車の方面
		Set<String> trainTypes = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の種類の条件：全ての種類の列車に乗る．
		Set<String> trainDestinations = Set.of(TGettingOnTransportationRule.ANY); // 乗車する列車の行き先の条件：全ての行き先の列車に乗る．
		// 17:55に会社を出発して乗車駅に向かう．
		registerRule(new TRuleOfMoving(LEAVE_COMPANY, this, TSpotTypes.COMPANY
				+ "1",
				TSpotTypes.MIDWAY_SPOT
						+ "1",
				new TTime(0, 0, 10), TStages.AGENT_MOVING, REACH_STATION_BACK));
		getRule(LEAVE_COMPANY).setTimeAndStage(17, 55, TStages.AGENT_MOVING);
		// 18:05に乗車駅に到着する．
		registerRule(new TRuleOfMovingStation(REACH_STATION_BACK, this, TSpotTypes.MIDWAY_SPOT
				+ "1", fDstStation,
				TStages.AGENT_MOVING, GETON_TRANSPORTATION_BACK));
		// 18:10に乗車駅で指定された列車に乗車する．
		registerRule(new TGettingOnTransportationRule(GETON_TRANSPORTATION_BACK, this, fDstStation, fLine,
				direction,
				trainTypes, trainDestinations, TStages.AGENT_MOVING, GETOFF_TRANSPORTATION_BACK));
		// 降車駅で列車から降車する．
		registerRule(new TGettingOffTransportationRule(GETOFF_TRANSPORTATION_BACK, this, fSrcStation,
				new TTime(0, 0, 5),
				TStages.AGENT_MOVING, GO_HOME));
		// 降車駅を出発して自宅に向かう．
		registerRule(new TRuleOfMoving(GO_HOME, this, fSrcStation, TSpotTypes.MIDWAY_SPOT
				+ "1", new TTime(0, 0, 5),
				TStages.AGENT_MOVING, REACH_HOME));
		// 自宅に到着する．
		registerRule(new TRuleOfMoving(REACH_HOME, this, TSpotTypes.MIDWAY_SPOT + "1", fHome));
	}
}