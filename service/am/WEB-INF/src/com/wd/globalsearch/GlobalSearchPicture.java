package com.wd.globalsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.Path;
import com.wd.tools.CommonUtil;

/**
 * 按照关键字搜索用户模块下的图片
 * @author dzx
 * */
public class GlobalSearchPicture implements IGlobalSearch {

	/**
	 * 按照关键字搜索用户模块下的图片
	 * @param keyName 关键字
	 * @return 文档信息集合，包括：文档名称，路径，后缀名，所属模块名
	 * */
	@Override
	public JSONArray search(String keyName) {
		// TODO Auto-generated method stub
		//String serverIp="192.168.14.6";
		//String serverPort="8081";
		String path=Path.getRootPath()+"/files/auser/";
		String extension="jpg,gif,png,JPEG";
		JSONArray js=CommonUtil.GlobalSearchFilesInfo(path, keyName, extension);
		if(js!=null && js.length()>0)
		{
			for(int i=0;i<js.length();i++)
			{
				JSONObject jo=null;
				String filePath="";
				try {
					
					jo=js.getJSONObject(i);
					jo.put("modulename","用户");
					filePath=jo.getString("path");
					if(!filePath.equalsIgnoreCase(""))
					{
						String rootPath=(Path.getRootPath()+"\\files\\auser").replace("/","\\");
						int index=filePath.indexOf(rootPath);
						if(index>=0)
						{
							String url=filePath.substring(index+rootPath.length(),filePath.length()-index);
							url="/files/AUSER"+url;
							jo.put("url",url);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return js;
	}

}
