package com.wd.upload;

import java.io.File;

import org.json.JSONArray;

import com.fastunit.Path;
import com.lowagie.text.pdf.codec.Base64;
import com.wd.ICuai;
import com.wd.tools.CommonUtil;

/**
 * 下载签名图片
 * */
public class DownloadSignCuai implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 得到文件路径
			String filePath = jsonArray.get(0).toString();
			// 获取服务器路径
			String serverPath = Path.getRootPath();
			// 得到附件，转换成字节流返回        修改签名保存路径  2012-10-26 岳斌
 			File file = new File(
					serverPath + "/" +filePath);
			// 将文件转换成字节流
			byte[] bytes = CommonUtil.getBytesFromFile(file);
			String string = Base64.encodeBytes(bytes);
			returnJsonArray.put(string);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return returnJsonArray;
	}
}