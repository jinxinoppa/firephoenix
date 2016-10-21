package com.mzm.poker.dao.entity;

@Entity(tableName = "fivepk_player_info")
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
	}

	public byte getPic() {
		return pic;
	}

	public void setPic(byte pic) {
		this.pic = pic;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
