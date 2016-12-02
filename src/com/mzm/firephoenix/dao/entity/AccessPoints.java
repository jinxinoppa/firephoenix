package com.mzm.firephoenix.dao.entity;
@Entity(tableName = "access_points", primaryKey = "id")
public class AccessPoints extends AbstractEntity {
	@Column(columnName = "id",isAutoIncrement = true)
	private int id;
	@Column(columnName = "nick_name")
	private String nickName;
	@Column(columnName = "seoid")
	private String seoid;
	@Column(columnName = "on_score")
	private int onScore;
	@Column(columnName = "on_coin")
	private int onCoin;
	@Column(columnName = "up_score")
	private int upScore;
	@Column(columnName = "up_coin")
	private int upCoin;
	
	
	public AccessPoints() {
		
	}
	
	
	public AccessPoints(int id,String nickName, String seoid, int onScore, int onCoin, int upScore, int upCoin) {
		this.id = id;
		this.nickName = nickName;
		this.seoid = seoid;
		this.onScore = onScore;
		this.onCoin = onCoin;
		this.upScore = upScore;
		this.upCoin = upCoin;
		insertFieldsList.add("id");
		insertFieldsList.add("nickName");
		insertFieldsList.add("seoid");
		insertFieldsList.add("onScore");
		insertFieldsList.add("onCoin");
		insertFieldsList.add("upScore");
		insertFieldsList.add("upCoin");
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
		updateFieldsList.add("id");
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
		updateFieldsList.add("nickName");
	}
	public String getSeoid() {
		return seoid;
	}
	public void setSeoid(String seoid) {
		this.seoid = seoid;
		updateFieldsList.add("seoid");
	}
	public int getOnScore() {
		return onScore;
	}
	public void setOnScore(int onScore) {
		this.onScore = onScore;
		updateFieldsList.add("onScore");
	}
	public int getOnCoin() {
		return onCoin;
	}
	public void setOnCoin(int onCoin) {
		this.onCoin = onCoin;
		updateFieldsList.add("onCoin");
	}
	public int getUpScore() {
		return upScore;
	}
	public void setUpScore(int upScore) {
		this.upScore = upScore;
		updateFieldsList.add("upScore");
	}
	public int getUpCoin() {
		return upCoin;
	}
	public void setUpCoin(int upCoin) {
		this.upCoin = upCoin;
		updateFieldsList.add("upCoin");
	}
	
}
