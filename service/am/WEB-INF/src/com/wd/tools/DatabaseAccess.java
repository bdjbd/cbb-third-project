package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DBFactory;

/**
 * 数据库访问工具类
 * 
 * @author zhouxn
 * @time 2012-5-11
 */
public class DatabaseAccess {

	/**
	 * 查询方法(返回JsonObject集合)
	 * 
	 * @param java
	 *            .lang.String sql语句
	 * @return org.json.JSONArray json数组
	 */
	public static JSONArray query(String sql) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			MapList list = DBFactory.getDB().query(sql);
			int row_count=list.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=list.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				for (int j =0; j<column_count;j++) {
					String currentValue=row.get(j);
					if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
						jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						jo.put(row.getKey(j).toUpperCase(),
								currentValue);
					}
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
	
	/**
	 * 根据指定的数据库连接，跨库查询
	 * @param sql:sql语句
	 * @param db:数据库连接
	 * @return org.json.JSONArray json数组
	 */
	public static JSONArray query(String sql,String db) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			MapList list = DBFactory.getDB(db).query(sql);
			int row_count=list.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=list.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				for (int j =0; j<column_count;j++) {
					String currentValue=row.get(j);
					if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
						jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						jo.put(row.getKey(j).toUpperCase(),
								currentValue);
					}
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}

	/**
	 * 新增、修改、删除方法
	 * 
	 * @param java
	 *            .lang.String sql语句
	 * @return org.json.JSONArray json数组
	 */
	public static JSONArray execute(String sql) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			int i = DBFactory.getDB().execute(sql);
			returnJsonArray.put(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
	
	/**
	 * 离线功能，增量同步单项数据专用查询方法
	 * */
	public static JSONArray queryForOffline(String sql) {
		JSONArray returnJsonArray = new JSONArray();
		try {
			MapList list = DBFactory.getDB().query(sql);
			int row_count=list.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=list.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				for (int j =0; j<column_count;j++) {
					String currentValue=row.get(j);
					
					if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
						jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						currentValue=currentValue.replace(",","`");
						jo.put(row.getKey(j).toUpperCase(),
								currentValue);
					}
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
	
	/**
	 * 离线功能，同步数据专用查询方法
	 * @param data 数据集合
	 * @param tableName 表名
	 * @param pkName 主键名
	 * @param dataType 数据类型
	 * @param fileControlName 文件元素编号
	 * @param signControlName 签名元素编号
	 * @param sql sql语句
	 * */
	public static JSONArray queryForOffline(JSONArray data,String tableName,String pkName,String dataType,String fileControlName,String signControlName,String sql) {
		JSONArray returnJsonArray =data;
		try {
			MapList list = DBFactory.getDB().query(sql);
			int row_count=list.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=list.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				//数据头信息，包括：表名、主键名、数据类型、文件元素编号、签名元素编号
				String head=tableName+"`"+pkName+"`"+dataType+"`"+fileControlName+"`"+signControlName;
				jo.put("head",head);
				for (int j =0; j<column_count;j++) {
					String currentValue=row.get(j);
					if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
						jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						currentValue=currentValue.replace(",","`");
						jo.put(row.getKey(j).toUpperCase(),
								currentValue);
					}
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}
}
