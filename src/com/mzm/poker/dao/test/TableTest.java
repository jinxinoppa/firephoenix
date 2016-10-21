package com.mzm.poker.dao.test;

import com.mzm.poker.dao.entity.AbstractEntity;
import com.mzm.poker.dao.entity.Column;
import com.mzm.poker.dao.entity.Entity;

@Entity(tableName = "table_test")
public class TableTest extends AbstractEntity {
	@Column(columnName = "column_a")
	public byte columnA;
	@Column(columnName = "column_b")
	public short columnB;
	@Column(columnName = "column_c")
	public int columnC;
	@Column(columnName = "column_d")
	public long columnD;
	@Column(columnName = "column_e")
	public float columnE;
	@Column(columnName = "column_f")
	public double columnF;
	@Column(columnName = "column_g")
	public String columnG;
	public byte getColumnA() {
		return columnA;
	}
	public void setColumnA(byte columnA) {
		this.columnA = columnA;
	}
	public short getColumnB() {
		return columnB;
	}
	public void setColumnB(short columnB) {
		this.columnB = columnB;
	}
	public int getColumnC() {
		return columnC;
	}
	public void setColumnC(int columnC) {
		this.columnC = columnC;
	}
	public long getColumnD() {
		return columnD;
	}
	public void setColumnD(long columnD) {
		this.columnD = columnD;
	}
	public float getColumnE() {
		return columnE;
	}
	public void setColumnE(float columnE) {
		this.columnE = columnE;
	}
	public double getColumnF() {
		return columnF;
	}
	public void setColumnF(double columnF) {
		this.columnF = columnF;
	}
	public String getColumnG() {
		return columnG;
	}
	public void setColumnG(String columnG) {
		this.columnG = columnG;
	}
}
