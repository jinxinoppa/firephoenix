package com.mzm.firephoenix.cache;

import org.apache.mina.core.session.IoSession;

public class PlayerInfo {
	private int pic;
	private String nickName;
	private String seoId;
	private IoSession playerInfoSession;
	private byte accountType;

	public PlayerInfo() {
	}

	public PlayerInfo(int pic, String nickName, String seoId, IoSession playerInfoSession) {
		this.pic = pic;
		this.nickName = nickName;
		this.seoId = seoId;
		this.playerInfoSession = playerInfoSession;
	}

	public PlayerInfo(int pic, String nickName) {
		this.pic = pic;
		this.nickName = nickName;
	}

	public int getPic() {
		return pic;
	}
	void setPic(int pic) {
		this.pic = pic;
	}
	public String getNickName() {
		return nickName;
	}
	void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getSeoId() {
		return seoId;
	}

	void setSeoId(String seoId) {
		this.seoId = seoId;
	}

	public IoSession getPlayerInfoSession() {
		return playerInfoSession;
	}

	void setPlayerInfoSession(IoSession playerInfoSession) {
		this.playerInfoSession = playerInfoSession;
	}

	public byte getAccountType() {
		return accountType;
	}

	public void setAccountType(byte accountType) {
		this.accountType = accountType;
	}
}