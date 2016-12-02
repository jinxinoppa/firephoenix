package com.mzm.firephoenix.dao.entity;

import java.sql.Date;

@Entity(tableName = "machine_gain", primaryKey = "id")
public class MachineGain extends AbstractEntity {
	@Column(columnName = "id",isAutoIncrement = true)
	private int id;
	@Column(columnName = "seo_machine_id")
	private String seoMachineId;
	@Column(columnName = "seoid")
	private String seoid;
	@Column(columnName = "add_win_number")
	private int addWinNumber;
	@Column(columnName = "add_play_number")
	private int addPlayNumber;
	
	@Column(columnName = "add_win_point")
	private int addWinPoint;
	@Column(columnName = "add_play_point")
	private int addPlayPoint;
	
	@Column(columnName = "gain")
	private int gain;
	@Column(columnName = "oneday")
	private Date oneday;
	
	
	
	
	public MachineGain() {

	}
	public MachineGain(String seoMachineId, String seoid, Date oneday) {
		this.seoMachineId = seoMachineId;
		this.seoid = seoid;
		this.oneday = oneday;
		
		insertFieldsList.add("seoMachineId");
		insertFieldsList.add("seoid");
		insertFieldsList.add("oneday");
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
		updateFieldsList.add("id");
	}
	public String getSeoMachineId() {
		return seoMachineId;
	}
	public void setSeoMachineId(String seoMachineId) {
		this.seoMachineId = seoMachineId;
		updateFieldsList.add("seoMachineId");
	}
	public String getSeoid() {
		return seoid;
	}
	public void setSeoid(String seoid) {
		this.seoid = seoid;
		updateFieldsList.add("seoid");
	}
	public int getAddWinNumber() {
		return addWinNumber;
	}
	public void setAddWinNumber(int addWinNumber) {
		this.addWinNumber = addWinNumber;
		updateFieldsList.add("addWinNumber");
	}
	public int getAddPlayNumber() {
		return addPlayNumber;
	}
	public void setAddPlayNumber(int addPlayNumber) {
		this.addPlayNumber = addPlayNumber;
		updateFieldsList.add("addPlayNumber");
	}

	public int getAddWinPoint() {
		return addWinPoint;
	}
	public void setAddWinPoint(int addWinPoint) {
		this.addWinPoint = addWinPoint;
		updateFieldsList.add("addWinPoint");
	}
	public int getAddPlayPoint() {
		return addPlayPoint;
	}
	public void setAddPlayPoint(int addPlayPoint) {
		this.addPlayPoint = addPlayPoint;
		updateFieldsList.add("addPlayPoint");
	}

	public int getGain() {
		return gain;
	}
	public void setGain(int gain) {
		this.gain = gain;
		updateFieldsList.add("gain");
	}
	public Date getOneday() {
		return oneday;
	}
	public void setOneday(Date oneday) {
		this.oneday = oneday;
		updateFieldsList.add("oneday");
	}
	
}
