package com.wd.globalsearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.wd.ICuai;
/**
 * 全文搜索服务提供者
 * @author 丁照祥
 * */
public class GlobalSearchCuai implements ICuai {

	/**
	 * 启动全文搜索
	 * @param jsonArray 搜索关键字
	 * @return 搜索到的相关信息
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String keyName="";
		JSONArray rs=new JSONArray();
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		try {
			keyName=jsonArray.getString(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(keyName!=null && !keyName.equalsIgnoreCase(""))
		{
			MapList list=getSearchClass();
			if(list!=null && list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					JSONObject jo=new JSONObject();
					Row row=list.getRow(i);
					String path=row.get("path");
					try {
						Class c = Class.forName(path);
						IGlobalSearch globalSearch = (IGlobalSearch)c.newInstance();
						JSONArray js=globalSearch.search(keyName);
						jo.put("name",row.get("name"));
						jo.put("type",row.get("type"));
						jo.put("data",js);
						rs.put(jo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return rs;
	}
	
	/**
	 * 查询所有搜索类
	 * */
	private MapList getSearchClass()
	{
		String sql="select id,name,path,type from abdp_globalSearch order by o asc";
		DB db=null;
		MapList list=null;
		try {
			db = DBFactory.getDB();
			list=db.query(sql);
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
