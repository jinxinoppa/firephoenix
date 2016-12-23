package com.mzm.firephoenix.dao.entity;

import java.util.Date;

@Entity(tableName = "fivepk_path", primaryKey = "id")
public class FivepkPath extends AbstractEntity {
	@Column(columnName = "id", isAutoIncrement = true)
	private int id;
	@Column(columnName = "name")
	private String name;
	@Column(columnName = "machine_id")
	private String machineId;
	@Column(columnName = "win_point")
	private int winPoint;
	@Column(columnName = "play_point")
	private int playPoint;
	@Column(columnName = "win_number")
	private int winNumber;
	@Column(columnName = "play_number")
	private int playNumber;
	@Column(columnName = "begin_point")
	private int beginPoint;
	@Column(columnName = "end_point")
	private int endPoint;
	@Column(columnName = "access_point")
	private int accessPoint;
	@Column(columnName = "begin_time")
	private Date beginTime;
	@Column(columnName = "login_ip")
	private String loginIp;
	
	
	
	public FivepkPath() {

	}
	
	
	
	
	public FivepkPath(String name, String machineId, int winNumber, int playNumber, int beginPoint,int endPoint, int accessPoint, Date beginTime, String loginIp) {
		this.name = name;
		this.machineId = machineId;
		this.winNumber = winNumber;
		this.playNumber = playNumber;
		this.beginPoint = beginPoint;
		this.endPoint = endPoint;
		this.accessPoint = accessPoint;
		this.beginTime = beginTime;
		this.loginIp = loginIp;
		insertFieldsList.add("name");
		insertFieldsList.add("machineId");
		insertFieldsList.add("winNumber");
		insertFieldsList.add("playNumber");
		insertFieldsList.add("beginPoint");
		insertFieldsList.add("endPoint");
		insertFieldsList.add("accessPoint");
		insertFieldsList.add("beginTime");
		insertFieldsList.add("loginIp");
	}




	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		updateFieldsList.add("name");
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
		updateFieldsList.add("name");
	}
	public int getWinPoint() {
		return winPoint;
	}
	public void setWinPoint(int winPoint) {
		this.winPoint = winPoint;
		updateFieldsList.add("name");
	}
	public int getPlayPoint() {
		return playPoint;
	}
	public void setPlayPoint(int playPoint) {
		this.playPoint = playPoint;
		updateFieldsList.add("name");
	}
	public int getWinNumber() {
		return winNumber;
	}
	public void setWinNumber(int winNumber) {
		this.winNumber = winNumber;
		updateFieldsList.add("name");
	}
	public int getPlayNumber() {
		return playNumber;
	}
	public void setPlayNumber(int playNumber) {
		this.playNumber = playNumber;
		updateFieldsList.add("name");
	}
	public int getBeginPoint() {
		return beginPoint;
	}
	public void setBeginPoint(int beginPoint) {
		this.beginPoint = beginPoint;
		updateFieldsList.add("name");
	}
	public int getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(int endPoint) {
		this.endPoint = endPoint;
		updateFieldsList.add("name");
	}
	public int getAccessPoint() {
		return accessPoint;
	}
	public void setAccessPoint(int accessPoint) {
		this.accessPoint = accessPoint;
		updateFieldsList.add("name");
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
		updateFieldsList.add("name");
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
		updateFieldsList.add("name");
	}

	
	
}
