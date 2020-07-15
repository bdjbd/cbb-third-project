package com.wd.globalsearch;

import java.io.File;
import org.json.JSONArray;
import com.wd.ICuai;
import com.wd.tools.CommonUtil;

/**
 * 根据指定的路径下载文件到本地
 * */
public class SearchFileByPathCuai implements ICuai {

	/**
	 * 下载指定文件
	 * @param jsonArray 要下载的文件路径
	 * @return 文件流
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			// 得到文件路径
			String filePath = jsonArray.get(0).toString();
			// 得到附件，转换成字节流返回
			File file = new File(filePath);
			// 获取文件夹下文件
			returnJsonArray = CommonUtil.findFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
}