package com.mzm.firephoenix.cache;

import java.util.Date;

public class MachineInfo {
	private int machineType = 1;
	private long accountId = 0;
	private Date stayTime;
	public long getAccountId() {
		return accountId;
	}
	void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public int getMachineType() {
		return machineType;
	}
	void setMachineType(int machineType) {
		this.machineType = machineType;
	}
	public Date getStayTime() {
		return stayTime;
	}
	void setStayTime(Date stayTime) {
		this.stayTime = stayTime;
	}
}
