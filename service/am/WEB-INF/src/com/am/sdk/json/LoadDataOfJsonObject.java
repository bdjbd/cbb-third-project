package com.am.sdk.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class LoadDataOfJsonObject
{
	/*
	 * 使用TableList构建主子表结构，并传入首次查询数据的sql
	 * 
	 * 返回相应结构的json字符串
	 * */
	
	
	final Logger logger = LoggerFactory.getLogger(LoadDataToJson.class);
	
	private JSONArray mDataList=null;
	
	
	
	public LoadDataOfJsonObject(TableList tl,String tSql)
	{
		mDataList=load(tl,tSql);
	}
	
	public String getJson()
	{
		return mDataList.toString();
	}
	
	private JSONArray load(TableList tl,String tSql)
	{
		JSONArray rString=null;
		
		if(tl.getUpIDName().length()>0)
			rString=loadTableTreeData(tl,tSql);
		else
			rString=loadTableData(tl,tSql);

		return rString;
	}
	
	private JSONObject loadChildTableData(TableList tl,Row row)
	{
		TableList childTableList=null;
		JSONObject tJO=new JSONObject();
		
		logger.debug("loadChildTableData().tl[" + tl.size() + "].getTableName()=" + tl.getTableName());
		try 
		{
			//tJO=new JSONObject();
			for(int i=0;i<tl.size();i++)
			{
				childTableList=tl.getChildTable(i);
				
				logger.debug("loadChildTableData().childTableList[" + i + "].getTableName()=" + childTableList.getTableName());
				
				tJO.put(childTableList.getTableName().toUpperCase(), load(childTableList,childTableList.getSQL(row,childTableList)));

			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tJO;
	}
	
	private JSONArray loadTableData(TableList tl,String tSql)
	{
		String rValue="";
		JSONArray tJSONArray=null;
		JSONObject tJO=null;
		TableList childTableList=null;
		
		try 
		{
			logger.debug("loadTableData().tSql=" + tSql);
			
			DB db = DBFactory.getDB();
			MapList map=db.query(tSql);
			
			logger.debug("loadTableData().map.size()=" + map.size());
			
			tJSONArray=new JSONArray();
			
			int rowCount=0;
			for(int i=0;i<map.size();i++)
			{
				tJO=new JSONObject();
				for(int l=0;l<map.getRow(i).size();l++)
					getJsonField(map.getKey(l),map.getRow(i).get(l),tJO);
				
				tJSONArray.put(tJO);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return tJSONArray;
	}
	
	private JSONArray loadTableTreeData(TableList tl,String tSql)
	{

		JSONArray tJSONArray=null; //{"COUNT":"1","DATA":[{"" : "","" : "","":""}]}";
		try 
		{
			logger.debug("loadTableTreeData().tSql=" + tSql);
			
			DB db = DBFactory.getDB();
			MapList map=db.query(tSql);
			
			logger.debug("loadTableTreeData().map.size()=" + map.size());
			
			JSONObject tJO=null;
			tJSONArray=new JSONArray();

			for(int i=0;i<map.size();i++)
			{
				tJO=new JSONObject();
				//添加所有列
				for(int l=0;l<map.getRow(i).size();l++)
					getJsonField(map.getKey(l),map.getRow(i).get(l),tJO);
				
				//获取子表数据
				//loadChildTableData(tl,map.getRow(i),tJO);
				
				//tJO.

				//获取递归数据
				//tJO=new JSONObject();
				//tJO.put(tl.getTableName().toUpperCase(), loadTableTreeData(tl,tl.getTemplateSQL(map.getRow(i),tl)));
				
				//tJSONArray.put(tJO);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return tJSONArray;
	}

	
	public void getJsonField(String name,String value,JSONObject jo)
	{	
		try 
		{
			if (value == null || value.trim().equalsIgnoreCase("null"))
				jo.put(name.toUpperCase(), "");
			else
				jo.put(name.toUpperCase(),value);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
	}
	
	//获得结果集中首行的第一个字段值
	public String getDBTableTopRowField(String sql, DB db,String dfValue)
			throws JDBCException 
	{
		String rValue = dfValue;
		MapList map = db.query(sql);

		if (!Checker.isEmpty(map))
			rValue = map.getRow(0).get(0);

		return rValue;
	}

}