package com.mzm.firephoenix.dao.entity;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntity {
	@Column(isContinue = true)
	public List<String> updateFieldsList = new ArrayList<String>(6);
	@Column(isContinue = true)
	public List<String> insertFieldsList = new ArrayList<String>(6);
}
