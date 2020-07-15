package com.wd.excel.exp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import com.wd.comp.Constant;
import com.wd.excel.ExcelException;

public class DBToExcel {
	private String xmlPath;// xml描述文件路径
	private Map<String, Object> param;// 参数

	/**
	 * @author 张小龙
	 * @param modelFilePath
	 *            excel模板文件路径
	 * @param dom
	 *            xml的dom对象
	 * @param param
	 *            //参数
	 * @return 返回excel二进制流
	 */
	public byte[] dbToExcel(String modelFilePath, Document dom,
			Map<String, Object> param) throws ExcelException {
		return dbToExcel(modelFilePath, null, dom, param);
	}

	/**
	 * @author 张小龙
	 * @param modelFilePath
	 *            excel模板文件路径
	 * @param xmlPath
	 *            xml描述文件路径
	 * @param param
	 *            //参数
	 * @return 返回excel二进制流
	 */
	public byte[] dbToExcel(String modelFilePath, String xmlPath,
			Map<String, Object> param) throws ExcelException {
		return dbToExcel(modelFilePath, xmlPath, null, param);
	}

	/**
	 * @author 张小龙
	 * @param modelFilePath
	 *            excel模板文件路径
	 * @param xmlPath
	 *            xml描述文件路径
	 * @param dom
	 *            xml的dom对象
	 * @param param
	 *            //参数
	 * @return 返回excel二进制流
	 */
	public byte[] dbToExcel(String modelFilePath, String xmlPath, Document dom,
			Map<String, Object> param) throws ExcelException {
		this.xmlPath = xmlPath;
		this.param = param;
		// 定义二进制数组流
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// excel模板工作簿
		Workbook modelWork = null;
		// excel导出工作簿
		WritableWorkbook workbook = null;
		try {
			// 加载模板
			modelWork = Workbook.getWorkbook(new File(
					Constant.EXCEL_TEMPLATE_FILEPATH + modelFilePath));
			// 创建导出工作簿
			workbook = Workbook.createWorkbook(os, modelWork);
			// 解析xml描述文件，并将数据填入正编辑的sheet
			parseXml(workbook.getSheet(0), dom);
			workbook.write();
		} catch (BiffException e) {
			throw new ExcelException("读取导出模板失败");
		} catch (IOException e) {
			throw new ExcelException("读取导出模板失败");
		} finally {
			// 关闭模板工作簿
			modelWork.close();
			try {
				// 关闭导出工作簿
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 返回导出工作簿的二进制流
		return os.toByteArray();
	}

	/**
	 * @author 张小龙
	 * @param sheet
	 *            正编辑的sheet
	 */
	@SuppressWarnings("unchecked")
	private void parseXml(WritableSheet sheet, Document dom)
			throws ExcelException {
		try {
			// 解析描述文件
			if (dom == null)
				dom = new SAXReader().read(Constant.EXCEL_TEMPLATE_FILEPATH
						+ xmlPath);
			// 取到根节点
			Element root = dom.getRootElement();
			// 取到完成时执行类路径
			String doComplete = root.attributeValue("doCompleted");
			// 遍历根节点下的子元素

			List<Element> tables = root.elements();
			for (Element e : tables) {
				// 如果是点阵表，就调用点阵解析类，否则调用矩阵解析类，将相应的数据填入excel
				if (e.getName().equals("PointTable")) {
					new PointTableParser(sheet, e, param);
				} else if (e.getName().equals("ListTable")) {
					new ListTableParser(sheet, e, param);
				}
			}
			// 如果配置了完成时处理类，执行完成时处理方法
			if (!"".equals(doComplete)) {
				IExportExcelComplete complete = (IExportExcelComplete) Class
						.forName(doComplete).newInstance();
				complete.doComplete(sheet, dom, param);
			}
		} catch (DocumentException e) {
			throw new ExcelException("读取配置文件失败");
		} catch (InstantiationException e) {
			throw new ExcelException("执行验证完成方法失败");
		} catch (IllegalAccessException e) {
			throw new ExcelException("执行验证完成方法失败");
		} catch (ClassNotFoundException e) {
			throw new ExcelException("执行验证完成方法失败");
		}
	}
}
