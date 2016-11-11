package com.mzm.firephoenix.cache;

public class PlayerInfo {
	private int pic;
	private String nickName;
	private String seoId;
	public PlayerInfo() {
	}

	public PlayerInfo(int pic, String nickName, String seoId) {
		this.pic = pic;
		this.nickName = nickName;
		this.seoId = seoId;
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
}
