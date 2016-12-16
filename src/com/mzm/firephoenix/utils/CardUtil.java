package com.mzm.firephoenix.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.entity.FivepkPrefabRandom;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;

@Component
public class CardUtil {

	public final static Log logger = LogFactory.getLog(CardUtil.class);

	@Autowired
	static JdbcDaoSupport jdbcDaoSupport;

	private static byte[] cards = new byte[]{
			// 方块 A - K
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			// 梅花 A - K
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
			// 红桃 A - K
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
			// 黑桃 A - K
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

	private final static int joker = 53;

	private final static List<Byte> jqkList = new ArrayList<Byte>(5);

	static {
		jqkList.add((byte) 11);
		jqkList.add((byte) 12);
		jqkList.add((byte) 13);
	}

	private final static byte[] prefabStraightFlush = new byte[]{5, 30, 26, 20, 16, 12, 4, 0};
	private final static byte[][] winPoolStraightFlush = {{2, 3, 4, 5, 6}, {2, 53, 4, 5, 6}, {3, 4, 5, 6, 7}, {3, 4, 5, 53, 7}};
	private final static double[] fivePrefab = new double[]{0.9, 0.91, 0.92, 0.93, 0.94, 0.95};

	public final static byte[][] WINPOOLSTRAIGHTFLUSH = {{1, 2, 3, 4, 5}, {2, 3, 4, 5, 6}, {3, 4, 5, 6, 7}, {4, 5, 6, 7, 8}, {5, 6, 7, 8, 9}, {6, 7, 8, 9, 10}, {7, 8, 9, 10, 11}, {8, 9, 10, 11, 12}, {9, 10, 11, 12, 13}};
	public final static byte[] WINPOOLFIVEBARS = {53, 53, 53, 53, 53};
	public final static byte[][] WINPOOLROYALFLUSH = {{10, 11, 12, 13, 1}};

	public final static byte[][] WINPOOLFOUROFAKINDJOKER = {{11, 24, 37, 50, 1}, {12, 25, 38, 51, 2}, {13, 26, 39, 52, 3}, {1, 14, 27, 40, 4}};
	public final static byte[][] WINPOOLFIVEOFAKIND = {{2, 15, 28, 41, 53}, {3, 16, 29, 42, 53}, {4, 17, 30, 43, 53}, {5, 18, 31, 44, 53}, {6, 19, 32, 45, 53}, {7, 20, 33, 46, 53}, {8, 21, 34, 47, 53}, {9, 22, 35, 48, 53}
	// {11, 24, 37, 50, 1}, {12, 25, 38, 51, 1}, {13, 26, 39, 52, 1}
	};

	static enum CardColor {
		Diamond(1), Club(2), Heart(3), Spade(4), Joker(5);
		private int cardColor;

		private CardColor(int cardColor) {
			this.cardColor = cardColor;
		}

		public int getCardColor() {
			return cardColor;
		}
	}

	static enum CardValue {
		Ace(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13);
		private int cardValue;

		private CardValue(int cardValue) {
			this.cardValue = cardValue;
		}

		public int getCardValue() {
			return cardValue;
		}
	}

	public static int getCardColor(int cardValue) {
		return (int) (Math.floor((cardValue - 1) / 13) + 1);
	}

	public static int getCardValue(int cardValue) {
		int value = cardValue % 13;
		if (value == 0) {
			value = 13;
		}
		return value;
	}

	public XSSFSheet getHSSFSheet(XSSFWorkbook workbook, String sheetName) {
		XSSFSheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
		}
		return sheet;
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, 10);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
	}

	public static byte[] doSort(byte[] sArr) {
		@SuppressWarnings("unchecked")
		List<Byte> list = CollectionUtils.arrayToList(sArr);
		Collections.shuffle(list);
		byte[] newArray = ArrayUtils.toPrimitive(list.toArray(new Byte[list.size()]));
		return newArray;
	}

	public static CardResult firstRandomCards(CardResult cr, FivepkSeoId fivepkSeoId, List<FivepkPrefabRandom> fivepkPrefabRandomList) {
		int nextInt = 0, newCardValue = 0;
		boolean isRepeated = false;
		// long startTime = System.currentTimeMillis();
		byte[] cardArray = new byte[5];
		Random r = new Random();
		FivepkPrefabRandom fivepkPrefabRandom = null;
		String[] prefabRandomArray = null;
		while (true) {
			logger.error("------------------------------------- loop");
			for (int i = 0; i < cardArray.length; i++) {
				logger.error("------------------------------------- loop4");
				CardUtil.cards = doSort(CardUtil.cards);
				logger.error("------------------------------------- loop6");
				fivepkPrefabRandom = fivepkPrefabRandomList.get(fivepkPrefabRandomList.size() - 1);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (Integer.parseInt(prefabRandomArray[0]) != 100 && cr.getJokerCount() <= 0 && r.nextInt(1060) < Integer.parseInt(prefabRandomArray[0])) {
					newCardValue = joker;
					cr.setJokerCount(cr.getJokerCount() + 1);
				} else {
					while (true){
						nextInt = RandomUtils.nextInt(0, CardUtil.cards.length);
						newCardValue = CardUtil.cards[nextInt];
						if (newCardValue == joker){
							continue;
						}
						break;
					}
				}
				logger.error("------------------------------------- loop7");
				for (int j = 0; j < i; j++) {
					if (newCardValue != CardUtil.cards.length && cardArray[j] == newCardValue) {
						isRepeated = true;
						break;
					}
				}
				if (isRepeated) {
					isRepeated = false;
					i--;
					continue;
				}
				cardArray[i] = (byte) newCardValue;
			}
			cr.setCards(cardArray);
			if (royalFlush(cardArray, cr).isWin()) {
				logger.error("------------------------------------- loop1");
				cr.setWin(false);
				cr.setKeepCards(null);
				cr.setJokerCount(0);
				continue;
			} else if (fiveOfAKind(cardArray, cr).isWin()) {
				logger.error("------------------------------------- loop2");
				cr.setWin(false);
				cr.setKeepCards(null);
				cr.setJokerCount(0);
				continue;
			} else if (straightFlush(cardArray, cr).isWin()) {
				logger.error("------------------------------------- loop3");
				cr.setWin(false);
				cr.setKeepCards(null);
				cr.setJokerCount(0);
				continue;
			} else if (fourOfAKindJAJoker(cardArray, cr).isWin()) {
				logger.error("------------------------------------- loop5");
				cr.setWin(false);
				cr.setKeepCards(null);
				cr.setJokerCount(0);
				continue;
			} else if (fourOfAKindJA(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(10);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			}  else if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(9);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			}  else if (fullHouse(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(8);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			} else if (flush(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(7);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			} else if (straight(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(6);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			} else if (threeOfAKind(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(5);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			} else if (sevenBetter(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(3);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			} else if (sevenBetterKeep(cardArray, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(0);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[0])) {
					cr.setWin(false);
					cr.setKeepCards(null);
					cr.setJokerCount(0);
					continue;
				}
				cr.setWin(false);
			}
			break;
		}

		// if (fiveBars(cardArray, cr).isWin()) {
		// cr.setWinType(1000);
		// } else
		// if (royalFlush(cardArray, cr).isWin()) {
		// cr.setWinType(500);
		// cr.setJokerCount(0);
		// continue;
		// } else if (fiveOfAKind(cardArray, cr).isWin()) {
		// cr.setWinType(250);
		// cr.setJokerCount(0);
		// continue;
		// } else if (straightFlush(cardArray, cr).isWin()) {
		// cr.setWinType(120);
		// cr.setJokerCount(0);
		// continue;
		// } else
		if (fourOfAKindJA(cardArray, cr).isWin()) {
			cr.setWinType(80);
		} else if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
			cr.setWinType(50);
		} else if (fullHouse(cardArray, cr).isWin()) {
			cr.setWinType(10);
		} else if (flush(cardArray, cr).isWin()) {
			cr.setWinType(7);
		} else if (straight(cardArray, cr).isWin()) {
			cr.setWinType(5);
		} else if (threeOfAKind(cardArray, cr).isWin()) {
			cr.setWinType(3);
		} else if (twoPairs(cardArray, cr).isWin()) {
			cr.setWinType(2);
		} else if (sevenBetter(cardArray, cr).isWin()) {
			cr.setWinType(1);
		} else if (fourFlush(cardArray, cr).isWin()) {
			cr.setWinType2(-1);
		} else if (fourStraight(cardArray, cr).isWin()) {
			cr.setWinType2(-2);
		} else if (fourStraightThird(cardArray, cr).isWin()) {
			cr.setWinType2(-2);
		} else if (fourStraightFourth(cardArray, cr).isWin()) {
			cr.setWinType2(-2);
		} else if (sevenBetterKeep(cardArray, cr).isWin()) {
			cr.setWinType2(-3);
		} else {
			cr.setWinType2(-4);
		}
		return cr;
	}

	public static CardResult secondRandomCards(CardResult cr, FivepkSeoId fivepkSeoId, List<FivepkPrefabRandom> fivepkPrefabRandomList) {
		cr.setWin(false);
		cr.setWinType(0);
		cr.setJokerCount(0);
		int nextInt = 0, cardValue = 0, cardColor = 0, compareCardValue = 0, compareCardColor = 0, newCardValue = 0;;
		boolean isRepeated = false;
		boolean isKeep = false;
		byte[] cards = cr.getCards();
		byte[] oldkeepCards = null;
		byte[] keepCards = cr.getKeepCards();
		if (keepCards != null) {
			oldkeepCards = Arrays.copyOf(keepCards, 5);
		}
		byte index = 0, sortedCardValue = 0, repeatedCardValue = 0;
		Random r = new Random();
		FivepkPrefabRandom fivepkPrefabRandom = null;
		String[] prefabRandomArray = null;
		while (true) {
			logger.error("------------------------------------- 2loop");
			for (int i = 0; i < cards.length; i++) {
				if (keepCards != null) {
					for (int k = 0; k < keepCards.length; k++) {
						if (i == keepCards[k]) {
							isKeep = true;
							break;
						}
					}
				}
				if (isKeep) {
					isKeep = false;
					continue;
				}
				logger.error("------------------------------------- 2loop4");
				CardUtil.cards = doSort(CardUtil.cards);
				logger.error("------------------------------------- 2loop6");
				fivepkPrefabRandom = fivepkPrefabRandomList.get(fivepkPrefabRandomList.size() - 1);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (Integer.parseInt(prefabRandomArray[0]) != 100 && cr.getJokerCount() <= 0 && r.nextInt(1060) < Integer.parseInt(prefabRandomArray[1])) {
					newCardValue = joker;
					cr.setJokerCount(cr.getJokerCount() + 1);
				} else {
					while (true){
						nextInt = RandomUtils.nextInt(0, CardUtil.cards.length);
						newCardValue = CardUtil.cards[nextInt];
						if (newCardValue == joker){
							continue;
						}
						break;
					}
				}
				logger.error("------------------------------------- 2loop7");
				compareCardColor = getCardColor(newCardValue);
				compareCardValue = getCardValue(newCardValue);
				if (keepCards != null) {
					for (int j = 0; j < cards.length; j++) {
						cardColor = getCardColor(cards[j]);
						cardValue = getCardValue(cards[j]);
						if (newCardValue != CardUtil.cards.length) {
							if (cards[j] == newCardValue
							// cardColor == compareCardColor ||
							// cardValue == compareCardValue
							) {
								isRepeated = true;
								break;
							}
						}
					}
				} else {
					for (int j = 0; j < i; j++) {
						if (newCardValue != CardUtil.cards.length && cards[j] == newCardValue) {
							isRepeated = true;
							break;
						}
					}
				}
				if (isRepeated) {
					isRepeated = false;
					i--;
					continue;
				}
				cards[i] = (byte) newCardValue;
			}

			if (royalFlush(cards, cr).isWin()) {
				logger.error("------------------------------------- 2loop1");
				cr.setWin(false);
				cr.setKeepCards(oldkeepCards);
				cr.setJokerCount(0);
				continue;
			} else if (fiveOfAKind(cards, cr).isWin()) {
				logger.error("------------------------------------- 2loop2");
				cr.setWin(false);
				cr.setKeepCards(oldkeepCards);
				cr.setJokerCount(0);
				continue;
			} else if (straightFlush(cards, cr).isWin()) {
				logger.error("------------------------------------- 2loop3");
				cr.setWin(false);
				cr.setKeepCards(oldkeepCards);
				cr.setJokerCount(0);
				continue;
			} else if (fourOfAKindJAJoker(cards, cr).isWin()) {
				logger.error("------------------------------------- 2loop5");
				cr.setWin(false);
				cr.setKeepCards(oldkeepCards);
				cr.setJokerCount(0);
				continue;
			} else if (fourOfAKindJA(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(10);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			}  else if (fourOfAKindTwoTen(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(9);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (fullHouse(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(8);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 10 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (flush(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(7);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 10 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (straight(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(6);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 10 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (threeOfAKind(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(5);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 3 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (twoPairs(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(4);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 2 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (sevenBetter(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(3);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType() < 1 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			} else if (sevenBetterKeep(cards, cr).isWin()) {
				fivepkPrefabRandom = fivepkPrefabRandomList.get(0);
				prefabRandomArray = fivepkPrefabRandom.getPrefab(fivepkSeoId.getPrefab(fivepkPrefabRandom.getPrefabCards()));
				if (cr.getWinType2() < -3 && r.nextInt(100) < Integer.parseInt(prefabRandomArray[1])) {
					if (cr.getWinType2() == -4) {
						cr.setKeepCards(null);
					}
					cr.setWin(false);
					continue;
				}
				cr.setWin(false);
			}
			break;
		}

		// if (fiveBars(cards, cr).isWin()) {
		// cr.setWinType(1000);
		// } else if (royalFlush(cards, cr).isWin()) {
		// cr.setWinType(500);
		// } else if (fiveOfAKind(cards, cr).isWin()) {
		// cr.setWinType(250);
		// } else if (straightFlush(cards, cr).isWin()) {
		// cr.setWinType(120);
		// } else
		if (fourOfAKindJA(cards, cr).isWin()) {
			cr.setWinType(80);
		} else if (fourOfAKindTwoTen(cards, cr).isWin()) {
			cr.setWinType(50);
		} else if (fullHouse(cards, cr).isWin()) {
			cr.setWinType(10);
		} else if (flush(cards, cr).isWin()) {
			cr.setWinType(7);
		} else if (straight(cards, cr).isWin()) {
			cr.setWinType(5);
		} else if (threeOfAKind(cards, cr).isWin()) {
			cr.setWinType(3);
		} else if (twoPairs(cards, cr).isWin()) {
			cr.setWinType(2);
		} else if (sevenBetter(cards, cr).isWin()) {
			cr.setWinType(1);
		}
		return cr;
	}

	public static CardResult secondRandomCards2(CardResult cr) {
		cr.setWin(false);
		cr.setWinType(0);
		int nextInt = 0, cardValue = 0, cardColor = 0, compareCardValue = 0, compareCardColor = 0;
		boolean isRepeated = false;
		boolean isKeep = false;
		byte[] cards = cr.getCards();
		byte[] keepCards = cr.getKeepCards();
		byte index = 0, sortedCardValue = 0, repeatedCardValue = 0;
		for (int i = 0; i < cards.length; i++) {
			if (keepCards != null) {
				for (int k = 0; k < keepCards.length; k++) {
					if (i == keepCards[k]) {
						isKeep = true;
						break;
					}
				}
			}
			if (isKeep) {
				isKeep = false;
				continue;
			}
			nextInt = RandomUtils.nextInt(0, CardUtil.cards.length) + 1;
			if (cr.getJokerCount() >= 2) {
				nextInt = RandomUtils.nextInt(0, CardUtil.cards.length);
			} else {
				nextInt = RandomUtils.nextInt(0, CardUtil.cards.length) + 1;
			}

			if (nextInt == joker) {
				cr.setJokerCount(cr.getJokerCount() + 1);
			}
			compareCardColor = getCardColor(nextInt);
			compareCardValue = getCardValue(nextInt);
			if (keepCards != null) {
				for (int j = 0; j < cards.length; j++) {
					cardColor = getCardColor(cards[j]);
					cardValue = getCardValue(cards[j]);
					if (nextInt != CardUtil.cards.length) {
						if (cards[j] == nextInt
						// cardColor == compareCardColor ||
						// cardValue == compareCardValue
						) {
							isRepeated = true;
							break;
						}
					}
				}
			} else {
				for (int j = 0; j < i; j++) {
					if (nextInt != CardUtil.cards.length && cards[j] == nextInt) {
						isRepeated = true;
						break;
					}
				}
			}
			if (isRepeated) {
				isRepeated = false;
				i--;
				continue;
			}
			cards[i] = (byte) nextInt;
		}

		return cr;
	}

	public static CardResult sevenBetterKeep(byte[] cards, CardResult cr) {
		int card = 0, firstCardValue = 0, secondCardValue = 0, maxValue = 0, maxIndex = 0;
		boolean hasJoker = false, isWin = false;
		List<Byte> keepList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				hasJoker = true;
				keepList.add((byte) i);
				continue;
			}
			firstCardValue = getCardValue(card);
			if (firstCardValue < 7) {
				if (firstCardValue == 1) {
					maxValue = firstCardValue;
					maxIndex = i;
				} else if (maxValue != 1 && firstCardValue > maxValue) {
					maxValue = firstCardValue;
					maxIndex = i;
				}
				if (hasJoker) {
					isWin = true;
				}
				for (int j = i + 1; j < cards.length; j++) {
					card = cards[j];
					secondCardValue = getCardValue(card);
					if (card == joker) {
						isWin = true;
					}
					if (secondCardValue < 7) {
						if (firstCardValue == secondCardValue) {
							keepList.add((byte) i);
							keepList.add((byte) j);
							cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
							return cr;
						}
					}
				}
			}
		}
		if (isWin) {
			keepList.add((byte) maxIndex);
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		return cr;
	}

	public static CardResult sevenBetter(byte[] cards, CardResult cr) {
		int card = 0, firstCardValue = 0, secondCardValue = 0, maxValue = 0, maxIndex = 0;
		boolean hasJoker = false, isWin = false;
		List<Byte> keepList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				hasJoker = true;
				keepList.add((byte) i);
				continue;
			}
			firstCardValue = getCardValue(card);
			if (firstCardValue >= 7 || firstCardValue == 1) {
				if (firstCardValue == 1) {
					maxValue = firstCardValue;
					maxIndex = i;
				} else if (maxValue != 1 && firstCardValue > maxValue) {
					maxValue = firstCardValue;
					maxIndex = i;
				}
				if (hasJoker) {
					isWin = true;
				}
				for (int j = i + 1; j < cards.length; j++) {
					card = cards[j];
					secondCardValue = getCardValue(card);
					if (card == joker) {
						isWin = true;
					}
					if (secondCardValue >= 7 || secondCardValue == 1) {
						if (firstCardValue == secondCardValue) {
							keepList.add((byte) i);
							keepList.add((byte) j);
							cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
							return cr;
						}
					}
				}
			}
		}
		if (isWin) {
			keepList.add((byte) maxIndex);
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		return cr;
	}

	public static CardResult twoPairs(byte[] cards, CardResult cr) {
		int card = 0, firstCardValue = 0, secondCardValue = 0, count = 0;
		List<Byte> keepList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				count++;
				keepList.add((byte) i);
				continue;
			}
			firstCardValue = getCardValue(card);
			for (int j = i + 1; j < cards.length; j++) {
				card = cards[j];
				if (card == joker) {
					continue;
				}
				secondCardValue = getCardValue(card);
				if (firstCardValue == secondCardValue) {
					keepList.add((byte) i);
					keepList.add((byte) j);
					count++;
				}
			}
		}
		if (count >= 2) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		return cr;
	}

	public static CardResult threeOfAKind(byte[] cards, CardResult cr) {
		int cardValue = 0, card = 0, jokerCount = 0, doubleMaxValue = 0, singleMaxValue = 0;
		Byte count = null;
		Map<Integer, Byte> countMap = new HashMap<Integer, Byte>(5);
		Map<Integer, List<Byte>> keepCardMap = new HashMap<Integer, List<Byte>>(5);
		List<Byte> keepCardList = null;
		List<Byte> keepList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				continue;
			}
			cardValue = getCardValue(card);
			count = countMap.get(cardValue);
			if (cardValue == 1) {
				singleMaxValue = cardValue;
			} else if (singleMaxValue != 1 && cardValue > singleMaxValue) {
				singleMaxValue = cardValue;
			}
			if (count == null) {
				count = 1;
			} else {
				count++;
				if (cardValue == 1) {
					doubleMaxValue = cardValue;
				} else if (doubleMaxValue != 1 && cardValue > doubleMaxValue) {
					doubleMaxValue = cardValue;
				}
			}
			countMap.put(cardValue, count);
			keepCardList = keepCardMap.get(cardValue);
			if (keepCardList == null) {
				keepCardList = new ArrayList<Byte>();
				keepCardMap.put(cardValue, keepCardList);
			}
			keepCardList.add((byte) i);
		}
		if (jokerCount == 1 && doubleMaxValue != 0) {
			keepList.addAll(keepCardMap.get(doubleMaxValue));
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		if (jokerCount == 2 && singleMaxValue != 0) {
			keepList.addAll(keepCardMap.get(singleMaxValue));
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}

		for (int key : countMap.keySet()) {
			count = countMap.get(key);
			if (count >= 3) {
				keepList.addAll(keepCardMap.get(key));
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}
		return cr;
	}

	public static CardResult straight(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, sum = 0, jokerCount = 0, aIndex = 0, maxValue = 0;
		byte card = 0, gapArray = 0;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				cardValue = getCardValue(card);
				if (cardValue > maxValue) {
					maxValue = cardValue;
				}
				if (cardValue == 1) {
					sortedCards[i] = 14;
					isA = true;
					aIndex = i;
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					indexMap.put(cardValue, i);
				}
				sum += cardValue;
			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 4) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = card;
			}
		}
		if (maxValue <= 5 && isA) {
			sortedCards[aIndex] = 1;
		}
		Arrays.sort(sortedCards);
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker && sortedCards[i - 1] != 0) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b >= 0) {
					gapArray += b;
					if (!keepList.contains(indexMap.get((int) sortedCards[i]).byteValue())) {
						keepList.add(indexMap.get((int) sortedCards[i]).byteValue());
					}
					if (!keepList.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
						keepList.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
					}
				} else {
					return cr;
				}
			}
		}
		if (gapArray <= jokerCount) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		} else if (sum / 5 == 0) {
			cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
		}
		return cr;
	}

	public static CardResult flush(byte[] cards, CardResult cr) {
		byte cardColor = 0, card = 0, jokerCount = 0;
		Map<Byte, Byte> cardColorCountMap = new HashMap<Byte, Byte>(5);
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepList = new ArrayList<Byte>();
		List<Byte> keepCardList = null;
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				continue;
			}
			cardColor = (byte) getCardColor(card);
			Byte count = cardColorCountMap.get(cardColor);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			cardColorCountMap.put(cardColor, count);
			keepCardList = keepCardMap.get(cardColor);
			if (keepCardList == null) {
				keepCardList = new ArrayList<Byte>();
				keepCardMap.put(cardColor, keepCardList);
			}
			keepCardList.add((byte) i);
		}
		for (byte key : cardColorCountMap.keySet()) {
			if (cardColorCountMap.get(key) + jokerCount >= 5) {
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}
		return cr;
	}

	public static CardResult fullHouse(byte[] cards, CardResult cr) {
		byte cardValue = 0, card = 0;
		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
		for (int i = 0; i < cards.length; i++) {
			if (cardValueCountMap.size() > 2) {
				return cr;
			}
			card = cards[i];
			if (card == joker) {
				continue;
			}
			cardValue = (byte) getCardValue(card);
			Byte count = cardValueCountMap.get(cardValue);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			cardValueCountMap.put(cardValue, count);
		}
		if (cardValueCountMap.size() == 2) {
			cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
			return cr;
		}
		return cr;
	}

	public static CardResult fourOfAKindTwoTen(byte[] cards, CardResult cr) {
		byte cardValue = 0, card = 0, jokerCount = 0, maxCardValue = 0;
		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
		Byte count = 0;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepCardList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				continue;
			}
			cardValue = (byte) getCardValue(card);
			if (2 <= cardValue && cardValue <= 10) {
				if (cardValue == 1) {
					maxCardValue = cardValue;
				} else if (maxCardValue != 1 && cardValue > maxCardValue) {
					maxCardValue = cardValue;
				}
				count = cardValueCountMap.get(cardValue);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				keepCardList = keepCardMap.get(cardValue);
				if (keepCardList == null) {
					keepCardList = new ArrayList<Byte>();
					keepCardMap.put(cardValue, keepCardList);
				}
				keepCardList.add((byte) i);
				cardValueCountMap.put(cardValue, count);
			}
		}
		if (jokerCount == 3) {
			if (maxCardValue != 0) {
				keepList.addAll(keepCardMap.get(maxCardValue));
			} else {
				return cr;
			}
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		} else {
			for (Byte key : cardValueCountMap.keySet()) {
				count = cardValueCountMap.get(key);
				if (count + jokerCount >= 4) {
					keepList.addAll(keepCardMap.get(key));
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
			}
		}
		return cr;
	}

	public static CardResult fourOfAKindJAJoker(byte[] cards, CardResult cr) {
		byte cardValue = 0, card = 0, jokerCount = 0, maxCardValue = 0;
		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
		Byte count = 0;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepCardList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				return cr;
			}
			cardValue = (byte) getCardValue(card);
			if (1 == cardValue || cardValue > 10) {
				if (cardValue == 1) {
					maxCardValue = cardValue;
				} else if (maxCardValue != 1 && cardValue > maxCardValue) {
					maxCardValue = cardValue;
				}
				count = cardValueCountMap.get(cardValue);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				keepCardList = keepCardMap.get(cardValue);
				if (keepCardList == null) {
					keepCardList = new ArrayList<Byte>();
					keepCardMap.put(cardValue, keepCardList);
				}
				keepCardList.add((byte) i);
				cardValueCountMap.put(cardValue, count);
			}
		}
		if (jokerCount == 3) {
			if (maxCardValue != 0) {
				keepList.addAll(keepCardMap.get(maxCardValue));
			} else {
				return cr;
			}
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		} else {
			for (Byte key : cardValueCountMap.keySet()) {
				count = cardValueCountMap.get(key);
				if (count + jokerCount >= 4) {
					keepList.addAll(keepCardMap.get(key));
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
			}
		}
		return cr;
	}

	public static CardResult fourOfAKindJA(byte[] cards, CardResult cr) {
		byte cardValue = 0, card = 0, jokerCount = 0, maxCardValue = 0;
		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
		Byte count = 0;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepCardList = new ArrayList<Byte>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				continue;
			}
			cardValue = (byte) getCardValue(card);
			if (1 == cardValue || cardValue > 10) {
				if (cardValue == 1) {
					maxCardValue = cardValue;
				} else if (maxCardValue != 1 && cardValue > maxCardValue) {
					maxCardValue = cardValue;
				}
				count = cardValueCountMap.get(cardValue);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				keepCardList = keepCardMap.get(cardValue);
				if (keepCardList == null) {
					keepCardList = new ArrayList<Byte>();
					keepCardMap.put(cardValue, keepCardList);
				}
				keepCardList.add((byte) i);
				cardValueCountMap.put(cardValue, count);
			}
		}
		if (jokerCount == 3) {
			if (maxCardValue != 0) {
				keepList.addAll(keepCardMap.get(maxCardValue));
			} else {
				return cr;
			}
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		} else {
			for (Byte key : cardValueCountMap.keySet()) {
				count = cardValueCountMap.get(key);
				if (count + jokerCount >= 4) {
					keepList.addAll(keepCardMap.get(key));
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
			}
		}
		return cr;
	}

	public static CardResult straightFlush(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, sum = 0, jokerCount = 0, cardColor = 0, aIndex = 0, maxValue = 0;
		byte card = 0, gapArray = 0;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				if (cardColor == 0) {
					cardColor = getCardColor(card);
				} else {
					if (cardColor != getCardColor(card)) {
						return cr;
					}
				}
				cardValue = getCardValue(card);
				if (cardValue > maxValue) {
					maxValue = cardValue;
				}
				if (cardValue == 1) {
					sortedCards[i] = 14;
					isA = true;
					aIndex = i;
					sum += 14;
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					sum += cardValue;
					indexMap.put(cardValue, i);
				}

			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 4) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = (byte) cardValue;
			}
		}
		if (maxValue <= 5 && isA) {
			sortedCards[aIndex] = 1;
		}
		Arrays.sort(sortedCards);
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker && sortedCards[i - 1] != 0) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b >= 0) {
					gapArray += b;
				}
				if (!keepList.contains(indexMap.get((int) sortedCards[i]).byteValue())) {
					keepList.add(indexMap.get((int) sortedCards[i]).byteValue());
				}
				if (!keepList.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
					keepList.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
				}
			}
		}
		if (gapArray <= jokerCount) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		if (sum / 5 == 0) {
			cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
			return cr;
		}
		return cr;
	}

	public static CardResult straightFlushWithPrefab(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, sum = 0, jokerCount = 0, cardColor = 0, aIndex = 0, maxValue = 0;
		byte card = 0, gapArray = 0;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				if (cardColor == 0) {
					cardColor = getCardColor(card);
				} else {
					if (cardColor != getCardColor(card)) {
						return cr;
					}
				}
				cardValue = getCardValue(card);
				if (cardValue > maxValue) {
					maxValue = cardValue;
				}
				if (cardValue == 1) {
					sortedCards[i] = 14;
					isA = true;
					aIndex = i;
					sum += 14;
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					sum += cardValue;
					indexMap.put(cardValue, i);
				}

			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 4) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = (byte) cardValue;
			}
		}
		if (maxValue <= 5 && isA) {
			sortedCards[aIndex] = 1;
		}
		Arrays.sort(sortedCards);
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker && sortedCards[i - 1] != 0) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b >= 0) {
					gapArray += b;
				}
				if (!keepList.contains(indexMap.get((int) sortedCards[i]).byteValue())) {
					keepList.add(indexMap.get((int) sortedCards[i]).byteValue());
				}
				if (!keepList.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
					keepList.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
				}
			}
		}
		if (gapArray <= jokerCount) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		if (sum / 5 == 0) {
			cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
			return cr;
		}
		return cr;
	}

	public static CardResult fiveOfAKind(byte[] cards, CardResult cr) {
		byte cardValue = 0, card = 0, jokerCount = 0;
		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
		Byte count = 0;
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepList = new ArrayList<Byte>();
		List<Byte> keepCardList = null;
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				// if (count + jokerCount >= 5) {
				// cr.setAfterWin(true,
				// ArrayUtils.toPrimitive(keepList.toArray(new
				// Byte[keepList.size()])));
				// return cr;
				// }
				continue;
			}
			cardValue = (byte) getCardValue(card);
			count = cardValueCountMap.get(cardValue);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			cardValueCountMap.put(cardValue, count);
			keepCardList = keepCardMap.get(cardValue);
			if (keepCardList == null) {
				keepCardList = new ArrayList<Byte>();
				keepCardMap.put(cardValue, keepCardList);
			}
			keepCardList.add((byte) i);
			if (count + jokerCount >= 5) {
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}
		for (byte key : cardValueCountMap.keySet()) {
			if (cardValueCountMap.get(key) + jokerCount >= 5) {
				keepCardList = keepCardMap.get(key);
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}
		return cr;
	}

	public static CardResult royalFlush(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, sum = 0, jokerCount = 0, cardColor = 0;
		byte card = 0, gapArray = 0;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				if (cardColor == 0) {
					cardColor = getCardColor(card);
				} else {
					if (cardColor != getCardColor(card)) {
						return cr;
					}
				}
				cardValue = getCardValue(card);
				if (cardValue == 1) {
					sortedCards[i] = 14;
					indexMap.put(14, i);
				} else if (cardValue >= 10) {
					sortedCards[i] = (byte) cardValue;
					indexMap.put(cardValue, i);
				} else {
					return cr;
				}
				sum += cardValue;
			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 4) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = card;
			}
		}
		Arrays.sort(sortedCards);
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker && sortedCards[i - 1] != 0) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b >= 0) {
					gapArray += b;
					if (gapArray <= jokerCount) {
						if (!keepList.contains(indexMap.get((int) sortedCards[i]).byteValue())) {
							keepList.add(indexMap.get((int) sortedCards[i]).byteValue());
						}
						if (!keepList.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
							keepList.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
						}
					}
				}
			}
		}
		if (gapArray <= jokerCount) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		} else if (sum / 5 == 0) {
			cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
		}
		return cr;
	}

	public static CardResult fiveBars(byte[] cards, CardResult cr) {
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != CardUtil.cards.length) {
				return cr;
			}
		}
		cr.setAfterWin(true, new byte[]{0, 1, 2, 3, 4});
		return cr;
	}

	public static CardResult fourFlush(byte[] cards, CardResult cr) {
		byte cardColor = 0, card = 0, jokerCount = 0;
		Map<Byte, Byte> cardColorCountMap = new HashMap<Byte, Byte>(5);
		Map<Byte, List<Byte>> keepCardMap = new HashMap<Byte, List<Byte>>(5);
		List<Byte> keepList = new ArrayList<Byte>();
		List<Byte> keepCardList = null;
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card == joker) {
				jokerCount++;
				keepList.add((byte) i);
				continue;
			}
			cardColor = (byte) getCardColor(card);
			Byte count = cardColorCountMap.get(cardColor);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			cardColorCountMap.put(cardColor, count);
			keepCardList = keepCardMap.get(cardColor);
			if (keepCardList == null) {
				keepCardList = new ArrayList<Byte>();
				keepCardMap.put(cardColor, keepCardList);
			}
			keepCardList.add((byte) i);
		}
		for (byte key : cardColorCountMap.keySet()) {
			if (cardColorCountMap.get(key) + jokerCount >= 4) {
				keepCardList = keepCardMap.get(key);
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}
		return cr;
	}

	public static CardResult fourStraight(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, jokerCount = 0, aIndex = 0, maxValue = 0;
		byte card = 0, continueCount = 0, gapArray = 0, sortedIndex = 0;;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				cardValue = getCardValue(card);
				if (cardValue > maxValue) {
					maxValue = cardValue;
				}
				if (cardValue == 1) {
					sortedCards[i] = 14;
					isA = true;
					aIndex = i;
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					indexMap.put(cardValue, i);
				}
			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 3) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = card;
			}
		}
		if (maxValue <= 4 && isA) {
			sortedCards[aIndex] = 1;
		}
		Arrays.sort(sortedCards);

		List<Byte> keepList2 = new ArrayList<Byte>();
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
				if (b == 0) {
					continueCount++;
					keepList.add(sortedIndex);
					if (continueCount + 1 + jokerCount >= 4) {
						keepList.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
						break;
					}
				} else {
					continueCount = 0;
				}

				byte c = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (c > 0) {
					gapArray += b;
					if (keepList2.size() < 4 && !keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
				}
				if (c == 0) {
					gapArray += b;
					if (keepList2.size() < 4 && !keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
					if (keepList2.size() < 4 && !keepList2.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
						keepList2.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
					}
				}
				if (c < 0 && i == 1) {
					if (keepList2.size() < 4 && !keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
				}
				if (b >= 0) {
					gapArray += b;
					if (keepList2.size() < 5) {
						if (!keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
					}
				}
				if (b < 0 && keepList2.size() < 5) {
					if (!keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
					if (!keepList2.contains(indexMap.get((int) sortedCards[i - 1]).byteValue())) {
						keepList2.add(indexMap.get((int) sortedCards[i - 1]).byteValue());
					}
				}
			}
		}
		if (continueCount + 1 + jokerCount >= 4) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		if (gapArray - jokerCount <= 1) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList2.toArray(new Byte[keepList2.size()])));
			return cr;
		}
		return cr;
	}

	// public static CardResult fourStraightSecond(byte[] cards, CardResult cr)
	// {
	// byte[] sortedCards = new byte[5];
	// int cardValue = 0, jokerCount = 0;
	// byte card = 0, continueCount = 0, gapArray = 0, sortedIndex = 0;;
	// boolean isA = false;
	// List<Byte> keepList = new ArrayList<Byte>();
	// List<Integer> aIndex = new ArrayList<Integer>();
	// Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
	// for (int i = 0; i < cards.length; i++) {
	// card = cards[i];
	// if (card != joker) {
	// cardValue = getCardValue(card);
	// if (cardValue == 1) {
	// sortedCards[i] = 1;
	// isA = true;
	// aIndex.add(i);
	// indexMap.put(1, i);
	// indexMap.put(14, i);
	// } else {
	// sortedCards[i] = (byte) cardValue;
	// indexMap.put(cardValue, i);
	// }
	// } else {
	// jokerCount++;
	// keepList.add((byte) i);
	// if (jokerCount >= 3) {
	// cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new
	// Byte[keepList.size()])));
	// return cr;
	// }
	// sortedCards[i] = card;
	// }
	// }
	// int oneToFourteen = 0;
	// for (int i = 0; i < sortedCards.length; i++) {
	// if (jqkList.contains(sortedCards[i])) {
	// oneToFourteen++;
	// }
	// }
	// if (oneToFourteen >= jqkList.size() && isA) {
	// for (int i = 0; i < aIndex.size(); i++) {
	// sortedCards[aIndex.get(i)] = 14;
	// }
	// }
	// Arrays.sort(sortedCards);
	//
	// List<Byte> keepCardList = new ArrayList<Byte>(5);
	// int totalGap = 0;
	// int gapCount = 0;
	// int oneCardGap = 0;
	// int keepJokerCount = jokerCount;
	// List<Byte> notKeepCardList = new ArrayList<Byte>(5);
	// int notKeepCardListSize = 0;
	// for (int i = sortedCards.length - 1; i > 0; i--) {
	// if (sortedCards[i] != joker) {
	// byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
	// if (b == 0) {
	// if (!keepCardList.contains(sortedCards[i])) {
	// keepCardList.add(sortedCards[i]);
	// }
	// if (!keepCardList.contains(sortedCards[i - 1])) {
	// keepCardList.add(sortedCards[i - 1]);
	// }
	// } else if (b == 1) {
	// if (keepJokerCount >= b || notKeepCardListSize >= b) {
	// if (!keepCardList.contains(sortedCards[i])) {
	// keepCardList.add(sortedCards[i]);
	// }
	// if (!keepCardList.contains(sortedCards[i - 1])) {
	// keepCardList.add(sortedCards[i - 1]);
	// }
	// keepJokerCount--;
	// notKeepCardListSize--;
	// } else {
	// totalGap += b;
	// oneCardGap++;
	// if (!keepCardList.contains(sortedCards[i])) {
	// notKeepCardList.add(sortedCards[i]);
	// notKeepCardListSize++;
	// }
	// }
	// gapCount++;
	// } else {
	//
	// if (b < 0) {
	// totalGap += -b;
	// } else {
	// totalGap += b;
	// }
	// gapCount++;
	// if (!keepCardList.contains(sortedCards[i])) {
	// notKeepCardList.add(sortedCards[i]);
	// notKeepCardListSize++;
	// }
	// if (!keepCardList.contains(sortedCards[i - 1])) {
	// notKeepCardList.add(sortedCards[i - 1]);
	// notKeepCardListSize++;
	// }
	// }
	// }
	// }
	//
	// if (gapCount >= 2 && totalGap >= 3) {
	// return cr;
	// }
	//
	// int notKeepCard = 0;
	// for (int i = sortedCards.length - 1; i > 0; i--) {
	// if (sortedCards[i] != joker) {
	// byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
	// if (b - jokerCount >= 1) {
	// if (!keepCardList.contains(sortedCards[i - 1])) {
	// notKeepCardList.add(sortedCards[i - 1]);
	// notKeepCard++;
	// }
	// if (!keepCardList.contains(sortedCards[i])) {
	// notKeepCardList.add(sortedCards[i]);
	// notKeepCard++;
	// }
	// }
	// }
	// }
	// // cr.setCards(new byte[]{7,9,10,13,14});
	// // if (totalGap > notKeepCard){
	// // return cr;
	// // }
	// // if (oneCardGap > notKeepCard && totalGap - notKeepCard > 0) {
	// // return cr;
	// // }
	// List<Byte> keepList2 = new ArrayList<Byte>();
	// for (int i = sortedCards.length - 1; i > 0; i--) {
	// if (sortedCards[i] != joker) {
	// byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
	// sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
	// if (b == 0) {
	// sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
	// if (!keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
	// if (!keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// } else if (b == 1) {
	// if (jokerCount > 0) {
	// sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
	// if (!keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
	// if (!keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// jokerCount--;
	// } else if (notKeepCard > 0) {
	// sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
	// if (!notKeepCardList.contains(sortedCards[i]) &&
	// !keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
	// if (!notKeepCardList.contains(sortedCards[i - 1]) &&
	// !keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// notKeepCard--;
	// }
	// } else if (b == 2) {
	// if (jokerCount + notKeepCard >= 2) {
	// sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
	// if (!notKeepCardList.contains(sortedCards[i]) &&
	// !keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
	// if (!notKeepCardList.contains(sortedCards[i - 1]) &&
	// !keepList2.contains(sortedIndex)) {
	// keepList2.add(sortedIndex);
	// }
	// jokerCount--;
	// notKeepCard--;
	// }
	// }
	// }
	// }
	// keepList.addAll(keepList2);
	// if (keepList.size() == 4) {
	// cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new
	// Byte[keepList.size()])));
	// return cr;
	// }
	// return cr;
	// }

	public static CardResult fourStraightThird(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, jokerCount = 0;
		byte card = 0, continueCount = 0, gapArray = 0, sortedIndex = 0, sortedIndex2 = 0;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		List<Integer> aIndex = new ArrayList<Integer>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				cardValue = getCardValue(card);
				if (cardValue == 1) {
					sortedCards[i] = 1;
					isA = true;
					aIndex.add(i);
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					indexMap.put(cardValue, i);
				}
			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 3) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = card;
			}
		}

		int oneToFourteen = 0;
		for (int i = 0; i < sortedCards.length; i++) {
			if (jqkList.contains(sortedCards[i])) {
				oneToFourteen++;
			}
		}
		if (oneToFourteen >= 2 && isA) {
			for (int i = 0; i < aIndex.size(); i++) {
				sortedCards[aIndex.get(i)] = 14;
			}
		}

		Arrays.sort(sortedCards);
		int maxContinueValue = 0;
		List<Byte> checkRepeatedList = new ArrayList<Byte>();
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
				sortedIndex2 = indexMap.get((int) sortedCards[i - 1]).byteValue();
				if (b == -1) {
					continue;
				}
				if (b == 0) {
					if (!checkRepeatedList.contains(sortedCards[i])) {
						if (!keepList.contains(sortedIndex)) {
							continueCount++;
							keepList.add(sortedIndex);
						}
						checkRepeatedList.add(sortedCards[i]);
						if (maxContinueValue < sortedCards[i]) {
							maxContinueValue = sortedCards[i];
						}
						if (continueCount + 1 + jokerCount >= 3) {
							if (!keepList.contains(sortedIndex2)) {
								keepList.add(sortedIndex2);
							}
							if (maxContinueValue < sortedCards[i - 1]) {
								maxContinueValue = sortedCards[i - 1];
							}
							break;
						}
					}
				} else {
					continueCount = 0;
					keepList.clear();
				}
			}
		}
		int firstIndex = 0;
		int firstValue = 0;
		int secondIndex = 0;
		int secondValue = 0;
		int thirdValue = 0;
		byte thirdIndex = 0;
		int resultGap = 0;
		List<Byte> keepList2 = new ArrayList<Byte>();
		// 1,4,5,7,9
		// 4,3,2,1
		// 1,2,3,5,7
		// 4,3,2,1
		// 7,53,9,2,5 先保了一对9 最好保 4张顺自带一对9
		boolean isRepeated = false;
		int checkRepeatedValue = 0;
		boolean isBreak = false;
		if (keepList.size() == 3) {
			Collections.sort(keepList);
			for (byte i = 0; i < cards.length; i++) {
				if (!keepList.contains(i)) {
					thirdValue = getCardValue(cards[i]);

					for (int j = 0; j < keepList.size(); j++) {
						firstIndex = keepList.get(j);
						firstValue = getCardValue(cards[firstIndex]);
						if (maxContinueValue > 4 && isA && firstValue == 1) {
							firstValue = 14;
						}
						if (maxContinueValue > 4 && isA && thirdValue == 1) {
							thirdValue = 14;
						}
						for (int j2 = 0; j2 < keepList.size(); j2++) {
							checkRepeatedValue = getCardValue(cards[keepList.get(j2)]);
							if (maxContinueValue > 4 && isA && checkRepeatedValue == 1) {
								checkRepeatedValue = 14;
							}
							if (thirdValue == checkRepeatedValue) {
								isRepeated = true;
							}
						}
						if (isRepeated) {
							isRepeated = false;
							break;
						}
						resultGap = thirdValue - firstValue - 1;
						thirdIndex = i;
						if (resultGap == 1 || resultGap == -3) {
							if (!keepList2.contains(thirdIndex) && !keepList.contains(thirdIndex)) {
								keepList2.add(thirdIndex);
								isBreak = true;
								break;
							}
						}
					}
					if (isBreak) {
						break;
					}
				}
			}
		}
		keepList.addAll(keepList2);
		if (keepList.size() == 4) {
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}
		return cr;
	}

	public static CardResult fourStraightFourth(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, jokerCount = 0;
		byte card = 0, continueCount = 0, gapArray = 0, sortedIndex = 0;;
		boolean isA = false;
		List<Byte> keepList = new ArrayList<Byte>();
		List<Integer> aIndex = new ArrayList<Integer>();
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			if (card != joker) {
				cardValue = getCardValue(card);
				if (cardValue == 1) {
					sortedCards[i] = 1;
					isA = true;
					aIndex.add(i);
					indexMap.put(1, i);
					indexMap.put(14, i);
				} else {
					sortedCards[i] = (byte) cardValue;
					indexMap.put(cardValue, i);
				}
			} else {
				jokerCount++;
				keepList.add((byte) i);
				if (jokerCount >= 3) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				sortedCards[i] = card;
			}
		}
		int oneToFourteen = 0;
		for (int i = 0; i < sortedCards.length; i++) {
			if (jqkList.contains(sortedCards[i])) {
				oneToFourteen++;
			}
		}
		if (oneToFourteen >= 2 && isA) {
			for (int i = 0; i < aIndex.size(); i++) {
				sortedCards[aIndex.get(i)] = 14;
			}
		}
		Arrays.sort(sortedCards);
		List<Byte> keepList2 = new ArrayList<Byte>(5);

		byte sortedValue = 0;
		for (int i = 0; i < sortedCards.length; i++) {
			sortedValue = sortedCards[i];
			List<Byte> keepList3 = new ArrayList<Byte>(5);
			for (int j = 1; j < 5; j++) {
				for (int j2 = i + 1; j2 < sortedCards.length; j2++) {
					if (sortedValue + j == sortedCards[j2]) {
						if (!keepList3.contains(indexMap.get((int) sortedCards[j2]).byteValue())) {
							keepList3.add(indexMap.get((int) sortedCards[j2]).byteValue());
						}
					}
				}
				if (keepList3.size() >= 3) {
					if (!keepList3.contains(indexMap.get((int) sortedCards[i]).byteValue())) {
						keepList3.add(indexMap.get((int) sortedCards[i]).byteValue());
						cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList3.toArray(new Byte[keepList3.size()])));
						return cr;
					}
				}

			}
		}
		return cr;
	}

	public static boolean orginalRoyalFlush(byte[] cards) {
		int card = 0, cardValue = 0, sum = 0;
		for (int i = 0; i < cards.length; i++) {
			card = cards[i];
			cardValue = getCardValue(card);
			sum += cardValue;
		}
		if (sum == 47) {
			return true;
		}
		return false;
	}

	public static byte compareCard() {
		int nextInt = RandomUtils.nextInt(1, CardUtil.cards.length);
		return (byte) nextInt;
	}
}
