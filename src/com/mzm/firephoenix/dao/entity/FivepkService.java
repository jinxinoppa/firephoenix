package com.mzm.firephoenix.dao.entity;
@Entity(tableName = "fivepk_service", primaryKey = "id")
public class FivepkService extends AbstractEntity {
	@Column(columnName = "id")
	private int id;
	@Column(columnName = "coin")
	private int coin;
	@Column(columnName = "score")
	private int score;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
		updateFieldsList.add("id");
	}
	public int getCoin() {
		return coin;
	}
	public void setCoin(int coin) {
		this.coin = coin;
		updateFieldsList.add("coin");
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
		updateFieldsList.add("score");
	}
	
	
	
}
