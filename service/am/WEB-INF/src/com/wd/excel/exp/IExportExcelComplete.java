package com.wd.excel.exp;

import java.util.Map;
import jxl.write.WritableSheet;
import org.dom4j.Document;
import com.wd.excel.ExcelException;

public interface IExportExcelComplete {
	/**
	 * @author 张小龙
	 * @param param
	 *            导出excel的参数
	 * @exception ExcelException
	 *                此方法可报出实例化的ExcelException异常
	 * @see ExcelException
	 * */
	public void doComplete(WritableSheet sheet, Document dom,
			Map<String, Object> param) throws ExcelException;
}
