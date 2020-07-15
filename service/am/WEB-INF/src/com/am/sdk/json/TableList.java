package com.am.sdk.json;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.Row;

public class TableList 
{
	final Logger logger = LoggerFactory.getLogger(LoadDataToJson.class);
	
	private String mTableName="";
	private String mIDName="";
	private String mUpIDName="";
	private String mSQL="";
	private String mTemplateSQL="";
	
	private ArrayList ChiledTables=null;
	
	/**
	 * 
	 * @param TableName 表名
	 * @param IDName 主表的主键名称
	 * @param UpIDName 上级id名称，当该字段为空时系统不会做递归查询
	 * @param tSQL 依据主表id查询子表记录的sql语句
	 * @param tTemplateSQL 递归树状结构的sql
	 */
	public TableList(String TableName,String IDName,String UpIDName,String tSQL,String tTemplateSQL)
	{
		mTableName=TableName;
		mIDName=IDName;
		mUpIDName=UpIDName;
		mSQL=tSQL;
		mTemplateSQL=tTemplateSQL;
		
		ChiledTables=new ArrayList();
	}
	
	public void addChildTable(TableList tl)
	{
		ChiledTables.add(tl);
	}
	
	public int size()
	{
		return ChiledTables.size();
	}
	
	public TableList getChildTable(int index)
	{
		return (TableList)ChiledTables.get(index);
	}
	
	public String getTableName()
	{
		return mTableName;
	}
	
	public String getIDName()
	{
		return mIDName;
	}
	
	public String getUpIDName()
	{
		return mUpIDName;
	}
	
	public String getExcSQL(Row row,TableList tl)
	{
		String rValue="";
		
		logger.debug(" tl.getIDName()=" + tl.getIDName() + "\n");
		logger.debug(" tl.getUpIDName()=" + tl.getUpIDName() + "\n");
		
		if(this.getUpIDName().length()>0)
			rValue=getTemplateSQL(row,tl);
		else
			rValue=getSQL(row,tl);
		
		logger.debug(" getExcSQL()=" + rValue + "\n");
		
		
		return rValue;
	}
	
	//返回递归是需要使用的sql
	public String getSQL(Row row,TableList tl)
	{	
		logger.debug(tl.getIDName() + "=" + tl);
		String value=row.get(tl.getIDName()).toString();
		
		logger.debug(tl.getIDName() + "=" + value);
		
		return mSQL.replace("[" + tl.getIDName() + "]", value);
	}
	
	//返回主子表需要使用的sql
	public String getTemplateSQL(Row row,TableList tl)
	{
		/*
		String rValue="";
		for(int l=0;l<row.size();l++)
			rValue+="\n" + row.getKey(l) + "=" + row.get(l);
		
		logger.debug("getTemplateSQL(" + tl.getIDName() + ").value=" + rValue);
		*/
		
		
		//String value=row.get(tl.getIDName()).toString();
		//String tSql=mTemplateSQL.replace("[" + tl.getIDName() + "]", value);
		
		String value=row.get(tl.getUpIDName()).toString();
		String tSql=mTemplateSQL.replace("[" + tl.getUpIDName() + "]", value);
		
		return tSql;
	}

}
