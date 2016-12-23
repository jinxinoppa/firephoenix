package com.mzm.firephoenix.constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelReader {

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
					String[] keepArr = cell.getStringCellValue().split(",");
					keep = new byte[keepArr.length];
					for (int i = 0; i < keepArr.length; i++) {
						keep[i] = Byte.parseByte(keepArr[i]);
					}
					cardsPoolUnit.setKeep(keep);
					cardsPoolUnit.setWinType(Double.valueOf(row.getCell(2).getNumericCellValue()).intValue());
					cell = row.getCell(3);
					String[] secondArr = cell.getStringCellValue().split(",");
					for (int i = 0; i < secondArr.length; i++) {
						second[i] = Byte.parseByte(secondArr[i]);
					}
					cardsPoolUnit.setSecond(second);
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