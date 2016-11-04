package com.mzm.firephoenix.dao.entity;

import java.util.ArrayList;
import java.util.List;

import com.mzm.firephoenix.cardutils.CardUtil;

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
	@Column(columnName = "score")
	private long score;
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

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
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

	public List<Integer> getCompareHistoryCardsList() {
		if (compareHistoryCards != null) {
			String[] strArr = compareHistoryCards.split(",");
			for (int i = 0; i < strArr.length; i++) {
				compareHistoryCardsList.add(Integer.parseInt(strArr[i]));
			}
		} else {
			for (int i = 0; i < 6; i++) {
				compareHistoryCardsList.add((int) CardUtil.compareCard());
			}
		}
		return compareHistoryCardsList;
	}

	public void setCompareHistoryCardsList() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < compareHistoryCardsList.size(); i++) {
			sb.append(compareHistoryCardsList.get(i)).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		setCompareHistoryCards(sb.toString());
	}

	public String getCompareHistoryCards() {
		StringBuffer sb = new StringBuffer();
		if (compareHistoryCards == null) {
			for (int i = 0; i < 6; i++) {
				sb.append(CardUtil.compareCard());
				sb.append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			setCompareHistoryCards(sb.toString());
		}
		return compareHistoryCards;
	}

	protected void setCompareHistoryCards(String compareHistoryCards) {
		this.compareHistoryCards = compareHistoryCards;
		updateFieldsList.add("compareHistoryCards");
	}

	public void clearUpdateFieldsList() {
		updateFieldsList.clear();
	}
}
