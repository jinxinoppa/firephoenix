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

	@Column(columnName = "prefab_five_bars")
	private byte prefabFiveBars;
	@Column(columnName = "prefab_five_bars_count")
	private int prefabFiveBarsCount;
	@Column(columnName = "prefab_royal_flush")
	private byte prefabRoyalFlush;
	@Column(columnName = "prefab_royal_flush_count")
	private int prefabRoyalFlushCount;
	@Column(columnName = "prefab_five_of_a_kind")
	private byte prefabFiveOfAKind;
	@Column(columnName = "prefab_five_of_a_kind_count")
	private int prefabFiveOfAKindCount;
	@Column(columnName = "prefab_straight_flush")
	private byte prefabStraightFlush;
	@Column(columnName = "prefab_straight_flush_count")
	private int prefabStraightFlushCount;
	@Column(columnName = "prefab_four_of_a_kind_JOKER")
	private byte prefabFourOfAKindJOKER;
	@Column(columnName = "prefab_four_of_a_kind_JOKER_count")
	private int prefabFourOfAKindJOKERCount;
	@Column(columnName = "seo_machine_play_count")
	private long seoMachinePlayCount;
	
	public byte getPrefab(int prefabCards) {
		switch (prefabCards) {
			case 80 :
				return prefabFourOfAKindJOKER;
			case 120 :
				return prefabStraightFlush;
			case 250 :
				return prefabFiveOfAKind;
			case 500 :
				return prefabRoyalFlush;
			case 1000 :
				return prefabFiveBars;
			default :
				throw new NoSuchFieldError("no such field :[" + prefabCards + "]");
		}
	}

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
	public byte getPrefabFiveBars() {
		return prefabFiveBars;
	}
	public void setPrefabFiveBars(byte prefabFiveBars) {
		this.prefabFiveBars = prefabFiveBars;
		updateFieldsList.add("prefabFiveBars");
	}
	public byte getPrefabRoyalFlush() {
		return prefabRoyalFlush;
	}
	public void setPrefabRoyalFlush(byte prefabRoyalFlush) {
		this.prefabRoyalFlush = prefabRoyalFlush;
		updateFieldsList.add("prefabRoyalFlush");
	}
	public byte getPrefabFiveOfAKind() {
		return prefabFiveOfAKind;
	}
	public void setPrefabFiveOfAKind(byte prefabFiveOfAKind) {
		this.prefabFiveOfAKind = prefabFiveOfAKind;
		updateFieldsList.add("prefabFiveOfAKind");
	}
	public byte getPrefabStraightFlush() {
		return prefabStraightFlush;
	}
	public void setPrefabStraightFlush(byte prefabStraightFlush) {
		this.prefabStraightFlush = prefabStraightFlush;
		updateFieldsList.add("prefabStraightFlush");
	}
	public byte getPrefabFourOfAKindJOKER() {
		return prefabFourOfAKindJOKER;
	}
	public void setPrefabFourOfAKindJOKER(byte prefabFourOfAKindJOKER) {
		this.prefabFourOfAKindJOKER = prefabFourOfAKindJOKER;
		updateFieldsList.add("prefabFourOfAKindJOKER");
	}
	public long getSeoMachinePlayCount() {
		return seoMachinePlayCount;
	}
	public void setSeoMachinePlayCount(long seoMachinePlayCount) {
		this.seoMachinePlayCount = seoMachinePlayCount;
		updateFieldsList.add("seoMachinePlayCount");
	}

	public int getPrefabFiveBarsCount() {
		return prefabFiveBarsCount;
	}

	public void setPrefabFiveBarsCount(int prefabFiveBarsCount) {
		this.prefabFiveBarsCount = prefabFiveBarsCount;
		updateFieldsList.add("prefabFiveBarsCount");
	}

	public int getPrefabRoyalFlushCount() {
		return prefabRoyalFlushCount;
	}

	public void setPrefabRoyalFlushCount(int prefabRoyalFlushCount) {
		this.prefabRoyalFlushCount = prefabRoyalFlushCount;
		updateFieldsList.add("prefabRoyalFlushCount");
	}

	public int getPrefabFiveOfAKindCount() {
		return prefabFiveOfAKindCount;
	}

	public void setPrefabFiveOfAKindCount(int prefabFiveOfAKindCount) {
		this.prefabFiveOfAKindCount = prefabFiveOfAKindCount;
		updateFieldsList.add("prefabFiveOfAKindCount");
	}

	public int getPrefabStraightFlushCount() {
		return prefabStraightFlushCount;
	}

	public void setPrefabStraightFlushCount(int prefabStraightFlushCount) {
		this.prefabStraightFlushCount = prefabStraightFlushCount;
		updateFieldsList.add("prefabStraightFlushCount");
	}

	public int getPrefabFourOfAKindJOKERCount() {
		return prefabFourOfAKindJOKERCount;
	}

	public void setPrefabFourOfAKindJOKERCount(int prefabFourOfAKindJOKERCount) {
		this.prefabFourOfAKindJOKERCount = prefabFourOfAKindJOKERCount;
		updateFieldsList.add("prefabFourOfAKindJOKERCount");
	}
}
