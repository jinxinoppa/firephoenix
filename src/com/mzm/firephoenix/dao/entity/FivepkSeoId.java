package com.mzm.firephoenix.dao.entity;

import java.util.Date;

@Entity(tableName = "fivepk_seo", primaryKey = "autoId")
public class FivepkSeoId extends AbstractEntity {
	@Column(columnName = "auto_id")
	private long autoId;
	@Column(columnName = "seoid")
	private String seoId;
	@Column(columnName = "seo_machine_id")
	private String seoMachineId;
	@Column(columnName = "seo_machine_type")
	private int seoMachineType;
	@Column(columnName = "account_id")
	private long accountId;
	@Column(columnName = "seo_machine_stay_time")
	private Date seoMachineStayTime;

	@Column(columnName = "prefab_five_bars")
	private byte prefabFiveBars;
	@Column(columnName = "prefab_five_bars_count")
	private double prefabFiveBarsCount;
	@Column(columnName = "prefab_royal_flush")
	private byte prefabRoyalFlush;
	@Column(columnName = "prefab_royal_flush_count")
	private double prefabRoyalFlushCount;
	@Column(columnName = "prefab_five_of_a_kind")
	private byte prefabFiveOfAKind;
	@Column(columnName = "prefab_five_of_a_kind_count")
	private double prefabFiveOfAKindCount;

	@Column(columnName = "prefab_five_of_a_kind_compare")
	private int prefabFiveOfAKindCompare;

	@Column(columnName = "prefab_straight_flush")
	private byte prefabStraightFlush;
	@Column(columnName = "prefab_straight_flush_count")
	private double prefabStraightFlushCount;
	@Column(columnName = "prefab_four_of_a_kind_joker")
	private byte prefabFourOfAKindJoker;
	@Column(columnName = "prefab_four_of_a_kind_joker_count")
	private double prefabFourOfAKindJokerCount;

	@Column(columnName = "prefab_four_of_a_kind_Joker_two_fourteen")
	private int prefabFourOfAKindJokerTwoFourteen;

	@Column(columnName = "prefab_four_of_a_kind_J_A")
	private byte prefabFourOfAKindJA;
	@Column(columnName = "prefab_four_of_a_kind_J_A_count")
	private double prefabFourOfAKindJACount;
	@Column(columnName = "prefab_four_of_a_kind_ja")
	private byte prefabFourOfAKindJa;

	@Column(columnName = "prefab_four_of_a_kind_T_T")
	private byte prefabFourOfAKindTT;
	@Column(columnName = "prefab_four_of_a_kind_T_T_count")
	private double prefabFourOfAKindTTCount;

	@Column(columnName = "prefab_four_of_a_kind_two_ten")
	private byte prefabFourOfAKindTwoTen;
	
	@Column(columnName = "prefab_four_of_a_kind_two_ten_two")
	private byte prefabFourOfAKindTwoTenTwo;
	@Column(columnName = "prefab_four_of_a_kind_two_ten_continue")
	private String prefabFourOfAKindTwoTenContinue;
	
	@Column(columnName = "prefab_full_house")
	private byte prefabFullHouse;
	@Column(columnName = "prefab_flush")
	private byte prefabFlush;
	@Column(columnName = "prefab_straight")
	private byte prefabStraight;
	@Column(columnName = "prefab_three_of_a_kind")
	private byte prefabThreeOfAKind;
	@Column(columnName = "prefab_two_pairs")
	private byte prefabTwoPairs;
	@Column(columnName = "prefab_seven_better")
	private byte prefabSevenBetter;
	@Column(columnName = "prefab_four_flush")
	private byte prefabFourFlush;
	@Column(columnName = "prefab_four_straight")
	private byte prefabFourStraight;
	@Column(columnName = "prefab_seven_better_keep")
	private byte prefabSevenBetterKeep;
	
	@Column(columnName = "prefab_joker")
	private byte prefabJoker;

	@Column(columnName = "seo_machine_play_count")
	private long seoMachinePlayCount;
	@Column(columnName = "machine_auto")
	private int machineAuto;
	@Column(columnName = "compare_history_cards")
	private String compareHistoryCards;

	@Column(columnName = "prefab_force_seven_better")
	private byte prefabForceSevenBetter;
	@Column(columnName = "prefab_force_seven_better_count")
	private byte prefabForceSevenBetterCount;
	
	
	@Column(columnName = "prefab_compare_buff")
	private int prefabCompareBuff;
	@Column(columnName = "prefab_compare_cut_down")
	private byte prefabCompareCutDown;
	@Column(columnName = "prefab_compare_cut_down_count")
	private byte prefabCompareCutDownCount;
	@Column(columnName = "prefab_compare_seven_joker")
	private byte prefabCompareSevenJoker;
	
	public int getMachineAuto() {
		return machineAuto;
	}

	public void setMachineAuto(int machineAuto) {
		this.machineAuto = machineAuto;
		updateFieldsList.add("machineAuto");
	}

	public byte getPrefab(int prefabCards) {
		switch (prefabCards) {
			case -3 :
				return prefabSevenBetterKeep;
			case -2 :
				return prefabFourStraight;
			case -1 :
				return prefabFourFlush;
			case 1 :
				return prefabSevenBetter;
			case 2 :
				return prefabTwoPairs;
			case 3 :
				return prefabThreeOfAKind;
			case 5 :
				return prefabStraight;
			case 7 :
				return prefabFlush;
			case 10 :
				return prefabFullHouse;
			case 48 :
				return prefabFourOfAKindTwoTenTwo;	
			case 49 :
				return prefabFourOfAKindTT;
			case 50 :
				return prefabFourOfAKindTwoTen;
			case 78 :
				return prefabFourOfAKindJA;
			case 79 :
				return prefabFourOfAKindJa;
			case 80 :
				return prefabFourOfAKindJoker;
			case 120 :
				return prefabStraightFlush;
			case 250 :
				return prefabFiveOfAKind;
			case 500 :
				return prefabRoyalFlush;
			case 1000 :
				return prefabFiveBars;
			case 9997 :
				return prefabCompareCutDown;
			case 9998 :
				return prefabForceSevenBetter;
			case 9999 :
				return prefabJoker;
			default :
				throw new NoSuchFieldError("no such field :[" + prefabCards + "]");
		}
	}

	public long getAutoId() {
		return autoId;
	}
	public void setAutoId(long autoId) {
		this.autoId = autoId;
	}
	public String getSeoId() {
		return seoId;
	}
	public void setSeoId(String seoId) {
		this.seoId = seoId;
		updateFieldsList.add("seoId");
	}
	public String getSeoMachineId() {
		return seoMachineId;
	}
	public void setSeoMachineId(String seoMachineId) {
		this.seoMachineId = seoMachineId;
		updateFieldsList.add("seoMachineId");
	}
	public int getSeoMachineType() {
		return seoMachineType;
	}
	public void setSeoMachineType(int seoMachineType) {
		this.seoMachineType = seoMachineType;
		updateFieldsList.add("seoMachineType");
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
		updateFieldsList.add("accountId");
	}
	public Date getSeoMachineStayTime() {
		return seoMachineStayTime;
	}
	public void setSeoMachineStayTime(Date seoMachineStayTime) {
		this.seoMachineStayTime = seoMachineStayTime;
		updateFieldsList.add("seoMachineStayTime");
	}
	public byte getPrefabFiveBars() {
		return prefabFiveBars;
	}
	public void setPrefabFiveBars(byte prefabFiveBars) {
		this.prefabFiveBars = prefabFiveBars;
		updateFieldsList.add("prefabFiveBars");
	}
	public byte getPrefabRoyalFlush() {
		return prefabRoyalFlush;
	}
	public void setPrefabRoyalFlush(byte prefabRoyalFlush) {
		this.prefabRoyalFlush = prefabRoyalFlush;
		updateFieldsList.add("prefabRoyalFlush");
	}
	public byte getPrefabFiveOfAKind() {
		return prefabFiveOfAKind;
	}
	public void setPrefabFiveOfAKind(byte prefabFiveOfAKind) {
		this.prefabFiveOfAKind = prefabFiveOfAKind;
		updateFieldsList.add("prefabFiveOfAKind");
	}
	public byte getPrefabStraightFlush() {
		return prefabStraightFlush;
	}
	public void setPrefabStraightFlush(byte prefabStraightFlush) {
		this.prefabStraightFlush = prefabStraightFlush;
		updateFieldsList.add("prefabStraightFlush");
	}
	public long getSeoMachinePlayCount() {
		return seoMachinePlayCount;
	}
	public void setSeoMachinePlayCount(long seoMachinePlayCount) {
		this.seoMachinePlayCount = seoMachinePlayCount;
		updateFieldsList.add("seoMachinePlayCount");
	}

	public double getPrefabFiveBarsCount() {
		return prefabFiveBarsCount;
	}

	public void setPrefabFiveBarsCount(double prefabFiveBarsCount) {
		this.prefabFiveBarsCount = prefabFiveBarsCount;
		updateFieldsList.add("prefabFiveBarsCount");
	}

	public double getPrefabRoyalFlushCount() {
		return prefabRoyalFlushCount;
	}

	public void setPrefabRoyalFlushCount(double prefabRoyalFlushCount) {
		this.prefabRoyalFlushCount = prefabRoyalFlushCount;
		updateFieldsList.add("prefabRoyalFlushCount");
	}

	public double getPrefabFiveOfAKindCount() {
		return prefabFiveOfAKindCount;
	}

	public void setPrefabFiveOfAKindCount(double prefabFiveOfAKindCount) {
		this.prefabFiveOfAKindCount = prefabFiveOfAKindCount;
		updateFieldsList.add("prefabFiveOfAKindCount");
	}

	public double getPrefabStraightFlushCount() {
		return prefabStraightFlushCount;
	}

	public void setPrefabStraightFlushCount(double prefabStraightFlushCount) {
		this.prefabStraightFlushCount = prefabStraightFlushCount;
		updateFieldsList.add("prefabStraightFlushCount");
	}

	public byte getPrefabFourOfAKindJoker() {
		return prefabFourOfAKindJoker;
	}

	public void setPrefabFourOfAKindJoker(byte prefabFourOfAKindJoker) {
		this.prefabFourOfAKindJoker = prefabFourOfAKindJoker;
		updateFieldsList.add("prefabFourOfAKindJoker");
	}

	public double getPrefabFourOfAKindJokerCount() {
		return prefabFourOfAKindJokerCount;
	}

	public void setPrefabFourOfAKindJokerCount(double prefabFourOfAKindJokerCount) {
		this.prefabFourOfAKindJokerCount = prefabFourOfAKindJokerCount;
		updateFieldsList.add("prefabFourOfAKindJokerCount");
	}

	public byte getPrefabFourOfAKindJa() {
		return prefabFourOfAKindJa;
	}

	public void setPrefabFourOfAKindJa(byte prefabFourOfAKindJa) {
		this.prefabFourOfAKindJa = prefabFourOfAKindJa;
	}

	public byte getPrefabFourOfAKindTwoTen() {
		return prefabFourOfAKindTwoTen;
	}

	public void setPrefabFourOfAKindTwoTen(byte prefabFourOfAKindTwoTen) {
		this.prefabFourOfAKindTwoTen = prefabFourOfAKindTwoTen;
	}

	public byte getPrefabFullHouse() {
		return prefabFullHouse;
	}

	public void setPrefabFullHouse(byte prefabFullHouse) {
		this.prefabFullHouse = prefabFullHouse;
	}

	public byte getPrefabFlush() {
		return prefabFlush;
	}

	public void setPrefabFlush(byte prefabFlush) {
		this.prefabFlush = prefabFlush;
	}

	public byte getPrefabStraight() {
		return prefabStraight;
	}

	public void setPrefabStraight(byte prefabStraight) {
		this.prefabStraight = prefabStraight;
	}

	public byte getPrefabThreeOfAKind() {
		return prefabThreeOfAKind;
	}

	public void setPrefabThreeOfAKind(byte prefabThreeOfAKind) {
		this.prefabThreeOfAKind = prefabThreeOfAKind;
	}

	public byte getPrefabTwoPairs() {
		return prefabTwoPairs;
	}

	public void setPrefabTwoPairs(byte prefabTwoPairs) {
		this.prefabTwoPairs = prefabTwoPairs;
	}

	public byte getPrefabSevenBetter() {
		return prefabSevenBetter;
	}

	public void setPrefabSevenBetter(byte prefabSevenBetter) {
		this.prefabSevenBetter = prefabSevenBetter;
	}

	public byte getPrefabFourFlush() {
		return prefabFourFlush;
	}

	public void setPrefabFourFlush(byte prefabFourFlush) {
		this.prefabFourFlush = prefabFourFlush;
	}

	public byte getPrefabFourStraight() {
		return prefabFourStraight;
	}

	public void setPrefabFourStraight(byte prefabFourStraight) {
		this.prefabFourStraight = prefabFourStraight;
	}

	public byte getPrefabSevenBetterKeep() {
		return prefabSevenBetterKeep;
	}

	public void setPrefabSevenBetterKeep(byte prefabSevenBetterKeep) {
		this.prefabSevenBetterKeep = prefabSevenBetterKeep;
	}

	public int getPrefabFiveOfAKindCompare() {
		return prefabFiveOfAKindCompare;
	}

	public void setPrefabFiveOfAKindCompare(int prefabFiveOfAKindCompare) {
		this.prefabFiveOfAKindCompare = prefabFiveOfAKindCompare;

	}

	public int getPrefabFourOfAKindJokerTwoFourteen() {
		return prefabFourOfAKindJokerTwoFourteen;
	}

	public void setPrefabFourOfAKindJokerTwoFourteen(int prefabFourOfAKindJokerTwoFourteen) {
		this.prefabFourOfAKindJokerTwoFourteen = prefabFourOfAKindJokerTwoFourteen;
		updateFieldsList.add("prefabFourOfAKindJokerTwoFourteen");
	}

	public byte getPrefabFourOfAKindJA() {
		return prefabFourOfAKindJA;
	}

	public void setPrefabFourOfAKindJA(byte prefabFourOfAKindJA) {
		this.prefabFourOfAKindJA = prefabFourOfAKindJA;
	}

	public byte getPrefabFourOfAKindTT() {
		return prefabFourOfAKindTT;
	}

	public void setPrefabFourOfAKindTT(byte prefabFourOfAKindTT) {
		this.prefabFourOfAKindTT = prefabFourOfAKindTT;
	}

	public double getPrefabFourOfAKindJACount() {
		return prefabFourOfAKindJACount;
	}

	public void setPrefabFourOfAKindJACount(double prefabFourOfAKindJACount) {
		this.prefabFourOfAKindJACount = prefabFourOfAKindJACount;
		updateFieldsList.add("prefabFourOfAKindJACount");
	}

	public double getPrefabFourOfAKindTTCount() {
		return prefabFourOfAKindTTCount;
	}

	public void setPrefabFourOfAKindTTCount(double prefabFourOfAKindTTCount) {
		this.prefabFourOfAKindTTCount = prefabFourOfAKindTTCount;
		updateFieldsList.add("prefabFourOfAKindTTCount");
	}

	public byte getPrefabJoker() {
		return prefabJoker;
	}

	public void setPrefabJoker(byte prefabJoker) {
		this.prefabJoker = prefabJoker;
	}

	public String getPrefabFourOfAKindTwoTenContinue() {
		return prefabFourOfAKindTwoTenContinue;
	}

	public void setPrefabFourOfAKindTwoTenContinue(String prefabFourOfAKindTwoTenContinue) {
		this.prefabFourOfAKindTwoTenContinue = prefabFourOfAKindTwoTenContinue;
		updateFieldsList.add("prefabFourOfAKindTwoTenContinue");
	}

	public byte getPrefabFourOfAKindTwoTenTwo() {
		return prefabFourOfAKindTwoTenTwo;
	}

	public void setPrefabFourOfAKindTwoTenTwo(byte prefabFourOfAKindTwoTenTwo) {
		this.prefabFourOfAKindTwoTenTwo = prefabFourOfAKindTwoTenTwo;
		updateFieldsList.add("prefabFourOfAKindTwoTenTwo");
	}
	
	public String getCompareHistoryCards() {
		return compareHistoryCards;
	}

	public void setCompareHistoryCards(String compareHistoryCards) {
		this.compareHistoryCards = compareHistoryCards;
		updateFieldsList.add("compareHistoryCards");
	}
	public void firstInLastOut(int compareCard) {
		if (compareHistoryCards != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(compareCard).append(",").append(compareHistoryCards.substring(0, compareHistoryCards.lastIndexOf(",") == -1 ? 0 : compareHistoryCards.lastIndexOf(",")));
			setCompareHistoryCards(sb.toString());
		}
	}

	public byte getPrefabForceSevenBetter() {
		return prefabForceSevenBetter;
	}

	public void setPrefabForceSevenBetter(byte prefabForceSevenBetter) {
		this.prefabForceSevenBetter = prefabForceSevenBetter;
	}

	public byte getPrefabForceSevenBetterCount() {
		return prefabForceSevenBetterCount;
	}

	public void setPrefabForceSevenBetterCount(byte prefabForceSevenBetterCount) {
		this.prefabForceSevenBetterCount = prefabForceSevenBetterCount;
		updateFieldsList.add("prefabForceSevenBetterCount");
	}

	public int getPrefabCompareBuff() {
		return prefabCompareBuff;
	}

	public void setPrefabCompareBuff(int prefabCompareBuff) {
		this.prefabCompareBuff = prefabCompareBuff;
		updateFieldsList.add("prefabCompareBuff");
	}

	public byte getPrefabCompareCutDown() {
		return prefabCompareCutDown;
	}

	public void setPrefabCompareCutDown(byte prefabCompareCutDown) {
		this.prefabCompareCutDown = prefabCompareCutDown;
	}

	public byte getPrefabCompareCutDownCount() {
		return prefabCompareCutDownCount;
	}

	public void setPrefabCompareCutDownCount(byte prefabCompareCutDownCount) {
		this.prefabCompareCutDownCount = prefabCompareCutDownCount;
		updateFieldsList.add("prefabCompareCutDownCount");
	}

	public byte getPrefabCompareSevenJoker() {
		return prefabCompareSevenJoker;
	}

	public void setPrefabCompareSevenJoker(byte prefabCompareSevenJoker) {
		this.prefabCompareSevenJoker = prefabCompareSevenJoker;
		updateFieldsList.add("prefabCompareSevenJoker");
	}
}
