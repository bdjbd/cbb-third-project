package com.wd.globalsearch.sstl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.fastunit.Path;
import com.wd.globalsearch.IGlobalSearch;
import com.wd.tools.CommonUtil;

/**
 * 按照关键字搜索指定的图片
 * @author dzx
 * */
public class GlobalSearchPicture implements IGlobalSearch {

	/**
	 * 按照关键字搜索指定的图片
	 * @param keyName 关键字
	 * @return 文档信息集合，包括：文档名称，路径，后缀名
	 * */
	@Override
	public JSONArray search(String keyName) {
		// TODO Auto-generated method stub
		String extension="'jpg','gif','png','JPEG'";
		JSONArray js=CommonUtil.GlobalSearchFilesInfo(keyName, extension);
		if(js!=null && js.length()>0)
		{
			for(int i=0;i<js.length();i++)
			{
				JSONObject jo=null;
				String filePath="";
				try {
					jo=js.getJSONObject(i);
					filePath=jo.getString("path");
					if(!filePath.equalsIgnoreCase(""))
					{
						String rootPath=(Path.getRootPath()+"\\files\\bzgl_casemanage").replace("/","\\");
						int index=filePath.indexOf(rootPath);
						if(index>=0)
						{
							String url=filePath.substring(index+rootPath.length(),filePath.length()-index);
							url="/files/CASEMANAGE"+url;
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
