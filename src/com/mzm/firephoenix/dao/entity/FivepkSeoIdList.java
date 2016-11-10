package com.mzm.firephoenix.dao.entity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "fivepk_seo")
public class FivepkSeoIdList extends AbstractEntity {
	@Column(columnName = "seoid")
	private String seoid;
	public String getSeoid() {
		return seoid;
	}
	public void setSeoid(String seoid) {
		this.seoid = seoid;
	}
}
