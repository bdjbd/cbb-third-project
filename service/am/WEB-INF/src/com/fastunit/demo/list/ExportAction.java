package com.fastunit.demo.list;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.action.Unit2ExcelAction;
import com.fastunit.util.Checker;
import com.fastunit.util.DateUtil;

public class ExportAction extends Unit2ExcelAction {

	// 控制单元数据（data）和显示的元素
	@Override
	protected void config(ActionContext ac) throws Exception {
		super.config(ac);
		// 设置数据（data）：价格为空时显示0
		for (int i = 0; i < data.size(); i++) {
			if (Checker.isEmpty(data.getRow(i).get("price"))) {
				data.put(i, "price", "0");
			}
		}
		// 设置显示的列：不显示bookid
		elements.remove(uc.getElement("bookid"));
	}

	// 取得文件名称（不包含.xls文件后缀）
	@Override
	protected String getFileName(ActionContext ac) {
		return "定制的文件名";
	}

	// 取得标题
	@Override
	protected String getTitle(ActionContext ac) {
		return "定制的标题";
	}

	// 创建Title样式
	// protected HSSFCellStyle createTitleStyle(HSSFWorkbook wb) {
	// HSSFFont font = wb.createFont();
	// font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	// font.setFontHeightInPoints((short) 11);
	// HSSFCellStyle style = wb.createCellStyle();
	// style.setFont(font);
	// style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
	// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
	// return style;
	// }

	// 创建标题Cell的默认样式。本例：背景色设为黄色
	@Override
	protected HSSFCellStyle createDefaultHeadStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);// 背景色设为黄色
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return style;
	}

	// 定制标题cell的样式。本例：价格的字体设为粗体
	@Override
	protected HSSFCellStyle getHeadCellStyle(HSSFCellStyle defaultHeadStyle,
			HSSFWorkbook wb, String elementId) {
		if ("price".equals(elementId)) {
			HSSFCellStyle style = createDefaultHeadStyle(wb);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 9);
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			style.setAlignment(CellStyle.ALIGN_CENTER);
			style.setFont(font);
			return style;
		} else {
			return defaultHeadStyle;
		}
	}

	// 创建数据Cell的默认样式
	// protected HSSFCellStyle createDefaultDataStyle(HSSFWorkbook wb) {
	// HSSFCellStyle style = wb.createCellStyle();
	// HSSFFont font = wb.createFont();
	// font.setFontHeightInPoints((short) 9);
	// style.setFont(font);
	// style.setBorderTop(HSSFCellStyle.BORDER_THIN);
	// style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	// style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	// style.setBorderRight(HSSFCellStyle.BORDER_THIN);
	// return style;
	// }

	// 定制数据cell的样式。本例：价格为空的背景色设为灰色
	@Override
	protected HSSFCellStyle getDataCellStyle(HSSFCellStyle defaultStyle,
			HSSFWorkbook wb, String elementId, Row dataRow) {
		if ("price".equals(elementId)
				&& Double.parseDouble(dataRow.get(elementId, "0")) <= 0) {
			HSSFCellStyle style = createDefaultDataStyle(wb);
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			return style;
		} else {
			return defaultStyle;
		}
	}

	// 设置单元格的值。本例：价格使用数字类型。
	@Override
	protected void setCellValue(HSSFCell cell, String text, String elementId) {
		if ("price".equals(elementId)) {
			cell.setCellValue(Double.parseDouble(text));
		} else {
			super.setCellValue(cell, text, elementId);
		}
	}

	// 设置已生成的sheet。
	@Override
	protected void resetSheet(ActionContext ac, HSSFWorkbook wb, HSSFSheet sheet) {
		// 一、前3列定宽，20个字符宽度
		sheet.setColumnWidth(0, 20 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 20 * 256);
		//sheet.autoSizeColumn(0);// 自动调整列宽

		// 二、设置合计行的背景色
		HSSFCellStyle style = createDefaultDataStyle(wb);
		style.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		int rows = sheet.getPhysicalNumberOfRows();
		HSSFRow lastRow = sheet.getRow(rows - 1);
		int cells = lastRow.getPhysicalNumberOfCells();
		for (int i = 0; i < cells; i++) {
			lastRow.getCell(i).setCellStyle(style);
		}
		// 三、增加一行、合并
		HSSFRow row = sheet.createRow(rows);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("导出时间：" + DateUtil.getCurrentDate());
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setColor(Font.COLOR_RED);
		style = wb.createCellStyle();
		style.setFont(font);
		cell.setCellStyle(style);
		sheet.addMergedRegion(new CellRangeAddress(rows, rows, 0,
				elements.size() - 1));// 合并

	}

	// 自行创建HSSFWorkbook对象、输出
	// protected void doDownload(ActionContext ac, HttpServletResponse response)
	// throws Exception {
	// super.doDownload(ac, response);
	// }
}
