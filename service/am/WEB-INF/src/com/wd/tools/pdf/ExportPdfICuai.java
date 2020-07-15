package com.wd.tools.pdf;

import java.io.File;

import org.json.JSONArray;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.tools.CommonUtil;

/**
 *   说明:
 * 			根据数据创建PDF,并提供下载的WebService端接口实现类。
 *   @creator	YueBin
 *   @create 	2012-11-22  
 *   @revision	1
 */
public class ExportPdfICuai implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJs = null;
		File file = null;
		try {
		//SQL
		String sql=jsonArray.getJSONObject(0).getString("SQL");
		//pdf文件名
		String pdfFileName =jsonArray.getJSONObject(0).getString("PDFFILENAME");
		//pdf标题
		String pdfTableTitle=jsonArray.getJSONObject(0).getString("PDFTABLETITLE");
		//pdf文件表格表头
		String pdfTableHead=jsonArray.getJSONObject(0).getString("TABLEHEAD");
		//调用接用户名
		String userName=jsonArray.getJSONObject(0).getString("USERNAME");
		//纸张方向
		String pdfOrientation=jsonArray.getJSONObject(0).getString("PDFORIENTATION");
		//pdf保存路径
		String filePath=Path.getHomePath()+File.separator+"PDF"+File.separator+userName+File.separator+pdfFileName;
		//创建PDF文件
		boolean pdfRes=PdfFactory.createPDFByDataSource(sql, filePath, pdfTableTitle,pdfTableHead,pdfOrientation);
		
		if(!pdfRes){
			return returnJs=new JSONArray("[{\"RESULT\":\"FAILED\"}]");
		}
			file=new File(filePath);
			String fileByteStr = Base64
					.encodeBytes(CommonUtil.getBytesFromFile(file));
			//JSONArray对象中不能有"\n"
			fileByteStr=fileByteStr.replace("\n","@_@!");
			returnJs=new JSONArray("[{\""+pdfFileName+"\":\""+fileByteStr+"\"}]");
			file.delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnJs;
	}

}
