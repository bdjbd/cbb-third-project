package com.wd.upload;

import java.io.File;
import java.io.FileOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.comp.Constant;
import com.wd.tools.CommonUtil;

/**
 * 上传附件
 * */
public class UploadFilesCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray pJsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 得到文件存放文件夹
			String folderPath = pJsonArray.getString(0);
			// 拼接文件存放文件夹
			String fileSaveFolder = Path.getHomePath() + "/"
					+ Constant.UPLOADFILES + "/" + folderPath;
			File file = new File(fileSaveFolder);
			// 如果文件夹不存在则创建
			if (!file.exists()) {
				file.mkdirs();
			}
			// 获取文件集合
			JSONArray jsonArray = (JSONArray) pJsonArray.get(1);
			FileOutputStream fos = null;
			// 循环上传
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String state = jsonObject.getString("state");
				// 得到文件名
				String fileName = jsonObject.getString("fileName");
				// 保存文件
				file = new File(fileSaveFolder + "/" + fileName);
				// 如果状态为delete时，删除文件
				if (state.equals("delete")) {
					CommonUtil.deleteFile(file);
					// 如果状态为add时，保存文件到服务器上
				} else if (state.equals("add")) {
					// 文件字节流
					String fileByteStr = jsonObject.getString("fileByte");
					// 转换成字节流
					byte[] fileByte = Base64.decode(fileByteStr);
					// 保存到服务器上
					fos = new FileOutputStream(file);
					fos.write(fileByte);
				}
			}
			returnJsonArray.put(Constant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			returnJsonArray.put(Constant.FAILED);
		}
		return returnJsonArray;
	}
}