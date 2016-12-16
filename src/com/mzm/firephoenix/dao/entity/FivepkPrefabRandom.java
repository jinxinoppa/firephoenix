package com.mzm.firephoenix.dao.entity;

@Entity(tableName = "fivepk_prefab_random", primaryKey = "prefabCards")
public class FivepkPrefabRandom extends AbstractEntity {

	@Column(columnName = "prefab_cards")
	private int prefabCards;
	@Column(columnName = "prefab_0")
	private String prefab0;
	@Column(columnName = "prefab_1")
	private String prefab1;
	@Column(columnName = "prefab_2")
	private String prefab2;
	@Column(columnName = "prefab_3")
	private String prefab3;
	@Column(columnName = "prefab_4")
	private String prefab4;
	@Column(columnName = "prefab_5")
	private String prefab5;
	@Column(columnName = "prefab_6")
	private String prefab6;
	@Column(columnName = "prefab_7")
	private String prefab7;

	public String[] getPrefab(int prefab) {
		switch (prefab) {
			case 0 :
				return prefab0.split(",");
			case 1 :
				return prefab1.split(",");
			case 2 :
				return prefab2.split(",");
			case 3 :
				return prefab3.split(",");
			case 4 :
				return prefab4.split(",");
			case 5 :
				return prefab5.split(",");
			case 6 :
				return prefab6.split(",");
			case 7 :
				return prefab7.split(",");
			default :
				return new String[]{"0", "0"};
		}
	}

	public int getPrefabCards() {
		return prefabCards;
	}

	public void setPrefabCards(int prefabCards) {
		this.prefabCards = prefabCards;
	}

	public String getPrefab0() {
		return prefab0;
	}

	public void setPrefab0(String prefab0) {
		this.prefab0 = prefab0;
	}

	public String getPrefab1() {
		return prefab1;
	}

	public void setPrefab1(String prefab1) {
		this.prefab1 = prefab1;
	}

	public String getPrefab2() {
		return prefab2;
	}

	public void setPrefab2(String prefab2) {
		this.prefab2 = prefab2;
	}

	public String getPrefab3() {
		return prefab3;
	}

	public void setPrefab3(String prefab3) {
		this.prefab3 = prefab3;
	}

	public String getPrefab4() {
		return prefab4;
	}

	public void setPrefab4(String prefab4) {
		this.prefab4 = prefab4;
	}

	public String getPrefab5() {
		return prefab5;
	}

	public void setPrefab5(String prefab5) {
		this.prefab5 = prefab5;
	}

	public String getPrefab6() {
		return prefab6;
	}

	public void setPrefab6(String prefab6) {
		this.prefab6 = prefab6;
	}

	public String getPrefab7() {
		return prefab7;
	}

	public void setPrefab7(String prefab7) {
		this.prefab7 = prefab7;
	}
}
