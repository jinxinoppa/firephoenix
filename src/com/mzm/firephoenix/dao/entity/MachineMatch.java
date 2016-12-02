package com.mzm.firephoenix.dao.entity;

import java.sql.Date;



@Entity(tableName = "machine_match", primaryKey = "id")
public class MachineMatch extends AbstractEntity{
	@Column(columnName = "id",isAutoIncrement = true)
	private int id;
	@Column(columnName = "seo_machine_id")
	private String seoMachineId;
	@Column(columnName = "seoid")
	private String seoid;
	@Column(columnName = "win_number")
	private int winNumber;
	@Column(columnName = "play_number")
	private int playNumber;	
	@Column(columnName = "win_point")
	private int winPoint;
	@Column(columnName = "play_point")
	private int playPoint;	
	@Column(columnName = "three")
	private int three;
	@Column(columnName = "four")
	private int four;
	@Column(columnName = "five")
	private int five;
	@Column(columnName = "pass_number")
	private int passNumber;
	@Column(columnName = "pass_money")
	private int passMoney;
	@Column(columnName = "orider_machine_money")
	private int oriderMachineMoney;
	@Column(columnName = "oneday")
	private Date oneday;
	
	public MachineMatch() {
		
	}
	public MachineMatch(String seoMachineId, String seoid,Date oneday) {
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
	public Date getTime() {
		return oneday;
	}
	public void setTime(Date time) {
		this.oneday = time;
		updateFieldsList.add("oneday");
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
	public int getWinNumber() {
		return winNumber;
	}
	public void setWinNumber(int winNumber) {
		this.winNumber = winNumber;
		updateFieldsList.add("winNumber");
	}
	public int getPlayNumber() {
		return playNumber;
	}
	public void setPlayNumber(int playNumber) {
		this.playNumber = playNumber;
		updateFieldsList.add("playNumber");
	}
	public int getWinPoint() {
		return winPoint;
	}
	public void setWinPoint(int winPoint) {
		this.winPoint = winPoint;
		updateFieldsList.add("winPoint");
	}
	public int getPlayPoint() {
		return playPoint;
	}
	public void setPlayPoint(int playPoint) {
		this.playPoint = playPoint;
		updateFieldsList.add("playPoint");
	}
	public int getThree() {
		return three;
	}
	public void setThree(int three) {
		this.three = three;
		updateFieldsList.add("three");
	}
	public int getFour() {
		return four;
	}
	public void setFour(int four) {
		this.four = four;
		updateFieldsList.add("four");
	}
	public int getFive() {
		return five;
	}
	public void setFive(int five) {
		this.five = five;
		updateFieldsList.add("five");
	}
	public int getPassNumber() {
		return passNumber;
	}
	public void setPassNumber(int passNumber) {
		this.passNumber = passNumber;
		updateFieldsList.add("passNumber");
	}
	public int getPassMoney() {
		return passMoney;
	}
	public void setPassMoney(int passMoney) {
		this.passMoney = passMoney;
		updateFieldsList.add("passMoney");
	}
	public int getOriderMachineMoney() {
		return oriderMachineMoney;
	}
	public void setOriderMachineMoney(int oriderMachineMoney) {
		this.oriderMachineMoney = oriderMachineMoney;
		updateFieldsList.add("oriderMachineMoney");
	}
	
}
