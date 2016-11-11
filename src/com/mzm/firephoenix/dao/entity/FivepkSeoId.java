package com.mzm.firephoenix.dao.entity;

import java.util.Date;

@Entity(tableName = "fivepk_seo", primaryKey = "auto_id")
public class FivepkSeoId {
	@Column(columnName = "auto_id")
	private long autoId;
	@Column(columnName = "seoid")
	private String seoId;
	@Column(columnName = "seo_machine_id")
	private int seoMachineId;
	@Column(columnName = "seo_machine_type")
	private int seoMachineType;
	@Column(columnName = "account_id")
	private long accountId;
	@Column(columnName = "seo_machine_stay_time")
	private Date seoMachineStayTime;
	public long getAutoId() {
		return autoId;
	}
	public void setAutoId(long autoId) {
		this.autoId = autoId;
	}
	public String getSeoId() {
		return seoId;
	}
	public void setSeoId(String seoId) {
		this.seoId = seoId;
	}
	public int getSeoMachineId() {
		return seoMachineId;
	}
	public void setSeoMachineId(int seoMachineId) {
		this.seoMachineId = seoMachineId;
	}
	public int getSeoMachineType() {
		return seoMachineType;
	}
	public void setSeoMachineType(int seoMachineType) {
		this.seoMachineType = seoMachineType;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public Date getSeoMachineStayTime() {
		return seoMachineStayTime;
	}
	public void setSeoMachineStayTime(Date seoMachineStayTime) {
		this.seoMachineStayTime = seoMachineStayTime;
	}
}
