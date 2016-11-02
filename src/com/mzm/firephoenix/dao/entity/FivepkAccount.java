package com.mzm.firephoenix.dao.entity;

import java.util.Date;

@Entity(tableName = "fivepk_account", primaryKey = "accountId")
public class FivepkAccount extends AbstractEntity {
	@Column(columnName = "account_id", isAutoIncrement = true)
	private long accountId;
	@Column(columnName = "name")
	private String name;
	@Column(columnName = "password")
	private String password;
	@Column(columnName = "seoid")
	private String seoid;
	@Column(columnName = "create_date")
	private Date createDate;
	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSeoid() {
		return seoid;
	}

	public void setSeoid(String seoid) {
		this.seoid = seoid;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}