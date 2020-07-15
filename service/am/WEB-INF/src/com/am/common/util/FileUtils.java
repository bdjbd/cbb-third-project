package com.am.common.util;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Path;

/**
 * @author YueBin
 * @create 2016年5月24日
 * @version 
 * 说明:<br />
 * 文件处理类
 */
public class FileUtils {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**
	 * 获取平台上传文件的文件名，返回JSON格式字符串。
	 * @param table 表名
	 * @param columName 字段名
	 * @param id  主键
	 * @return 文件保存路径，格式如下：
	 * [{name:"文件名",path:"文件相对路径"},{name:"",path:""},{name:"",path:""}]
	 */
	public String getFastUnitFilePathJSON(String table, String columName,
			String id) {
		JSONArray result=new JSONArray();
		table=table.toUpperCase();
		
		String fileSpar = "/";
		String relativeFilePath = fileSpar + "files" + fileSpar + table
				+ fileSpar + id + fileSpar + columName;
		String fielPath = Path.getRootPath() + relativeFilePath;
		File file = new File(fielPath);
		if (!file.exists()) {
			return result.toString();
		}
		
		for (String f : file.list()) {
			JSONObject item=new JSONObject();
			try {
				item.put("name",f);
				item.put("path",relativeFilePath + fileSpar + f );
				result.put(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * 同步文件,如果文件不在现有字段中，则直接删除。
	 * @param tableName  表名
	 * @param tableFiled 平台影射字段名
	 * @param keyStr   数据主键
	 * @param nowFilePath  现在数据库中文件，多个文件使用逗号分割
	 */
	public void syncFilesByFilePath(String tableName,String tableFiled,String keyStr,String nowFilePath ){
		String ps=File.separator;
		// files/表名/主键/表字段名/文件
		String filePath = Path.getRootPath() +ps+"files"+ps+tableName+ps+keyStr+ps+tableFiled;
		
		File files=new File(filePath);
		
		if(files.exists()&&nowFilePath!=null){
			//如果文件存在
			for(String fileName:files.list()){
				if(!nowFilePath.contains(fileName)){
					//如果文件名不存在，则直接删除文件
					logger.info("删除文件 "+fileName);
					new File(filePath+"\\"+fileName).delete();
				}
			}
		}
		
		
		
		
	}
	
}
