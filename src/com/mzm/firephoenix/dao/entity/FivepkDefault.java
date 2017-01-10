package com.mzm.firephoenix.dao.entity;
@Entity(tableName = "fivepk_default", primaryKey = "id")
public class FivepkDefault extends AbstractEntity{
	@Column(columnName = "id", isAutoIncrement = true)
	private int id;
	@Column(columnName = "name")
	private String name;
	@Column(columnName = "machine_id")
	private String machineId;
	@Column(columnName = "credit")
	private int credit;
	@Column(columnName = "bet")
	private int bet;
	@Column(columnName = "win")
	private int win;
	@Column(columnName = "one_card")
	private String oneCard;
	@Column(columnName = "guard_card")
	private String guardCard;
	@Column(columnName = "two_card")
	private String twoCard;
	@Column(columnName = "card_type")
	private int cardType;
	@Column(columnName = "guess_point")
	private String guessPoint;
	@Column(columnName = "guess_type")
	private String guessType;
	
	public FivepkDefault() {	}


	public FivepkDefault(String name, String machineId, int credit,int bet, int win, String oneCard, String guardCard, String twoCard,int cardType, String guessPoint, String guessType) {
		this.name = name;
		this.machineId = machineId;
		this.credit = credit;
		this.bet = bet;
		this.win = win;
		this.oneCard = oneCard;
		this.guardCard = guardCard;
		this.twoCard = twoCard;
		this.cardType = cardType;
		this.guessPoint = guessPoint;
		this.guessType = guessType;
		insertFieldsList.add("name");
		insertFieldsList.add("machineId");
		insertFieldsList.add("credit");
		insertFieldsList.add("bet");
		insertFieldsList.add("win");
		insertFieldsList.add("oneCard");
		insertFieldsList.add("guardCard");
		insertFieldsList.add("twoCard");
		insertFieldsList.add("cardType");
		insertFieldsList.add("guessPoint");
		insertFieldsList.add("guessType");
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateFieldsList.add("name");
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
		updateFieldsList.add("machineId");
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
		updateFieldsList.add("credit");
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int bet) {
		this.bet = bet;
		updateFieldsList.add("bet");
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
		updateFieldsList.add("win");
	}

	public String getOneCard() {
		return oneCard;
	}

	public void setOneCard(String oneCard) {
		this.oneCard = oneCard;
		updateFieldsList.add("oneCard");
	}

	public String getGuardCard() {
		return guardCard;
	}

	public void setGuardCard(String guardCard) {
		this.guardCard = guardCard;
		updateFieldsList.add("guardCard");
	}

	public String getTwoCard() {
		return twoCard;
	}

	public void setTwoCard(String twoCard) {
		this.twoCard = twoCard;
		updateFieldsList.add("twoCard");
	}

	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
		updateFieldsList.add("cardType");
	}

	public String getGuessPoint() {
		return guessPoint;
	}

	public void setGuessPoint(String guessPoint) {
		this.guessPoint = guessPoint;
		updateFieldsList.add("guessPoint");
	}

	public String getGuessType() {
		return guessType;
	}

	public void setGuessType(String guessType) {
		this.guessType = guessType;
		updateFieldsList.add("guessType");
	}
	
	
}
