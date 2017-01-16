package com.mzm.firephoenix.constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.mzm.firephoenix.utils.CardResult;
import com.mzm.firephoenix.utils.CardUtil;

@Component
public class ExcelReader {

	public static void main(String[] args) throws IOException {
		FileInputStream file = new FileInputStream(new File("E:\\workspace\\firephoenix\\src\\four.xlsx"));

		XSSFWorkbook workbook = null;
		workbook = new XSSFWorkbook(file);
		URL url = ClassLoader.getSystemResource("\\");
		// workbook = new
		// XSSFWorkbook(ClassLoader.getSystemResourceAsStream(file));

		for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
			XSSFSheet sheet = workbook.getSheetAt(j);
			int physicalNumberOfRow = sheet.getPhysicalNumberOfRows();
			XSSFRow row = null;
			XSSFCell cell = null;
			for (int rowNumber = 0; rowNumber <= physicalNumberOfRow; rowNumber++) {
				row = sheet.getRow(rowNumber);
				CardsPoolUnit cardsPoolUnit = new CardsPoolUnit();
				byte[] first = new byte[5];
				if (row != null) {
					cell = row.getCell(0);
					String[] firstArr = cell.getStringCellValue().split(",");
					for (int i = 0; i < firstArr.length; i++) {
						first[i] = Byte.parseByte(firstArr[i]);
					}
					cardsPoolUnit.setFirst(first);
					List<CardsPoolUnit> list = GameConstant.WINPOOLMAP.get(Integer.parseInt(sheet.getSheetName()));
					if (list == null) {
						list = new ArrayList<CardsPoolUnit>();
						GameConstant.WINPOOLMAP.put(Integer.parseInt(sheet.getSheetName()), list);
					}
					list.add(cardsPoolUnit);
				}
			}
		}

		workbook.close();
		
		byte[] cardArray = null;
		int count = 0;
		List<CardsPoolUnit> list = GameConstant.WINPOOLMAP.get(1);
		for (int i = 0; i < list.size(); i++) {
			CardsPoolUnit cardsPoolUnit = list.get(i);
			CardResult cr = new CardResult();
			cardArray = cardsPoolUnit.getFirst();
			cr.setCards(cardsPoolUnit.getFirst());
			if (CardUtil.fourOfAKindJA(cardArray, cr).isWin()) {
				cr.setWinType(80);
			} else if (CardUtil.fourOfAKindTwoTen(cardArray, cr).isWin()) {
				cr.setWinType(50);
			} else if (CardUtil.fullHouse(cardArray, cr).isWin()) {
				cr.setWinType(10);
			} else if (CardUtil.flush(cardArray, cr).isWin()) {
				cr.setWinType(7);
			} else if (CardUtil.straight(cardArray, cr).isWin()) {
				cr.setWinType(5);
			} else if (CardUtil.threeOfAKind(cardArray, cr).isWin()) {
				cr.setWinType(3);
			} else if (CardUtil.twoPairs(cardArray, cr).isWin()) {
				cr.setWinType(2);
			}
			// else if (fourFlushStraightJoker(cardArray, cr).isWin() ||
			// fourFlushStraightThirdJoker(cardArray, cr).isWin() ||
			// fourFlushStraightFourthJoker(cardArray, cr).isWin()){
			// cr.setWinType(1);
			// cr.setWinType2(-5);
			// }
			else if (CardUtil.fourFlushStraightJokerNew(cardArray, cr).isWin()) {
				cr.setWinType2(-5);
				cr.setWinType2(999);
			}
			else if (CardUtil.fourFlushJoker(cardArray, cr).isWin()) {
				cr.setWinType(1);
			} else if (CardUtil.fourStraightFourthJokerNew(cardArray, cr).isWin()) {
				cr.setWinType(1);
				cr.setWinType2(998);
			} 
			else if (CardUtil.sevenBetter(cardArray, cr).isWin()) {
				cr.setWinType(1);
			}  
			else if (CardUtil.fourFlushStraightNew(cardArray, cr).isWin()) {
				cr.setWinType2(-5);
				cr.setWinType2(997);
			}
//			else if (fourFlushStraight(cardArray, cr).isWin() || fourFlushStraightThird(cardArray, cr).isWin() || fourFlushStraightFourth(cardArray, cr).isWin()) {
//				cr.setWinType2(-5);
//			}
			else if (CardUtil.fourFlush(cardArray, cr).isWin()) {
				cr.setWinType2(-1);
			} else if (CardUtil.fourStraight(cardArray, cr).isWin()) {
				cr.setWinType2(-2);
			} else if (CardUtil.fourStraightThird(cardArray, cr).isWin()) {
				cr.setWinType2(-2);
			} else if (CardUtil.fourStraightFourth(cardArray, cr).isWin()) {
				cr.setWinType2(-2);
			} else if (CardUtil.sevenBetterKeep(cardArray, cr).isWin()) {
				cr.setWinType2(-3);
			} else {
				cr.setWinType2(-4);
			}
			
			if (cr.getWinType2() != 999 && cr.getWinType2() != 998 && cr.getWinType2() != 997){
				System.out.println(i + 1);
				System.out.println("winType : " + cr.getWinType());
				count++;
			}
			
		}
		System.out.println(count);
	}
	
	public ExcelReader() throws IOException {
		FileInputStream file = new FileInputStream(new File("/opt/apache-tomcat-8.0.38/webapps/ROOT/WEB-INF/classes/winPool.xlsx"));
//		FileInputStream file = new FileInputStream(new File("E:\\workspace\\firephoenix\\src\\winPool.xlsx"));

		XSSFWorkbook workbook = null;
		workbook = new XSSFWorkbook(file);
		URL url = ClassLoader.getSystemResource("\\");
		// workbook = new
		// XSSFWorkbook(ClassLoader.getSystemResourceAsStream(file));

		for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
			XSSFSheet sheet = workbook.getSheetAt(j);
			int physicalNumberOfRow = sheet.getPhysicalNumberOfRows();
			XSSFRow row = null;
			XSSFCell cell = null;
			for (int rowNumber = 1; rowNumber <= physicalNumberOfRow; rowNumber++) {
				row = sheet.getRow(rowNumber);
				CardsPoolUnit cardsPoolUnit = new CardsPoolUnit();
				byte[] first = new byte[5];
				byte[] keep = null;
				byte[] second = new byte[5];
				if (row != null) {
					cell = row.getCell(0);
					String[] firstArr = cell.getStringCellValue().split(",");
					for (int i = 0; i < firstArr.length; i++) {
						first[i] = Byte.parseByte(firstArr[i]);
					}
					cardsPoolUnit.setFirst(first);
					cell = row.getCell(1);
					if (cell != null){
						String[] keepArr = cell.getStringCellValue().split(",");
						keep = new byte[keepArr.length];
						for (int i = 0; i < keepArr.length; i++) {
							keep[i] = Byte.parseByte(keepArr[i]);
						}
						cardsPoolUnit.setKeep(keep);
						if (row.getCell(2) != null){
							cardsPoolUnit.setWinType(Double.valueOf(row.getCell(2).getNumericCellValue()).intValue());
						}
					}
					cell = row.getCell(3);
					if (cell != null){
						String[] secondArr = cell.getStringCellValue().split(",");
						for (int i = 0; i < secondArr.length; i++) {
							second[i] = Byte.parseByte(secondArr[i]);
						}
						cardsPoolUnit.setSecond(second);
					}
					
					List<CardsPoolUnit> list = GameConstant.WINPOOLMAP.get(Integer.parseInt(sheet.getSheetName()));
					if (list == null) {
						list = new ArrayList<CardsPoolUnit>();
						GameConstant.WINPOOLMAP.put(Integer.parseInt(sheet.getSheetName()), list);
					}
					list.add(cardsPoolUnit);
				}
			}
		}

		workbook.close();
	}
}