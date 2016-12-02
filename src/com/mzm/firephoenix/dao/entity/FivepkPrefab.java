package com.mzm.firephoenix.dao.entity;

@Entity(tableName = "fivepk_prefab", primaryKey = "accountId")
public class FivepkPrefab extends AbstractEntity {

	@Column(columnName = "prefab_cards")
	private int prefabCards;
	@Column(columnName = "prefab_0")
	private int prefab0;
	@Column(columnName = "prefab_1")
	private int prefab1;
	@Column(columnName = "prefab_2")
	private int prefab2;
	@Column(columnName = "prefab_3")
	private int prefab3;
	@Column(columnName = "prefab_4")
	private int prefab4;
	@Column(columnName = "prefab_5")
	private int prefab5;
	@Column(columnName = "prefab_6")
	private int prefab6;
	@Column(columnName = "prefab_7")
	private int prefab7;
	@Column(columnName = "win_pool")
	private int winPool;

	public int getPrefab(int prefab) {
		switch (prefab) {
			case 0 :
				return prefab0;
			case 1 :
				return prefab1;
			case 2 :
				return prefab2;
			case 3 :
				return prefab3;
			case 4 :
				return prefab4;
			case 5 :
				return prefab5;
			case 6 :
				return prefab6;
			case 7 :
				return prefab7;
			default :
				return 0;
		}
	}
	public int getPrefabCards() {
		return prefabCards;
	}
	public void setPrefabCards(int prefabCards) {
		this.prefabCards = prefabCards;
	}
	public int getPrefab0() {
		return prefab0;
	}
	public void setPrefab0(int prefab0) {
		this.prefab0 = prefab0;
	}
	public int getPrefab1() {
		return prefab1;
	}
	public void setPrefab1(int prefab1) {
		this.prefab1 = prefab1;
	}
	public int getPrefab2() {
		return prefab2;
	}
	public void setPrefab2(int prefab2) {
		this.prefab2 = prefab2;
	}
	public int getPrefab3() {
		return prefab3;
	}
	public void setPrefab3(int prefab3) {
		this.prefab3 = prefab3;
	}
	public int getPrefab4() {
		return prefab4;
	}
	public void setPrefab4(int prefab4) {
		this.prefab4 = prefab4;
	}
	public int getPrefab5() {
		return prefab5;
	}
	public void setPrefab5(int prefab5) {
		this.prefab5 = prefab5;
	}
	public int getPrefab6() {
		return prefab6;
	}
	public void setPrefab6(int prefab6) {
		this.prefab6 = prefab6;
	}
	public int getPrefab7() {
		return prefab7;
	}
	public void setPrefab7(int prefab7) {
		this.prefab7 = prefab7;
	}
}
