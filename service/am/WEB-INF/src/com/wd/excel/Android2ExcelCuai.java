package com.wd.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.comp.Constant;
import com.wd.excel.exp.DBToExcel;
import com.wd.tools.CommonUtil;

public class Android2ExcelCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJs = new JSONArray();
		String newFileNames = UUID.randomUUID().toString() + ".xls";
		FileOutputStream fos = null;
		File file = null;
		try {
			JSONObject paramJo = jsonArray.getJSONObject(0);
			String excelPath = paramJo.getString("excelPath");
			String xmlPath = paramJo.getString("xmlPath");
			Map<String, Object> param = new HashMap<String, Object>();
			Iterator<?> it = paramJo.keys();
			while (it.hasNext()) {// 遍历JSONObject
				String key = it.next().toString();
				if (!key.equals("excelPath") && !key.equals("xmlPath")
						&& !key.equals("excelName")) {
					param.put(key, paramJo.get(key));
				}
			}
			// 拼接fastunitHome临时路径
			String fileAccessPath = Constant.EXCEL_TEMP_FILEPATH;
			file = new File(fileAccessPath);
			// 如果文件夹不存在则创建
			if (!file.exists()) {
				file.mkdirs();
			} else {
				CommonUtil.deleteFileByDate(file, new Date());
			}
			file = new File(fileAccessPath + "/" + newFileNames);
			file.createNewFile();
			fos = new FileOutputStream(file);
			DBToExcel ddtoExcel = new DBToExcel();
			System.out.println("-----------------------------------------生成xls");
			byte[] data = ddtoExcel.dbToExcel(excelPath, xmlPath, param);
			fos.write(data);
			System.out.println("-----------------------------------------生成xls"+data.length);
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 将文件转换成字节流
		try {
			byte[] bytes = CommonUtil.getBytesFromFile(file);
			String string = Base64.encodeBytes(bytes);
			returnJs.put(string);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnJs;
	}
}
