package com.axatrikx.io;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.log4testng.Logger;

public class ExcelController {

	private static final Logger log = Logger.getLogger(ExcelController.class);
	
	private Workbook workBook;

	public ExcelController(String fileName) throws IOException, InvalidFormatException {
		workBook = WorkbookFactory.create(ExcelController.class.getClassLoader().getResourceAsStream(fileName));
	}

	public List<Sheet> getSheets() {
		int noOfSheets = workBook.getNumberOfSheets();
		List<Sheet> sheets = new ArrayList<Sheet>();
		for (int i = 0; i < noOfSheets; i++) {
			sheets.add(workBook.getSheetAt(i));
		}
		return sheets;
	}

	public List<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<Object> topRow = getTableData(getCurSheet()).get(0);
		for (Object item : topRow) {
			headers.add(String.valueOf(item));
		}
		return headers;
	}

	private Sheet getCurSheet() {
		return getSheets().get(0);
	}

	private ArrayList<ArrayList<Object>> getTableData(Sheet curSheet) {
		ArrayList<ArrayList<Object>> tableData = new ArrayList<ArrayList<Object>>();
		Iterator<Row> rowIterator = curSheet.iterator();
		ArrayList<Object> curRow;
		while (rowIterator.hasNext()) {
			curRow = new ArrayList<Object>();
			Row row = rowIterator.next();
			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					curRow.add(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					curRow.add(cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					curRow.add(cell.getStringCellValue());
					break;
				default:
					if (DateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						curRow.add(sdf.format(cell.getDateCellValue()));
					} else {
						log.warn("Unidentified data format");
					}
				}
			}
			tableData.add(curRow);
		}

		return tableData;
	}

	public static void main(String[] args) {
		try {
			System.out.println(new ExcelController("a.xlsx").getHeaders());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}