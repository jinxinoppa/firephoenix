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
	
	@Column(columnName = "four2")
	private int four2;
	@Column(columnName = "four3")
	private int four3;
	@Column(columnName = "four4")
	private int four4;
	@Column(columnName = "four5")
	private int four5;
	@Column(columnName = "four6")
	private int four6;
	@Column(columnName = "four7")
	private int four7;
	@Column(columnName = "four8")
	private int four8;
	@Column(columnName = "four9")
	private int four9;
	@Column(columnName = "four10")
	private int four10;
	@Column(columnName = "four11")
	private int four11;
	@Column(columnName = "four12")
	private int four12;
	@Column(columnName = "four13")
	private int four13;
	@Column(columnName = "four14")
	private int four14;
	@Column(columnName = "five1")
	private int five1;
	@Column(columnName = "five2")
	private int five2;


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
	
	public int getFour2() {
		return four2;
	}

	public void setFour2(int four2) {
		this.four2 = four2;
		updateFieldsList.add("four2");
	}

	public int getFour3() {
		return four3;
	}

	public void setFour3(int four3) {
		this.four3 = four3;
		updateFieldsList.add("four3");
	}

	public int getFour4() {
		return four4;
	}

	public void setFour4(int four4) {
		this.four4 = four4;
		updateFieldsList.add("four4");
	}

	public int getFour5() {
		return four5;
	}

	public void setFour5(int four5) {
		this.four5 = four5;
		updateFieldsList.add("four5");
	}

	public int getFour6() {
		return four6;
	}

	public void setFour6(int four6) {
		this.four6 = four6;
		updateFieldsList.add("four6");
	}

	public int getFour7() {
		return four7;
	}

	public void setFour7(int four7) {
		this.four7 = four7;
		updateFieldsList.add("four7");
	}

	public int getFour8() {
		return four8;
	}

	public void setFour8(int four8) {
		this.four8 = four8;
		updateFieldsList.add("four8");
	}

	public int getFour9() {
		return four9;
	}

	public void setFour9(int four9) {
		this.four9 = four9;
		updateFieldsList.add("four9");
	}

	public int getFour10() {
		return four10;
	}

	public void setFour10(int four10) {
		this.four10 = four10;
		updateFieldsList.add("four10");
	}

	public int getFour11() {
		return four11;
	}

	public void setFour11(int four11) {
		this.four11 = four11;
		updateFieldsList.add("four11");
	}

	public int getFour12() {
		return four12;
	}

	public void setFour12(int four12) {
		
		this.four12 = four12;
		updateFieldsList.add("four12");
	}

	public int getFour13() {
		return four13;
	}

	public void setFour13(int four13) {
		this.four13 = four13;
		updateFieldsList.add("four13");
	}

	public int getFour14() {
		return four14;
	}

	public void setFour14(int four14) {
		this.four14 = four14;
		updateFieldsList.add("four14");
	}

	public int getFive1() {
		return five1;
	}

	public void setFive1(int five1) {
		this.five1 = five1;
		updateFieldsList.add("five1");
	}

	public int getFive2() {
		return five2;
	}

	public void setFive2(int five2) {
		this.five2 = five2;
		updateFieldsList.add("five2");
	}

}
