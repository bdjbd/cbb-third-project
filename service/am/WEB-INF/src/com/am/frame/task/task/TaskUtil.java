package com.am.frame.task.task;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TaskUtil {
	
	
	private static TaskUtil instance=null;
	
	private TaskUtil(){
		
	}
	
	public static synchronized TaskUtil getInstance(){
		if(instance == null){
			instance = new TaskUtil();
		}
		return instance;
	}
	
	
	
	/**
	 * ResultSet转换正JSON格式
	 * @param rst
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public JSONObject resultSetToJSON(ResultSet rst) throws SQLException,JSONException{
		JSONObject result=new JSONObject();
		JSONArray data=new JSONArray();
		ResultSetMetaData rsmd=rst.getMetaData();
		int columns=rsmd.getColumnCount();
		while(rst.next()){
			JSONObject row=new JSONObject();
			for(int i=1;i<=columns;i++){
				row.put(rsmd.getColumnLabel(i).toUpperCase(),rst.getObject(i)==null?"":rst.getObject(i));
			}
			data.put(row);
		}
		result.put("DATA",data);
		return result;
	}
	
	
}
