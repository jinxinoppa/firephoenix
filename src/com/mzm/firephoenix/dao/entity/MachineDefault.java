package com.mzm.firephoenix.dao.entity;

import java.sql.Date;



@Entity(tableName = "machine_default", primaryKey = "id")
public class MachineDefault extends AbstractEntity {
	@Column(columnName = "id",isAutoIncrement = true)
	private int id;
	@Column(columnName = "seo_machine_id")
	private String seoMachineId;
	@Column(columnName = "seoid")
	private String seoid;
	@Column(columnName = "win_number")
	private int winNumber;
	@Column(columnName = "play_number")
	private int playNumber;

	@Column(columnName = "win_sum_point")
	private int winSumPoint;
	@Column(columnName = "play_sum_point")
	private int playSumPoint;

	@Column(columnName = "five_bars")
	private int fiveBars;
	@Column(columnName = "royal_flush")
	private int royalFlush;
	@Column(columnName = "five_kind")
	private int fiveKind;
	@Column(columnName = "str_flush")
	private int strFlush;
	@Column(columnName = "big_four_kind")
	private int bigFourKind;
	@Column(columnName = "little_four_kind")
	private int littleFourKind;
	@Column(columnName = "full_house")
	private int fullHouse;
	@Column(columnName = "flush")
	private int flush;
	@Column(columnName = "straight")
	private int straight;
	@Column(columnName = "three_kind")
	private int threeKind;
	@Column(columnName = "two_pairs")
	private int twoPairs;
	@Column(columnName = "seven_better")
	private int sevenBetter;
	@Column(columnName = "oneday")
	private Date oneday;

	public MachineDefault() {

	}

	public MachineDefault(String seoMachineId, String seoid,Date oneday) {
		this.seoMachineId = seoMachineId;
		this.seoid = seoid;
		this.oneday = oneday;
		insertFieldsList.add("seoMachineId");
		insertFieldsList.add("seoid");
		insertFieldsList.add("oneday");
	}

	public MachineDefault(String seoMachineId, String seoid, int winNumber, int playNumber, float gameRate, int winSumPoint, int playSumPoint, float winRatep, int fiveBars, int royalFlush, int fiveKind, int strFlush, int bigFourKind, int littleFourKind, int fullHouse, int flush, int straight,
			int threeKind, int twoPairs, int sevenBetter) {
		this.seoMachineId = seoMachineId;
		this.seoid = seoid;
		this.winNumber = winNumber;
		this.playNumber = playNumber;

		this.winSumPoint = winSumPoint;
		this.playSumPoint = playSumPoint;

		this.fiveBars = fiveBars;
		this.royalFlush = royalFlush;
		this.fiveKind = fiveKind;
		this.strFlush = strFlush;
		this.bigFourKind = bigFourKind;
		this.littleFourKind = littleFourKind;
		this.fullHouse = fullHouse;
		this.flush = flush;
		this.straight = straight;
		this.threeKind = threeKind;
		this.twoPairs = twoPairs;
		this.sevenBetter = sevenBetter;
		insertFieldsList.add("seoMachineId");
		insertFieldsList.add("seoid");
		insertFieldsList.add("winNumber");
		insertFieldsList.add("playNumber");

		insertFieldsList.add("winSumPoint");
		insertFieldsList.add("playSumPoint");

		insertFieldsList.add("fiveBars");
		insertFieldsList.add("royalFlush");
		insertFieldsList.add("fiveKind");
		insertFieldsList.add("strFlush");
		insertFieldsList.add("bigFourKind");
		insertFieldsList.add("littleFourKind");
		insertFieldsList.add("fullHouse");
		insertFieldsList.add("flush");
		insertFieldsList.add("straight");
		insertFieldsList.add("threeKind");
		insertFieldsList.add("twoPairs");
		insertFieldsList.add("twoPairs");
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		updateFieldsList.add("id");
	}

	public Date getTime() {
		return oneday;
	}

	public void setTime(Date oneday) {
		this.oneday = oneday;
		updateFieldsList.add("oneday");
	}

	public String getSeoMachineId() {
		return seoMachineId;
	}
	public void setSeoMachineId(String seoMachineId) {
		this.seoMachineId = seoMachineId;
		updateFieldsList.add("seoMachineId");
	}
	public String getSeoid() {
		return seoid;
	}
	public void setSeoid(String seoid) {
		this.seoid = seoid;
		updateFieldsList.add("seoid");
	}
	public int getWinNumber() {
		return winNumber;
	}
	public void setWinNumber(int winNumber) {
		this.winNumber = winNumber;
		updateFieldsList.add("winNumber");
	}
	public int getPlayNumber() {
		return playNumber;
	}
	public void setPlayNumber(int playNumber) {
		this.playNumber = playNumber;
		updateFieldsList.add("playNumber");
	}

	public int getWinSumPoint() {
		return winSumPoint;
	}
	public void setWinSumPoint(int winSumPoint) {
		this.winSumPoint = winSumPoint;
		updateFieldsList.add("winSumPoint");
	}


	public int getPlaySumPoint() {
		return playSumPoint;
	}

	public void setPlaySumPoint(int playSumPoint) {
		this.playSumPoint = playSumPoint;
		updateFieldsList.add("playSumPoint");
	}

	public int getFiveBars() {
		return fiveBars;
	}
	public void setFiveBars(int fiveBars) {
		this.fiveBars = fiveBars;
		updateFieldsList.add("fiveBars");
	}
	public int getRoyalFlush() {
		return royalFlush;
	}
	public void setRoyalFlush(int royalFlush) {
		this.royalFlush = royalFlush;
		updateFieldsList.add("royalFlush");
	}
	public int getFiveKind() {
		return fiveKind;
	}
	public void setFiveKind(int fiveKind) {
		this.fiveKind = fiveKind;
		updateFieldsList.add("fiveKind");
	}
	public int getStrFlush() {
		return strFlush;
	}
	public void setStrFlush(int strFlush) {
		this.strFlush = strFlush;
		updateFieldsList.add("strFlush");
	}
	public int getBigFourKind() {
		return bigFourKind;
	}
	public void setBigFourKind(int bigFourKind) {
		this.bigFourKind = bigFourKind;
		updateFieldsList.add("bigFourKind");
	}
	public int getLittleFourKind() {
		return littleFourKind;
	}
	public void setLittleFourKind(int littleFourKind) {
		this.littleFourKind = littleFourKind;
		updateFieldsList.add("littleFourKind");
	}
	public int getFullHouse() {
		return fullHouse;
	}
	public void setFullHouse(int fullHouse) {
		this.fullHouse = fullHouse;
		updateFieldsList.add("fullHouse");
	}
	public int getFlush() {
		return flush;
	}
	public void setFlush(int flush) {
		this.flush = flush;
		updateFieldsList.add("flush");
	}
	public int getStraight() {
		return straight;
	}
	public void setStraight(int straight) {
		this.straight = straight;
		updateFieldsList.add("straight");
	}
	public int getThreeKind() {
		return threeKind;
	}
	public void setThreeKind(int threeKind) {
		this.threeKind = threeKind;
		updateFieldsList.add("threeKind");
	}
	public int getTwoPairs() {
		return twoPairs;
	}
	public void setTwoPairs(int twoPairs) {
		this.twoPairs = twoPairs;
		updateFieldsList.add("twoPairs");
	}
	public int getSevenBetter() {
		return sevenBetter;
	}
	public void setSevenBetter(int sevenBetter) {
		this.sevenBetter = sevenBetter;
		updateFieldsList.add("sevenBetter");
	}

}
