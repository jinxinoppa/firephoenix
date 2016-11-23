package com.mzm.firephoenix.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CardUtil {

	private final static byte[] cards = new byte[]{
			// 方块 A - K
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
			// 梅花 A - K
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
			// 红桃 A - K
			27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
			// 黑桃 A - K
			40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};

	private final static int joker = 53;

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

	public void appendCards(byte[] cardArray, XSSFSheet sheet, boolean isFirst, int physicalNumberOfRow) {
		int card = 0, cardValue = 0;
		int cardColor = 0;
		String strCardValue = null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cardArray.length; i++) {
			card = cardArray[i];
			cardValue = getCardValue(card);
			switch (cardValue) {
				case 11 :
					strCardValue = "J";
					break;
				case 12 :
					strCardValue = "Q";
					break;
				case 13 :
					strCardValue = "K";
					break;
				case 1 :
					strCardValue = "A";
					break;
				default :
					strCardValue = String.valueOf(cardValue);
					break;
			}
			cardColor = getCardColor(card);
			switch (cardColor) {
				case 1 :
					sb.append(", 方块 " + strCardValue);
					break;
				case 2 :
					sb.append(", 梅花 " + strCardValue);
					break;
				case 3 :
					sb.append(", 红桃 " + strCardValue);
					break;
				case 4 :
					sb.append(", 黑桃 " + strCardValue);
					break;
				case 5 :
					sb.append(", 鬼");
					break;
				default :
					break;
			}
		}
		sb.deleteCharAt(0);
		// int physicalNumberOfRow = sheet.getPhysicalNumberOfRows();
		sheet.setDefaultColumnWidth(40);

		XSSFCell cell = null;
		XSSFRow row = sheet.getRow(physicalNumberOfRow);
		if (row == null) {
			row = sheet.createRow(physicalNumberOfRow);
		}
		if (isFirst) {
			cell = row.createCell(0);
		} else {
			cell = row.createCell(1);
		}
		cell.setCellValue(sb.toString());
	}

	public XSSFSheet getHSSFSheet(XSSFWorkbook workbook, String sheetName) {
		XSSFSheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
		}
		return sheet;
	}

	public void firstAndSecond(String count) {
		int totalCount = Integer.valueOf(count);
		byte[] cardArray = null;
		XSSFWorkbook workbook = new XSSFWorkbook();
		int fullHouseCount = 0;
		int flushCount = 0;
		int straightCount = 0;
		int threeOfAKindCount = 0;
		int twoPairsCount = 0;
		int sevenBetterCount = 0;
		int failCount = 0;
		int fourOfAKindJA = 0;
		int fourOfAKindTwoTen = 0;
		int straightFlush = 0;
		int fiveOfAKind = 0;
		int royalFlush = 0;
		int fiveBars = 0;
		int fourStraight = 0;
		int fourFlush = 0;

		int sfullHouseCount = 0;
		int sflushCount = 0;
		int sstraightCount = 0;
		int sthreeOfAKindCount = 0;
		int stwoPairsCount = 0;
		int ssevenBetterCount = 0;
		int sfailCount = 0;
		int sfourOfAKindJA = 0;
		int sfourOfAKindTwoTen = 0;
		int sstraightFlush = 0;
		int sfiveOfAKind = 0;
		int sroyalFlush = 0;
		int sfiveBars = 0;
		int sfourStraight = 0;
		int sfourFlush = 0;

		XSSFSheet sheet = null;
		for (int i = 0; i < totalCount; i++) {
			cardArray = firstRandomCards().getCards();
			CardResult cr = new CardResult();
			cr.setCards(cardArray);
			if (fiveBars(cardArray, cr).isWin()) {
				cr.setWin(false);
				fiveBars++;
				sheet = getHSSFSheet(workbook, "五鬼");
				appendCards(cardArray, sheet, true, fiveBars);
				cr.setPhysicalNumberOfRow(fiveBars);
			} else {
				if (royalFlush(cardArray, cr).isWin()) {
					cr.setWin(false);
					royalFlush++;
					sheet = getHSSFSheet(workbook, "同花大顺");
					appendCards(cardArray, sheet, true, royalFlush);
					cr.setPhysicalNumberOfRow(royalFlush);
				} else {
					if (fiveOfAKind(cardArray, cr).isWin()) {
						cr.setWin(false);
						fiveOfAKind++;
						sheet = getHSSFSheet(workbook, "五梅");
						appendCards(cardArray, sheet, true, fiveOfAKind);
						cr.setPhysicalNumberOfRow(fiveOfAKind);
					} else {
						if (straightFlush(cardArray, cr).isWin()) {
							cr.setWin(false);
							straightFlush++;
							sheet = getHSSFSheet(workbook, "同花小顺");
							appendCards(cardArray, sheet, true, straightFlush);
							cr.setPhysicalNumberOfRow(straightFlush);
						} else {
							if (fourOfAKindJA(cardArray, cr).isWin()) {
								cr.setWin(false);
								fourOfAKindJA++;
								sheet = getHSSFSheet(workbook, "大四梅");
								appendCards(cardArray, sheet, true, fourOfAKindJA);
								cr.setPhysicalNumberOfRow(fourOfAKindJA);
							} else {
								if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
									cr.setWin(false);
									fourOfAKindTwoTen++;
									sheet = getHSSFSheet(workbook, "小四梅");
									appendCards(cardArray, sheet, true, fourOfAKindTwoTen);
									cr.setPhysicalNumberOfRow(fourOfAKindTwoTen);
								} else {
									if (fullHouse(cardArray, cr).isWin()) {
										cr.setWin(false);
										fullHouseCount++;
										sheet = getHSSFSheet(workbook, "葫芦");
										appendCards(cardArray, sheet, true, fullHouseCount);
										cr.setPhysicalNumberOfRow(fullHouseCount);
									} else {
										if (flush(cardArray, cr).isWin()) {
											cr.setWin(false);
											flushCount++;
											sheet = getHSSFSheet(workbook, "同花");
											appendCards(cardArray, sheet, true, flushCount);
											cr.setPhysicalNumberOfRow(flushCount);
										} else {
											if (straight(cardArray, cr).isWin()) {
												cr.setWin(false);
												straightCount++;
												sheet = getHSSFSheet(workbook, "顺子");
												appendCards(cardArray, sheet, true, straightCount);
												cr.setPhysicalNumberOfRow(straightCount);
											} else {
												if (threeOfAKind(cardArray, cr).isWin()) {
													cr.setWin(false);
													threeOfAKindCount++;
													sheet = getHSSFSheet(workbook, "三条");
													appendCards(cardArray, sheet, true, threeOfAKindCount);
													cr.setPhysicalNumberOfRow(threeOfAKindCount);
												} else {
													if (twoPairs(cardArray, cr).isWin()) {
														cr.setWin(false);
														twoPairsCount++;
														sheet = getHSSFSheet(workbook, "两对");
														appendCards(cardArray, sheet, true, twoPairsCount);
														cr.setPhysicalNumberOfRow(twoPairsCount);
													} else {
														if (sevenBetter(cardArray, cr).isWin()) {
															cr.setWin(false);
															sevenBetterCount++;
															sheet = getHSSFSheet(workbook, "一对");
															appendCards(cardArray, sheet, true, sevenBetterCount);
															cr.setPhysicalNumberOfRow(sevenBetterCount);
														} else {
															if (fourFlush(cardArray, cr).isWin()) {
																cr.setWin(false);
																fourFlush++;
																sheet = getHSSFSheet(workbook, "四张同花");
																appendCards(cardArray, sheet, true, fourFlush);
																cr.setPhysicalNumberOfRow(fourFlush);
															} else {
																if (fourStraight(cardArray, cr).isWin()) {
																	cr.setWin(false);
																	fourStraight++;
																	sheet = getHSSFSheet(workbook, "四张顺");
																	appendCards(cardArray, sheet, true, fourStraight);
																	cr.setPhysicalNumberOfRow(fourStraight);
																} else {
																	failCount++;
																	sheet = getHSSFSheet(workbook, "乌龙");
																	appendCards(cardArray, sheet, true, failCount);
																	cr.setPhysicalNumberOfRow(failCount);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			//
			cardArray = secondRandomCards(cr).getCards();
			if (fiveBars(cardArray, cr).isWin()) {
				appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
			} else {
				if (royalFlush(cardArray, cr).isWin()) {
					appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
				} else {
					if (fiveOfAKind(cardArray, cr).isWin()) {
						appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
					} else {
						if (straightFlush(cardArray, cr).isWin()) {
							appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
						} else {
							if (fourOfAKindJA(cardArray, cr).isWin()) {
								appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
							} else {
								if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
									appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
								} else {
									if (fullHouse(cardArray, cr).isWin()) {
										appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
									} else {
										if (flush(cardArray, cr).isWin()) {
											appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
										} else {
											if (straight(cardArray, cr).isWin()) {
												appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
											} else {
												if (threeOfAKind(cardArray, cr).isWin()) {
													appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
												} else {
													if (twoPairs(cardArray, cr).isWin()) {
														appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
													} else {
														if (sevenBetter(cardArray, cr).isWin()) {
															appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
														} else {
															if (fourFlush(cardArray, cr).isWin()) {
																appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
															} else {
																if (fourStraight(cardArray, cr).isWin()) {
																	appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
																} else {
																	appendCards(cardArray, sheet, false, cr.getPhysicalNumberOfRow());
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(new File("一手二手发牌.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printAndExcel(String count) {
		int totalCount = Integer.valueOf(count);
		int fullHouseCount = 0;
		int flushCount = 0;
		int straightCount = 0;
		int threeOfAKindCount = 0;
		int twoPairsCount = 0;
		int sevenBetterCount = 0;
		int failCount = 0;
		int fourOfAKindJA = 0;
		int fourOfAKindTwoTen = 0;
		int straightFlush = 0;
		int fiveOfAKind = 0;
		int royalFlush = 0;
		int fiveBars = 0;
		int fourStraight = 0;
		int fourFlush = 0;
		byte[] cardArray = null;

		XSSFWorkbook workbook = new XSSFWorkbook();

		List<CardResult> secondList = new ArrayList<CardResult>();
		for (int i = 0; i < totalCount; i++) {
			cardArray = firstRandomCards().getCards();
			CardResult cr = new CardResult();
			secondList.add(cr);
			cr.setCards(cardArray);
			if (fiveBars(cardArray, cr).isWin()) {
				cr.setWin(false);
				fiveBars++;
				// appendCards(cardArray, getHSSFSheet(workbook, "五鬼"), true,
				// fiveBars);
			} else if (royalFlush(cardArray, cr).isWin()) {
				cr.setWin(false);
				royalFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花大顺"), true,
				// royalFlush);
			} else if (fiveOfAKind(cardArray, cr).isWin()) {
				cr.setWin(false);
				fiveOfAKind++;
				// appendCards(cardArray, getHSSFSheet(workbook, "五梅"), true,
				// fiveOfAKind);
			} else if (straightFlush(cardArray, cr).isWin()) {
				cr.setWin(false);
				straightFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花小顺"), true,
				// straightFlush);
			} else if (fourOfAKindJA(cardArray, cr).isWin()) {
				cr.setWin(false);
				fourOfAKindJA++;
				// appendCards(cardArray, getHSSFSheet(workbook, "大四梅"), true,
				// fourOfAKindJA);
			} else if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
				cr.setWin(false);
				fourOfAKindTwoTen++;
				// appendCards(cardArray, getHSSFSheet(workbook, "小四梅"), true,
				// fourOfAKindTwoTen);
				continue;
			} else if (fullHouse(cardArray, cr).isWin()) {
				cr.setWin(false);
				fullHouseCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "葫芦"), true,
				// fullHouseCount);
			} else if (flush(cardArray, cr).isWin()) {
				cr.setWin(false);
				flushCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花"), true,
				// flushCount);
			} else if (straight(cardArray, cr).isWin()) {
				cr.setWin(false);
				straightCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "顺子"), true,
				// straightCount);
			} else if (threeOfAKind(cardArray, cr).isWin()) {
				cr.setWin(false);
				threeOfAKindCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "三条"), true,
				// threeOfAKindCount);
			} else if (twoPairs(cardArray, cr).isWin()) {
				cr.setWin(false);
				twoPairsCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "两对"), true,
				// twoPairsCount);
			} else if (sevenBetter(cardArray, cr).isWin()) {
				cr.setWin(false);
				sevenBetterCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "一对"), true,
				// sevenBetterCount);
			} else if (fourFlush(cardArray, cr).isWin()) {
				cr.setWin(false);
				fourFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "四张同花"), true,
				// fourFlush);
			} else if (fourStraight(cardArray, cr).isWin()) {
				cr.setWin(false);
				fourStraight++;
				// appendCards(cardArray, getHSSFSheet(workbook, "四张顺"), true,
				// fourStraight);
			} else {
				cr.setWin(false);
				failCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "乌龙"), firstSB,
				// true);
			}
		}
		System.out.println("五鬼: " + fiveBars + " | " + (fiveBars == 0 ? 0 : totalCount / fiveBars) + " | " + div(fiveBars, totalCount));
		System.out.println("同花大顺: " + royalFlush + " | " + (royalFlush == 0 ? 0 : totalCount / royalFlush) + " | " + div(royalFlush, totalCount));
		System.out.println("五梅: " + fiveOfAKind + " | " + (fiveOfAKind == 0 ? 0 : totalCount / fiveOfAKind) + " | " + div(fiveOfAKind, totalCount));
		System.out.println("同花小顺: " + straightFlush + " | " + (straightFlush == 0 ? 0 : totalCount / straightFlush) + " | " + div(straightFlush, totalCount));
		System.out.println("大四梅: " + fourOfAKindJA + " | " + (fourOfAKindJA == 0 ? 0 : totalCount / fourOfAKindJA) + " | " + div(fourOfAKindJA, totalCount));
		System.out.println("小四梅: " + fourOfAKindTwoTen + " | " + (fourOfAKindTwoTen == 0 ? 0 : totalCount / fourOfAKindTwoTen) + " | " + div(fourOfAKindTwoTen, totalCount));
		System.out.println("葫芦: " + fullHouseCount + " | " + (fullHouseCount == 0 ? 0 : totalCount / fullHouseCount) + " | " + div(fullHouseCount, totalCount));
		System.out.println("同花: " + flushCount + " | " + (flushCount == 0 ? 0 : totalCount / flushCount) + " | " + div(flushCount, totalCount));
		System.out.println("顺子: " + straightCount + " | " + (straightCount == 0 ? 0 : totalCount / straightCount) + " | " + div(straightCount, totalCount));
		System.out.println("三条: " + threeOfAKindCount + " | " + (threeOfAKindCount == 0 ? 0 : totalCount / threeOfAKindCount) + " | " + div(threeOfAKindCount, totalCount));
		System.out.println("两对: " + twoPairsCount + " | " + (twoPairsCount == 0 ? 0 : totalCount / twoPairsCount) + " | " + div(twoPairsCount, totalCount));
		System.out.println("一对: " + sevenBetterCount + " | " + (sevenBetterCount == 0 ? 0 : totalCount / sevenBetterCount) + " | " + div(sevenBetterCount, totalCount));
		System.out.println("四張同花: " + fourFlush + " | " + (fourFlush == 0 ? 0 : totalCount / fourFlush) + " | " + div(fourFlush, totalCount));
		System.out.println("四張順: " + fourStraight + " | " + (fourStraight == 0 ? 0 : totalCount / fourStraight) + " | " + div(fourStraight, totalCount));
		System.out.println("乌龙: " + failCount + " | " + (failCount == 0 ? 0 : totalCount / failCount) + " | " + div(failCount, totalCount));
		System.out.println("--------------------第一手牌结束--------------------");
		System.out.println();
		fullHouseCount = 0;
		flushCount = 0;
		straightCount = 0;
		threeOfAKindCount = 0;
		twoPairsCount = 0;
		sevenBetterCount = 0;
		failCount = 0;
		fourOfAKindJA = 0;
		fourOfAKindTwoTen = 0;
		straightFlush = 0;
		fiveOfAKind = 0;
		royalFlush = 0;
		fiveBars = 0;
		fourStraight = 0;
		fourFlush = 0;
		for (int i = 0; i < secondList.size(); i++) {
			CardResult cr = secondList.get(i);
			cardArray = secondRandomCards(cr).getCards();
			if (fiveBars(cardArray, cr).isWin()) {
				fiveBars++;
				// appendCards(cardArray, getHSSFSheet(workbook, "五鬼"), false,
				// fiveBars);
			} else if (royalFlush(cardArray, cr).isWin()) {
				royalFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花大顺"), false,
				// royalFlush);
			} else if (fiveOfAKind(cardArray, cr).isWin()) {
				fiveOfAKind++;
				// appendCards(cardArray, getHSSFSheet(workbook, "五梅"), false,
				// fiveOfAKind);
			} else if (straightFlush(cardArray, cr).isWin()) {
				straightFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花小顺"), false,
				// straightFlush);
			} else if (fourOfAKindJA(cardArray, cr).isWin()) {
				fourOfAKindJA++;
				// appendCards(cardArray, getHSSFSheet(workbook, "大四梅"), false,
				// fourOfAKindJA);
			} else if (fourOfAKindTwoTen(cardArray, cr).isWin()) {
				fourOfAKindTwoTen++;
				// appendCards(cardArray, getHSSFSheet(workbook, "小四梅"), false,
				// fourOfAKindTwoTen);
			} else if (fullHouse(cardArray, cr).isWin()) {
				fullHouseCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "葫芦"), false,
				// fullHouseCount);
			} else if (flush(cardArray, cr).isWin()) {
				flushCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "同花"), false,
				// flushCount);
			} else if (straight(cardArray, cr).isWin()) {
				straightCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "顺子"), false,
				// straightCount);
			} else if (threeOfAKind(cardArray, cr).isWin()) {
				threeOfAKindCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "三条"), false,
				// threeOfAKindCount);
			} else if (twoPairs(cardArray, cr).isWin()) {
				twoPairsCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "两对"), false,
				// twoPairsCount);
			} else if (sevenBetter(cardArray, cr).isWin()) {
				sevenBetterCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "一对"), false,
				// sevenBetterCount);
			} else if (fourFlush(cardArray, cr).isWin()) {
				fourFlush++;
				// appendCards(cardArray, getHSSFSheet(workbook, "四张同花"), false,
				// fourFlush);
			} else if (fourStraight(cardArray, cr).isWin()) {
				fourStraight++;
				// appendCards(cardArray, getHSSFSheet(workbook, "四张顺"), false,
				// fourStraight);
			} else {
				failCount++;
				// appendCards(cardArray, getHSSFSheet(workbook, "乌龙"));
			}
		}

		System.out.println("五鬼: " + fiveBars + " | " + (fiveBars == 0 ? 0 : totalCount / fiveBars) + " | " + div(fiveBars, totalCount));
		System.out.println("同花大顺: " + royalFlush + " | " + (royalFlush == 0 ? 0 : totalCount / royalFlush) + " | " + div(royalFlush, totalCount));
		System.out.println("五梅: " + fiveOfAKind + " | " + (fiveOfAKind == 0 ? 0 : totalCount / fiveOfAKind) + " | " + div(fiveOfAKind, totalCount));
		System.out.println("同花小顺: " + straightFlush + " | " + (straightFlush == 0 ? 0 : totalCount / straightFlush) + " | " + div(straightFlush, totalCount));
		System.out.println("大四梅: " + fourOfAKindJA + " | " + (fourOfAKindJA == 0 ? 0 : totalCount / fourOfAKindJA) + " | " + div(fourOfAKindJA, totalCount));
		System.out.println("小四梅: " + fourOfAKindTwoTen + " | " + (fourOfAKindTwoTen == 0 ? 0 : totalCount / fourOfAKindTwoTen) + " | " + div(fourOfAKindTwoTen, totalCount));
		System.out.println("葫芦: " + fullHouseCount + " | " + (fullHouseCount == 0 ? 0 : totalCount / fullHouseCount) + " | " + div(fullHouseCount, totalCount));
		System.out.println("同花: " + flushCount + " | " + (flushCount == 0 ? 0 : totalCount / flushCount) + " | " + div(flushCount, totalCount));
		System.out.println("顺子: " + straightCount + " | " + (straightCount == 0 ? 0 : totalCount / straightCount) + " | " + div(straightCount, totalCount));
		System.out.println("三条: " + threeOfAKindCount + " | " + (threeOfAKindCount == 0 ? 0 : totalCount / threeOfAKindCount) + " | " + div(threeOfAKindCount, totalCount));
		System.out.println("两对: " + twoPairsCount + " | " + (twoPairsCount == 0 ? 0 : totalCount / twoPairsCount) + " | " + div(twoPairsCount, totalCount));
		System.out.println("一对: " + sevenBetterCount + " | " + (sevenBetterCount == 0 ? 0 : totalCount / sevenBetterCount) + " | " + div(sevenBetterCount, totalCount));
		System.out.println("四張同花: " + fourFlush + " | " + (fourFlush == 0 ? 0 : totalCount / fourFlush) + " | " + div(fourFlush, totalCount));
		System.out.println("四張順: " + fourStraight + " | " + (fourStraight == 0 ? 0 : totalCount / fourStraight) + " | " + div(fourStraight, totalCount));
		System.out.println("乌龙: " + failCount + " | " + (failCount == 0 ? 0 : totalCount / failCount) + " | " + div(failCount, totalCount));

		// try {
		// FileOutputStream out = new FileOutputStream(new File("牌型检测.xlsx"));
		// workbook.write(out);
		// out.close();
		// System.out.println("Excel written successfully..");
		//
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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

	public static CardResult firstRandomCards() {
		int nextInt = 0;
		boolean isRepeated = false;
		// long startTime = System.currentTimeMillis();
		byte[] cardArray = new byte[5];
		CardResult cr = new CardResult();
		for (int i = 0; i < cardArray.length; i++) {
			nextInt = RandomUtils.nextInt(0, CardUtil.cards.length) + 1;
			for (int j = 0; j < i; j++) {
				if (nextInt != CardUtil.cards.length && cardArray[j] == nextInt) {
					isRepeated = true;
					break;
				}
			}
			if (isRepeated) {
				isRepeated = false;
				i--;
				continue;
			}
			cardArray[i] = (byte) nextInt;
		}
		cr.setCards(cardArray);

		if (fiveBars(cardArray, cr).isWin()) {
			cr.setWinType(1000);
		} else if (royalFlush(cardArray, cr).isWin()) {
			cr.setWinType(500);
		} else if (fiveOfAKind(cardArray, cr).isWin()) {
			cr.setWinType(250);
		} else if (straightFlush(cardArray, cr).isWin()) {
			cr.setWinType(120);
		} else if (fourOfAKindJA(cardArray, cr).isWin()) {
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
		} else if (fourStraight(cardArray, cr).isWin()) {
		} else if (fourStraightSecond(cardArray, cr).isWin()) {
		} else if (fourStraightThird(cardArray, cr).isWin()) {
		} else if (sevenBetterKeep(cardArray, cr).isWin()) {
		}
		return cr;
	}

	public static CardResult secondRandomCards(CardResult cr) {
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

		if (fiveBars(cards, cr).isWin()) {
			cr.setWinType(1000);
		} else if (royalFlush(cards, cr).isWin()) {
			cr.setWinType(500);
		} else if (fiveOfAKind(cards, cr).isWin()) {
			cr.setWinType(250);
		} else if (straightFlush(cards, cr).isWin()) {
			cr.setWinType(120);
		} else if (fourOfAKindJA(cards, cr).isWin()) {
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

	public static void main(String[] args) {
		// System.out.println(Arrays.toString(secondRandomCards(new byte[] { 23,
		// 1, 12, 39, 11 }, new byte[] { 2, 3 })));
		// List<Integer> list = new ArrayList<Integer>();
		// list.add(1);
		// list.add(3);
		// list.add(2);
		// Object[] o = list.toArray();
		// for (int i = 0; i < o.length; i++) {
		// System.out.println(o[i] instanceof Object);
		// System.out.println((Integer) o[i]);
		// }
		// int[] iArr = new int[0];
		// Integer[] b = list.toArray(new Integer[0]);
		// for (int i = 0; i < b.length; i++) {
		// System.out.println(b[i]);
		// }
		CardResult cr = new CardResult();
		cr.setCards(new byte[]{12,5,53,53,4});
		System.out.println(threeOfAKind(cr.getCards(), cr).isWin());
		System.out.println(Arrays.toString(cr.getKeepCards()));
//		cr.setCards(new byte[]{1,4,5,7,9});
//		System.out.println(fourStraight(cr.getCards(), cr).isWin());
//		System.out.println(Arrays.toString(cr.getKeepCards()));
//		cr.setKeepCards(null);
//		System.out.println(fourStraightSecond(cr.getCards(), cr).isWin());
//		System.out.println(Arrays.toString(cr.getKeepCards()));
//		cr.setKeepCards(null);
//		System.out.println(fourStraightThird(cr.getCards(), cr).isWin());
//		System.out.println(Arrays.toString(cr.getKeepCards()));
		// System.out.println(Arrays.toString(secondRandomCards(cr).getCards()));
	}

	public static CardResult threeOfAKind(byte[] cards, CardResult cr) {
		int cardValue = 0, card = 0, jokerCount = 0, maxValue = 0;
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
			if (cardValue == 1) {
				maxValue = cardValue;
			} else if (maxValue != 1 && cardValue > maxValue) {
				maxValue = cardValue;
			}
			count = countMap.get(cardValue);
			if (count == null) {
				count = 1;
			} else {
				count++;
			}
			countMap.put(cardValue, count);
			keepCardList = keepCardMap.get(cardValue);
			if (keepCardList == null) {
				keepCardList = new ArrayList<Byte>();
				keepCardMap.put(cardValue, keepCardList);
			}
			keepCardList.add((byte) i);
			if (count + jokerCount >= 3) {
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
		}

		if (jokerCount >= 2) {
			keepList.addAll(keepCardMap.get(maxValue));
			cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
			return cr;
		}

		for (int key : countMap.keySet()) {
			count = countMap.get(key);
			if (count + jokerCount >= 3) {
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
				if (count + jokerCount >= 5) {
					cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
					return cr;
				}
				continue;
			}
			cardValue = (byte) getCardValue(card);
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
			if (count + jokerCount >= 5) {
				keepList.addAll(keepCardList);
				cr.setAfterWin(true, ArrayUtils.toPrimitive(keepList.toArray(new Byte[keepList.size()])));
				return cr;
			}
			cardValueCountMap.put(cardValue, count);
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

	public static CardResult fourStraightSecond(byte[] cards, CardResult cr) {
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
					sortedCards[i] = 1;
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
		if (maxValue == 13 && isA) {
			sortedCards[aIndex] = 14;
		}
		Arrays.sort(sortedCards);

		List<Byte> keepCardList = new ArrayList<Byte>(5);
		int totalGap = 0;
		int gapCount = 0;
		int oneCardGap = 0;
		int keepJokerCount = jokerCount;
		List<Byte> notKeepCardList = new ArrayList<Byte>(5);
		int notKeepCardListSize = 0;
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b == 0) {
					if (!keepCardList.contains(sortedCards[i])) {
						keepCardList.add(sortedCards[i]);
					}
					if (!keepCardList.contains(sortedCards[i - 1])) {
						keepCardList.add(sortedCards[i - 1]);
					}
				} else if (b == 1) {
					if (keepJokerCount >= b || notKeepCardListSize >= b) {
						if (!keepCardList.contains(sortedCards[i])) {
							keepCardList.add(sortedCards[i]);
						}
						if (!keepCardList.contains(sortedCards[i - 1])) {
							keepCardList.add(sortedCards[i - 1]);
						}
						keepJokerCount--;
						notKeepCardListSize--;
					} else {
						totalGap += b;
						oneCardGap++;
						gapCount++;
						if (!keepCardList.contains(sortedCards[i])) {
							notKeepCardList.add(sortedCards[i]);
							notKeepCardListSize++;
						}
					}
				} else {
					
					if (b < 0){
						totalGap += -b;
					} else {
						totalGap += b;
					}
					gapCount++;
					if (!keepCardList.contains(sortedCards[i])) {
						notKeepCardList.add(sortedCards[i]);
						notKeepCardListSize++;
					}
					if (!keepCardList.contains(sortedCards[i - 1])) {
						notKeepCardList.add(sortedCards[i - 1]);
						notKeepCardListSize++;
					}
				}

			}
		}

		if (oneCardGap > notKeepCardList.size() 
//				|| gapCount >= 2 && totalGap >= 3
				) {
			return cr;
		}
		
		int notKeepCard = 0;
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				if (b - jokerCount >= 1) {
					if (!keepCardList.contains(sortedCards[i - 1])) {
						notKeepCardList.add(sortedCards[i - 1]);
						notKeepCard++;
					}
					if (!keepCardList.contains(sortedCards[i])) {
						notKeepCardList.add(sortedCards[i]);
						notKeepCard++;
					}
				}
			}
		}
		// cr.setCards(new byte[]{7,9,10,13,14});
		// if (totalGap > notKeepCard){
		// return cr;
		// }
		// if (oneCardGap > notKeepCard && totalGap - notKeepCard > 0) {
		// return cr;
		// }
		List<Byte> keepList2 = new ArrayList<Byte>();
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
				if (b == 0) {
					sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
					if (!keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
					sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
					if (!keepList2.contains(sortedIndex)) {
						keepList2.add(sortedIndex);
					}
				} else if (b == 1) {
					if (jokerCount > 0) {
						sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
						if (!keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
						if (!keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						jokerCount--;
					} else if (notKeepCard > 0) {
						sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
						if (!notKeepCardList.contains(sortedCards[i]) && !keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
						if (!notKeepCardList.contains(sortedCards[i - 1]) && !keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						notKeepCard--;
					}
				} else if (b == 2) {
					if (jokerCount + notKeepCard >= 2) {
						sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
						if (!notKeepCardList.contains(sortedCards[i]) && !keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						sortedIndex = indexMap.get((int) sortedCards[i - 1]).byteValue();
						if (!notKeepCardList.contains(sortedCards[i - 1]) && !keepList2.contains(sortedIndex)) {
							keepList2.add(sortedIndex);
						}
						jokerCount--;
						notKeepCard--;
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

	public static CardResult fourStraightThird(byte[] cards, CardResult cr) {
		byte[] sortedCards = new byte[5];
		int cardValue = 0, jokerCount = 0, aIndex = 0, maxValue = 0;
		byte card = 0, continueCount = 0, gapArray = 0, sortedIndex = 0, sortedIndex2 = 0;
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
					sortedCards[i] = 1;
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
//		if (maxValue == 13 && isA) {
//			sortedCards[aIndex] = 14;
//		}
		Arrays.sort(sortedCards);
		int maxContinueValue = 0;
		for (int i = sortedCards.length - 1; i > 0; i--) {
			if (sortedCards[i] != joker) {
				byte b = (byte) (sortedCards[i] - sortedCards[i - 1] - 1);
				sortedIndex = indexMap.get((int) sortedCards[i]).byteValue();
				sortedIndex2 = indexMap.get((int) sortedCards[i - 1]).byteValue();
				if (b == 0) {
					continueCount++;
					keepList.add(sortedIndex);
					if (maxContinueValue < sortedCards[i]){
						maxContinueValue = sortedCards[i];
					}
					if (continueCount + 1 + jokerCount >= 3) {
						keepList.add(sortedIndex2);
						if (maxContinueValue < sortedCards[i - 1]){
							maxContinueValue = sortedCards[i - 1];
						}
						break;
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
//						if (maxValue == 13 && isA) {
//							firstValue = 14;
//						}
						if (maxContinueValue > 4 && isA){
							firstValue = 14;
						}
						for (int j2 = 0; j2 < keepList.size(); j2++) {
							checkRepeatedValue = getCardValue(cards[keepList.get(j2)]);
							if (thirdValue == checkRepeatedValue){
								isRepeated = true;
							}
						}
						if (isRepeated){
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
					if (isBreak){
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
		int nextInt = RandomUtils.nextInt(1, CardUtil.cards.length + 1);
		return (byte) nextInt;
	}
}
