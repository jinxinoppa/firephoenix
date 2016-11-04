package com.mzm.firephoenix.dao.entity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "fivepk_player_info", primaryKey = "accountId")
public class FivepkPlayerInfo extends AbstractEntity {
	@Column(columnName = "account_id")
	private long accountId;
	@Column(columnName = "name")
	private String name;
	@Column(columnName = "nick_name")
	private String nickName;
	@Column(columnName = "pic")
	private byte pic;
	@Column(columnName = "coin")
	private int coin;
	@Column(columnName = "score")
	private int score;
	@Column(columnName = "compare_history_cards")
	private String compareHistoryCards;
	@Column(isContinue = true)
	private List<Integer> compareHistoryCardsList = new ArrayList<Integer>(6);
	@Column(isContinue = true)
	private List<String> updateFieldsList = new ArrayList<String>(6);
	public FivepkPlayerInfo() {
	}

	public FivepkPlayerInfo(long accountId, String name, String nickName) {
		this.accountId = accountId;
		this.name = name;
		this.nickName = nickName;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		updateFieldsList.add("name");
	}

	public void firstInLastOut(int compareCard) {
		if (compareHistoryCards != null) {
			StringBuffer sb = new StringBuffer();
			sb.append(compareCard).append(",").append(compareHistoryCards.substring(0, compareHistoryCards.lastIndexOf(",")));
			setCompareHistoryCards(sb.toString());
		}
	}

	public String getCompareHistoryCards() {
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
	}
}
