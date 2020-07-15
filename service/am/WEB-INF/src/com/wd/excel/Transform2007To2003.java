package com.wd.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Transform2007To2003 {
	public static byte[] doTransform(byte[] b, String sheet)
			throws ExcelException {
		try {
			ByteArrayInputStream oi = new ByteArrayInputStream(b);
			org.apache.poi.ss.usermodel.Workbook workbookFrom = WorkbookFactory
					.create(oi);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			org.apache.poi.ss.usermodel.Sheet sheetFrom = null;
			if (sheet == null) {
				sheetFrom = workbookFrom.getSheetAt(0);
			} else {
				sheetFrom = workbookFrom.getSheet(sheet);
			}
			jxl.write.WritableWorkbook workbookTo = jxl.Workbook
					.createWorkbook(os);
			jxl.write.WritableSheet sheetTo = workbookTo.createSheet("sheet1",
					0);
			System.out.println("行数：" + sheetFrom.getPhysicalNumberOfRows());
			for (int rowIndex = 0; rowIndex < sheetFrom
					.getPhysicalNumberOfRows(); rowIndex++) {
				org.apache.poi.ss.usermodel.Row row = sheetFrom
						.getRow(rowIndex);
				if (row == null) {
					continue;
				}
				for (int colIndex = 0; colIndex < row
						.getPhysicalNumberOfCells(); colIndex++) {
					org.apache.poi.ss.usermodel.Cell cell = row
							.getCell(colIndex);
					System.out.println(sheetFrom.getPhysicalNumberOfRows()
							+ ":" + row.getPhysicalNumberOfCells() + "----"
							+ rowIndex + ":" + colIndex);
					if (cell == null)
						continue;
					switch (cell.getCellType()) {
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
						sheetTo.addCell(new jxl.write.Blank(colIndex, rowIndex));
						break;
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
						boolean valueBoolean = cell.getBooleanCellValue();
						sheetTo.addCell(new jxl.write.Boolean(colIndex,
								rowIndex, valueBoolean));
						break;
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
						// byte valueError = cell.getErrorCellValue();
						sheetTo.addCell(new jxl.write.Label(colIndex, rowIndex,
								"error!"));
						break;
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
						switch (cell.getCachedFormulaResultType()) {
						case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
							boolean formualBoolean = cell.getBooleanCellValue();
							sheetTo.addCell(new jxl.write.Boolean(colIndex,
									rowIndex, formualBoolean));
							break;
						case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
							String valueString = cell.getStringCellValue();
							sheetTo.addCell(new jxl.write.Label(colIndex,
									rowIndex, valueString));
							break;
						case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
							Double valueNumeric = cell.getNumericCellValue();
							sheetTo.addCell(new jxl.write.Number(colIndex,
									rowIndex, valueNumeric));
							break;
						}
						break;
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
						Double valueNumeric = cell.getNumericCellValue();
						sheetTo.addCell(new jxl.write.Number(colIndex,
								rowIndex, valueNumeric));
						break;
					case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
						String valueString = cell.getStringCellValue();
						sheetTo.addCell(new jxl.write.Label(colIndex, rowIndex,
								valueString));
					}
				}
			}
			workbookTo.write();
			workbookTo.close();
			os.flush();
			os.close();
			oi.close();
			return os.toByteArray();
		} catch (Exception e) {
			throw new ExcelException("读取excel表失败");
		}
	}

	public static void main(String[] args) {
		File fromFile = new File(
				"C:/Documents and Settings/Administrator/桌面/Hhg-Dds-E-003卸车计划.xlsx");
		try {
			FileInputStream fis = new FileInputStream(fromFile);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int len = fis.read(buffer, 0, 2048);
			while (len != -1) {
				os.write(buffer, 0, len);
				len = fis.read(buffer, 0, 2048);
			}
			fis.close();
			os.flush();
			os.close();
			File toFile = new File(
					"C:/Documents and Settings/Administrator/桌面/Hhg-Dds-E-003卸车计划副本.xls");
			toFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(toFile);
			fos.write(doTransform(os.toByteArray(), null));
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExcelException e) {
			e.printStackTrace();
		}
	}
}
