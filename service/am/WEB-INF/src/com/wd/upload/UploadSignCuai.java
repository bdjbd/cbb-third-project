package com.wd.upload;

import java.io.File;
import java.io.FileOutputStream;
import org.json.JSONArray;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.comp.Constant;

/**
 * 上传签名图片
 * */
public class UploadSignCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 文件存放文件夹
			String filePath = jsonArray.get(0).toString();
			// 得到文件名
			String fileName = jsonArray.get(1).toString();
			// 文件字节流
			String fileByteStr = jsonArray.get(2).toString();
			// 拼接文件存放文件夹    修改签名保存路径  2012-10-26 岳斌 
			String fileSaveFolder = Path.getRootPath() + "/" + Constant.UPLOADFILES
					+"/"+filePath + "/";
			File file = new File(fileSaveFolder);
			// 如果不存在则创建
			if (!file.exists()) {
				file.mkdirs();
			}
			// 转换成字节流
			byte[] fileByte = Base64.decode(fileByteStr);
			// 保存文件
			file = new File(fileSaveFolder + "/" + fileName);
			// 保存到服务器上
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(fileByte);
			// 保存成功将路径返回回去
			String fileSavePath = "/"+Constant.UPLOADFILES+"/"+filePath + "/" + fileName;
			returnJsonArray.put(fileSavePath);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return returnJsonArray;
	}
}