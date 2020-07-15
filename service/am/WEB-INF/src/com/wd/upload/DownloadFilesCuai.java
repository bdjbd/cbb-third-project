package com.wd.upload;

import java.io.File;
import org.json.JSONArray;
import com.fastunit.Path;
import com.wd.ICuai;
import com.wd.comp.Constant;
import com.wd.tools.CommonUtil;

/**
 * 下载附件
 * */
public class DownloadFilesCuai implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 得到文件路径
			String filePath = jsonArray.get(0).toString();
			// 获取服务器路径
			String serverPath = Path.getHomePath();
			// 得到附件，转换成字节流返回
			File file = new File(serverPath + "/" + Constant.UPLOADFILES + "/"
					+ filePath);
			// 获取文件夹下文件
			returnJsonArray = CommonUtil.findFolderFileReturnJsonArray(file);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return returnJsonArray;
	}
}