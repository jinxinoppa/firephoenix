package com.mzm.firephoenix.dao.entity;

import com.mzm.firephoenix.utils.CardUtil;

@Entity(tableName = "fivepk_player_info", primaryKey = "accountId")
public class FivepkPlayerInfo extends AbstractEntity {
	@Column(columnName = "account_id")
	private long accountId;
	@Column(columnName = "nick_name")
	private String nickName;
	@Column(columnName = "pic")
	private byte pic;
	@Column(columnName = "coin")
	private int coin;
	@Column(columnName = "score")
	private int score;
	@Column(columnName = "compare_history_cards")
	private String compareHistoryCards = "";
	@Column(columnName = "nick_name_count")
	private int nickNameCount;

	public FivepkPlayerInfo() {
	}

	public FivepkPlayerInfo(long accountId, String nickName, byte pic, int score) {
		this.accountId = accountId;
		this.nickName = nickName;
		this.pic = pic;
		this.score = score;
		insertFieldsList.add("accountId");
		insertFieldsList.add("nickName");
		insertFieldsList.add("pic");
		insertFieldsList.add("score");
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
		updateFieldsList.add("nickName");
	}

	public byte getPic() {
		return pic;
	}

	public void setPic(byte pic) {
		this.pic = pic;
		updateFieldsList.add("pic");
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		updateFieldsList.add("score");
	}

	public void firstInLastOut(int compareCard) {
		if (compareHistoryCards != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(compareCard).append(",").append(compareHistoryCards.substring(0, compareHistoryCards.lastIndexOf(",") == -1 ? 0 : compareHistoryCards.lastIndexOf(",")));
			setCompareHistoryCards(sb.toString());
		}
	}

	public String getCompareHistoryCards() {
		if (compareHistoryCards == null){
			compareHistoryCards = "0,0,0,0,0,0";
		}
		return compareHistoryCards;
	}

	public void setCompareHistoryCards(String compareHistoryCards) {
		this.compareHistoryCards = compareHistoryCards;
		updateFieldsList.add("compareHistoryCards");
	}

	public void clearUpdateFieldsList() {
		updateFieldsList.clear();
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
		updateFieldsList.add("coin");
	}

	public int getNickNameCount() {
		return nickNameCount;
	}

	public void setNickNameCount(int nickNameCount) {
		this.nickNameCount = nickNameCount;
		updateFieldsList.add("nickNameCount");
	}
}
