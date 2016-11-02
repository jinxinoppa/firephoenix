package com.mzm.firephoenix.utils;
//package com.mzm.poker.utils;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.lang3.RandomUtils;
//
//public class CardUtil {
//
//	private final static byte[] cards = new byte[]{
//			// 方块 A - K
//			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
//			// 梅花 A - K
//			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
//			// 红桃 A - K
//			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
//			// 黑桃 A - K
//			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
//
//	private final static int joker = 53;
//
//	static enum CardColor {
//		Diamond(1), Club(2), Heart(3), Spade(4), Joker(5);
//		private int cardColor;
//		private CardColor(int cardColor) {
//			this.cardColor = cardColor;
//		}
//
//		public int getCardColor() {
//			return cardColor;
//		}
//	}
//
//	static enum CardValue {
//		Ace(1), Two(2), Three(3), Four(4), Five(5), Six(6), Seven(7), Eight(8), Nine(9), Ten(10), Jack(11), Queen(12), King(13);
//		private int cardValue;
//		private CardValue(int cardValue) {
//			this.cardValue = cardValue;
//		}
//		public int getCardValue() {
//			return cardValue;
//		}
//	}
//
//	public static int getCardColor(int cardValue) {
//		return (int) (Math.floor((cardValue - 1) / 13) + 1);
//	}
//
//	public static int getCardValue(int cardValue) {
//		int value = cardValue % 13;
//		if (value == 0) {
//			value = 13;
//		}
//		return value;
//	}
//
//	// 发牌
//	public static byte[] randomCards() {
//		int nextInt = 0;
//		boolean isRepeated = false;
//		long startTime = System.currentTimeMillis();
//		byte[] cards = new byte[5];
//		for (int i = 0; i < cards.length; i++) {
//			nextInt = RandomUtils.nextInt(0, CardUtil.cards.length) + 1;
//			for (int j = 0; j < i; j++) {
//				if (nextInt != CardUtil.cards.length && cards[j] == nextInt) {
//					isRepeated = true;
//					break;
//				}
//			}
//			if (isRepeated) {
//				isRepeated = false;
//				i--;
//				continue;
//			}
//			cards[i] = (byte) nextInt;
//		}
//		System.out.println(Arrays.toString(cards));
//		System.out.println("duration : " + (System.currentTimeMillis() - startTime));
//		return cards;
//	}
//
//	public static boolean sevenBetter(byte[] cards) {
//		int card = 0, firstCardValue = 0, firstCardColor = 0, secondCardValue = 0, secondCardColor = 0;
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			firstCardValue = getCardValue(card);
//			if (firstCardValue > 7) {
//				firstCardColor = getCardColor(card);
//				for (int j = i + 1; j < cards.length; j++) {
//					card = cards[j];
//					if (card == joker) {
//						return true;
//					}
//					secondCardValue = getCardValue(card);
//					if (secondCardValue > 7) {
//						secondCardColor = getCardColor(card);
//						if (firstCardColor != secondCardColor && firstCardValue == secondCardValue) {
//							return true;
//						}
//					}
//					System.out.print(firstCardValue + "" + secondCardValue + ",");
//				}
//				System.out.println();
//			}
//		}
//		return false;
//	}
//
//	public static boolean twoPairs(byte[] cards) {
//		int card = 0, firstCardValue = 0, firstCardColor = 0, secondCardValue = 0, secondCardColor = 0, count = 0;
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			if (card == joker) {
//				count++;
//				continue;
//			}
//			firstCardValue = getCardValue(card);
//			firstCardColor = getCardColor(card);
//			for (int j = i + 1; j < cards.length; j++) {
//				card = cards[j];
//				secondCardValue = getCardValue(card);
//				secondCardColor = getCardColor(card);
//				if (firstCardColor != secondCardColor && firstCardValue == secondCardValue) {
//					count++;
//				}
//			}
//		}
//		if (count >= 2) {
//			return true;
//		}
//		return false;
//	}
//
//	public static boolean threeOfAKind(byte[] cards) {
//		int firstCardValue = 0, firstCard = 0, secondCardValue = 0, secondCard = 0, thirdCardValue = 0, thirdCard = 0, jokerCount = 0;
//		for (int i = 0; i < cards.length; i++) {
//			firstCard = cards[i];
//			if (firstCard == joker) {
//				jokerCount++;
//				if (jokerCount >= 2) {
//					return true;
//				}
//			}
//			firstCardValue = getCardValue(firstCard);
//			for (int j = i + 1; j < cards.length; j++) {
//				secondCard = cards[j];
//				if (secondCard == joker) {
//					jokerCount++;
//					if (jokerCount >= 2) {
//						return true;
//					}
//				}
//				secondCardValue = getCardValue(secondCard);
//
//				for (int k = j + 1; k < cards.length; k++) {
//					thirdCard = cards[k];
//					if (thirdCard == joker) {
//						jokerCount++;
//						if (jokerCount >= 2) {
//							return true;
//						}
//					}
//					thirdCardValue = getCardValue(thirdCard);
//					if (jokerCount == 1 && (firstCardValue == secondCardValue || secondCardValue == thirdCardValue || firstCardValue == thirdCardValue)) {
//						return true;
//					} else if (firstCardValue == secondCardValue && secondCardValue == thirdCardValue) {
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
//
//	public static void main(String[] args) {
//		 byte[] b = new byte[]{26, 51, 6, 10, 43};
//		// Arrays.sort(b);
//		// System.out.println(Arrays.toString(b));
////		System.out.println(straight(randomCards()));
//		System.out.println(straight(b));
//	}
//
//	public static boolean straight(byte[] cards) {
//		byte[] sortedCards = new byte[5];
//		int cardValue = 0;
//		byte card = 0;
//		int sum = 0;
//		int jokerCount = 0;
//		byte gapArray = 0;
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			if (card != joker) {
//				cardValue = getCardValue(card);
//				if (cardValue == 1) {
//					sortedCards[i] = 14;
//				} else {
//					sortedCards[i] = (byte) cardValue;
//				}
//			} else {
//				sortedCards[i] = card;
//				jokerCount++;
//				if (jokerCount >= 4) {
//					return true;
//				}
//			}
//		}
//		Arrays.sort(sortedCards);
//		for (int i = sortedCards.length - 1; i > 0; i--) {
//			byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
//			if (b > 0) {
//				gapArray += b;
//			}
//		}
//		if (gapArray <= jokerCount) {
//			return true;
//		}
//		for (int i = 0, j = sortedCards.length - 1; i < sortedCards.length && j > 0; i++, j--) {
//			cardValue = sortedCards[i];
//			sum += cardValue;
//		}
//		if (sum / 5 == 0) {
//			return true;
//		}
//		return false;
//	}
//
//	public static boolean flush(byte[] cards) {
//		byte cardColor = 0, card = 0, jokerCount = 0;
//		Map<Byte, Byte> cardColorCountMap = new HashMap<Byte, Byte>(5);
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			if (card == joker) {
//				jokerCount++;
//			}
//			cardColor = (byte) getCardColor(card);
//			Byte count = cardColorCountMap.get(cardColor);
//			if (count == null) {
//				count = 1;
//			} else {
//				count++;
//			}
//			cardColorCountMap.put(cardColor, count);
//		}
//		for (byte key : cardColorCountMap.keySet()) {
//			if (cardColorCountMap.get(key) + jokerCount >= 5) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public static boolean fullHouse(byte[] cards) {
//		byte cardValue = 0, card = 0, jokerCount = 0, totalCount = 0;
//		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
//		for (int i = 0; i < cards.length; i++) {
//			if (cardValueCountMap.size() > 2) {
//				return false;
//			}
//			card = cards[i];
//			if (card == joker) {
//				jokerCount++;
//				continue;
//			}
//			cardValue = (byte) getCardValue(card);
//			Byte count = cardValueCountMap.get(cardValue);
//			if (count == null) {
//				count = 1;
//			} else {
//				count++;
//			}
//			cardValueCountMap.put(cardValue, count);
//			totalCount++;
//		}
//		for (byte key : cardValueCountMap.keySet()) {
//			if (cardValueCountMap.get(key) > 3) {
//				return false;
//			}
//		}
//		if (cardValueCountMap.size() == 2) {
//			return true;
//		}
//		return false;
//	}
//
//	public static boolean fourOfAKindTwoTen(byte[] cards) {
//		byte cardValue = 0, card = 0, jokerCount = 0;
//		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
//		Byte count = 0;
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			if (card == joker) {
//				jokerCount++;
//				if (count + jokerCount >= 4) {
//					return true;
//				}
//				continue;
//			}
//			cardValue = (byte) getCardValue(card);
//			if (2 <= cardValue && cardValue <= 10) {
//				count = cardValueCountMap.get(cardValue);
//				if (count == null) {
//					count = 1;
//				} else {
//					count++;
//				}
//				if (count + jokerCount >= 4) {
//					return true;
//				}
//				cardValueCountMap.put(cardValue, count);
//			}
//		}
//		return false;
//	}
//
//	public static boolean fourOfAKindJA(byte[] cards) {
//		byte cardValue = 0, card = 0, jokerCount = 0;
//		Map<Byte, Byte> cardValueCountMap = new HashMap<Byte, Byte>(5);
//		Byte count = 0;
//		for (int i = 0; i < cards.length; i++) {
//			card = cards[i];
//			if (card == joker) {
//				jokerCount++;
//				if (count + jokerCount >= 4) {
//					return true;
//				}
//				continue;
//			}
//			cardValue = (byte) getCardValue(card);
//			if (1 == cardValue || cardValue > 10) {
//				count = cardValueCountMap.get(cardValue);
//				if (count == null) {
//					count = 1;
//				} else {
//					count++;
//				}
//				if (count + jokerCount >= 4) {
//					return true;
//				}
//				cardValueCountMap.put(cardValue, count);
//			}
//		}
//		return false;
//	}
//
//}
