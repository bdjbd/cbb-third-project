package com.wd.excel.exp;

import java.util.List;
import java.util.Map;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wd.excel.ExcelException;
import com.wd.tools.DatabaseAccess;

public class ListTableParser {
	private String searchSql = null;
	private int rowIndex = 0;

	// 导入矩阵列表
	public ListTableParser(WritableSheet sheet, Element e,
			Map<String, Object> param) throws ExcelException {
		// 取得主查询sql
		searchSql = e.attributeValue("searchSql");
		rowIndex = Integer.parseInt(e.attributeValue("rowIndex"));
		// 用参数替换sql中的标识符
		for (String key : param.keySet()) {
			searchSql = searchSql.replaceAll("@" + key, param.get(key)
					.toString());
		}
		@SuppressWarnings("unchecked")
		List<Element> fields = e.elements();
		String fieldLabels = "";
		for (Element temp : fields) {
			if (!"".equals(temp.attributeValue("TableField"))) {
				fieldLabels += temp.attributeValue("TableField") + ",";
			}
		}
		JSONArray js = DatabaseAccess.query(searchSql);
		if (js == null || js.length() == 0) {
			//throw new ExcelException("查询数据出错");
		}

		for (int i = 0; i < js.length(); i++) {
			JSONObject jo = new JSONObject();
			try {
				jo = js.getJSONObject(i);
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			for (Element temp : fields) {
				int colIndex = Integer.parseInt(temp.attributeValue("index"));
				String tableField = temp.attributeValue("TableField")
						.toUpperCase();
				String defaultValue = temp.attributeValue("defaultValue");
				String dataType = temp.attributeValue("DataType");
				String value = "";
				try {
					value = "".equals(tableField) || jo.get(tableField) == null ? ""
							: jo.get(tableField).toString();
				} catch (JSONException e2) {
					e2.printStackTrace();
				}
				// 用参数替换默认值中的标识符
				for (String key : param.keySet()) {
					defaultValue = defaultValue.replaceAll("@" + key, param
							.get(key).toString());
				}
				WritableCell cell = sheet.getWritableCell(colIndex, rowIndex);
				CellFormat formate = cell.getCellFormat();
				if (!"".equals(defaultValue)) {
					cell = new jxl.write.Label(colIndex, rowIndex, defaultValue);
				} else {
					try {
						if (value != null && !value.equals("")
								&& dataType.equals("java.lang.Integer")) {
							cell = new jxl.write.Number(colIndex, rowIndex,
									Integer.parseInt(value));
						} else if (value != null && !value.equals("")
								&& dataType.equals("java.lang.Double")) {
							cell = new jxl.write.Number(colIndex, rowIndex,
									Double.parseDouble(value));
						} else {
							cell = new jxl.write.Label(colIndex, rowIndex,
									value);
						}
					} catch (Exception e1) {
						cell = new jxl.write.Label(colIndex, rowIndex, value);
						e1.printStackTrace();
					}
				}
				// 如果要填充的cell，excel模板中没有定义样式，就自定义样式，宋体、12号字体，全边框
				if (formate == null) {
					WritableFont font = new WritableFont(
							WritableFont.createFont("仿宋_GB2312"), 12,
							WritableFont.NO_BOLD);
					formate = new WritableCellFormat(font);
					try {
						((WritableCellFormat) formate).setBorder(Border.ALL,
								BorderLineStyle.THIN);
					} catch (WriteException e1) {
						e1.printStackTrace();
					}
				}
				cell.setCellFormat(formate);
				try {
					sheet.addCell(cell);
				} catch (RowsExceededException e1) {
					e1.printStackTrace();
				} catch (WriteException e1) {
					e1.printStackTrace();
				}
			}
			rowIndex++;
		}
	}
}
