package com.mzm.firephoenix.dao.entity;

import java.util.Date;

@Entity(tableName = "fivepk_seo", primaryKey = "autoId")
public class FivepkSeoId extends AbstractEntity {
	@Column(columnName = "auto_id")
	private long autoId;
	@Column(columnName = "seoid")
	private String seoId;
	@Column(columnName = "seo_machine_id")
	private String seoMachineId;
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
		updateFieldsList.add("seoId");
	}
	public String getSeoMachineId() {
		return seoMachineId;
	}
	public void setSeoMachineId(String seoMachineId) {
		this.seoMachineId = seoMachineId;
		updateFieldsList.add("seoMachineId");
	}
	public int getSeoMachineType() {
		return seoMachineType;
	}
	public void setSeoMachineType(int seoMachineType) {
		this.seoMachineType = seoMachineType;
		updateFieldsList.add("seoMachineType");
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
		updateFieldsList.add("accountId");
	}
	public Date getSeoMachineStayTime() {
		return seoMachineStayTime;
	}
	public void setSeoMachineStayTime(Date seoMachineStayTime) {
		this.seoMachineStayTime = seoMachineStayTime;
		updateFieldsList.add("seoMachineStayTime");
	}
}
