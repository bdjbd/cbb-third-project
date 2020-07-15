package com.am.sdk.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class LoadDataToJson
{
	/*
	 * 使用TableList构建主子表结构，并传入首次查询数据的sql
	 * 
	 * 返回相应结构的json字符串
	 * */
	
	
	final Logger logger = LoggerFactory.getLogger(LoadDataToJson.class);
	
	private String mJsonString="";
	
	private DBManager dbm=new DBManager();
	
	
	
	public LoadDataToJson(TableList tl,String tSql)
	{
		mJsonString="" + load(tl,tSql) + "";
	}
	
	public String getJson()
	{
		return mJsonString;
	}
	
	private String load(TableList tl,String tSql)
	{
		String rString="";
		
		if(tl.getUpIDName().length()>0)
			rString=loadTableTreeData(tl,tSql);
		else
			rString=loadTableData(tl,tSql);
		logger.debug(rString);
		return "" + rString + "";
	}
	
	private String loadChildTableData(TableList tl,Row row)
	{
		TableList childTableList=null;
		String rValue="";
		
		logger.debug("loadChildTableData().tl[" + tl.size() + "].getTableName()=" + tl.getTableName()+row.toString());
		if(tl.size()>0)
		for(int i=0;i<tl.size();i++)
		{
			childTableList=tl.getChildTable(i);
			
			logger.debug("loadChildTableData().childTableList[" + i + "].getTableName()=" + childTableList.getTableName());
			logger.debug(load(childTableList,childTableList.getSQL(row,childTableList))+"77777");
			if(!load(childTableList,childTableList.getSQL(row,childTableList)).equals("[]")){
			rValue+=",\"" + childTableList.getTableName().toUpperCase() + "\"";
			rValue+=":" + load(childTableList,childTableList.getSQL(row,childTableList)) + "";
		}
		}
		logger.debug(rValue);
		return rValue;
	}
	
	private String loadTableData(TableList tl,String tSql)
	{
		String rValue=""; 
		try 
		{
			logger.debug("loadTableData().tSql=" + tSql);
			
			MapList map=dbm.query(tSql);
			
			logger.debug("loadTableData().map.size()=" + map.size());
		
			int rowCount=0;
			if(map.size()>0)
			for(int i=0;i<map.size();i++)
			{
				if(rowCount>0)
					rValue+=",";
				
				rValue+="{" + getJsonField(map.getKey(0),map.getRow(i).get(0));
				for(int l=1;l<map.getRow(i).size();l++)					
					rValue+="," + getJsonField(map.getKey(l),map.getRow(i).get(l));
				
				//获取子表数据
				rValue+=loadChildTableData(tl,map.getRow(i));
				
				rValue+="}";
				
				rowCount++;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			//logger.error(e.getMessage());
		}
		
		return "[" + rValue + "]";
	}
	
	private String loadTableTreeData(TableList tl,String tSql)
	{
		//DB db = null;

		String rValue=""; //{"COUNT":"1","DATA":[{"" : "","" : "","":""}]}";
		try 
		{
			logger.debug("loadTableTreeData().tSql=" + tSql);
			
			MapList map=dbm.query(tSql);
			
			logger.debug("loadTableTreeData().map.size()=" + map.size());

			int tIsOneRow=0;
			if(map.size()>0){
			for(int i=0;i<map.size();i++)
			{
				if(tIsOneRow>0)
					rValue+=",";
				
				rValue+="{" + getJsonField(map.getKey(0),map.getRow(i).get(0));
				for(int l=1;l<map.getRow(i).size();l++)
					rValue+="," + getJsonField(map.getKey(l),map.getRow(i).get(l));
				logger.debug("map.getRow(i)=" +map.getRow(i));
				//获取子表数据				
				rValue+=loadChildTableData(tl,map.getRow(i));				
				//获取递归数据
				if(loadTableTreeData(tl,tl.getTemplateSQL(map.getRow(i),tl)).equals("[]")){
					rValue+=",\"" + tl.getTableName().toUpperCase() + "1"+"\" : ";
				}else{
					rValue+=",\"" + tl.getTableName().toUpperCase() +"\" : ";
				}
				
				
					rValue+=loadTableTreeData(tl,tl.getTemplateSQL(map.getRow(i),tl));
	
			
				
				rValue+="}";
				
				tIsOneRow++;
				
			}
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.debug( rValue+"2222222222");
		return "[" + rValue + "]";
	}
	
	/** 
	* 判断是否是json结构 
	*/ 
	public boolean isJson(String value) 
	{ 
		try 
		{ 
			new JSONObject(value);
		} 
		catch (Exception e) 
		{
			try 
			{
				new JSONArray(value);
			} 
			catch (Exception e1) 
			{
				return false;
			}
		} 
		return true; 
	} 

	
	public String getJsonField(String name,String value)
	{
		String rValue="\"" + name.toUpperCase() + "\"";
		
		//logger.debug("getJsonField().isJson(" + value + ")=" + isJson(value));
		
		//如果是json则不带双引号
		if(isJson(value))
			rValue+=":" + value + "";
		else
		{
			if(value!=null)
			{
				value=value.replaceAll("\"", "\'");
				value=value.replaceAll("[\\t\\n\\r]", "");
			}
			
			//如果为null则置为“”
			if (value == null || value.trim().equalsIgnoreCase("null")) 
				value="";
			
			rValue+=":\"" + value + "\"";
		}
		
		return rValue;
	}
	
	private String CHAR(int i) {
		// TODO 自动生成的方法存根
		return null;
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