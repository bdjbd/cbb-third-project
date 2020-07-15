package com.am.frame.webapi.db;

import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

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
import com.google.gson.JsonArray;


/**
 * 公用数据库方法
 * 
 * @author wz 2015-12-1 15:57:14
 *
 */
public class DBManager {
	
	private static final Logger log =LoggerFactory.getLogger(DBManager.class);
	 
	 
	/**
	 * @param sql
	 * @param ThisPageNumber 当前页
	 * @param PageCount 每页条数
	 * @return 
	 * {
		count : "" //总条数
		ThisPageNumber : "" //当前页数
		MapPageNumber : "" //最大页数
		DATA : [] //数据集合
		}
	 */
	public JSONObject query(String sql, String thisPageNumber, String pageSize) {
		
		return query(sql, thisPageNumber, pageSize,true);
	}
	


	/**
	 * 操作数据库的增、删、改操作
	 * @param sql
	 * @return  CODE 0 执行成功，1执行失败。
	 */
	public JSONObject update(String sql) {
		
		String str = "";
		
		int result = 0;
		
		JSONObject resultJson = null;
		
		DB db = null;
		
		try {
			
			db = DBFactory.newDB();
			
			result = db.execute(sql);
			
			if (result > 0) {
				str = "{\"CODE\":\"0\",\"MSG\":\"更新成功！\"}";
			} else {
				str = "{\"CODE\":\"1\",\"MSG\":\"更新失败！\"}";
			}
			
			resultJson= new JSONObject(str);
		
		} catch (Exception e) {
		
			e.printStackTrace();
		
		} finally {
			
			try {
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
			
		return resultJson;
	}

	public int execute(String sql, String[] values, int[] typs){
		int reuslt=0;
		DB db=null;
		try{
			db=DBFactory.newDB();
			db.execute(sql, values, typs);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return reuslt;
	}
	
	
	/**
	 * 解析post请求传过来的参数并执行sql语句
	 * [{\"ROWS\":[{\"COLUMNS\":[{\"colName1\":\"value1\"},{\"colName\":\"value2\"}],\"ID\":\"id1\",\"STATUS\":\"i\"},{\"COLUMNS\":[{\"colName2\":\"2value1\"},{\"colName\":\"2value2\"}],\"ID\":\"id2\",\"STATUS\":\"u\"}],\"TABLENAME\":\"AUSER\"}]
	 * @return JSONObject 返回一个json串
	 */
	public JSONObject executeJson(String str) {
		
		JSONArray jsonArray = null;
		
		// 获得表名的json
		JSONObject tableJo = null;
		
		// 获得行的json
		JSONObject rowJo = null;
		
		// 获得行的jsonarray
		JSONArray rowArray = null;
		
		// 返回json
		JSONObject reslultJson = null;
		
		DBManager dbmg = new DBManager();
		
		// update语句
		String usql = "";
		
		// delete语句
		String dsql = "";
		
		//tablename
		String tableNames = "";
		
		try {
			
			jsonArray = new JSONArray(URLDecoder.decode(str, "utf-8"));
			
			int  a =jsonArray.length();
			
			//循环表
			for (int i = 0; i < jsonArray.length(); i++) {
				
				tableJo = jsonArray.getJSONObject(i);
				
				//获得表名
				tableNames = tableJo.get("TABLENAME").toString();
				
				// 获得行
				rowArray = tableJo.getJSONArray("ROWS");
				
				// 循环行数组
				for (int j = 0; j < rowArray.length(); j++) {
					
					rowJo = rowArray.getJSONObject(j);
					
					// 判断状态是否是插入
					if ("i".equals(rowJo.get("STATUS").toString().toLowerCase())){
						
						String isql = dbmg.getInsertSql(rowJo,tableNames);
						 
						log.info("执行insert操作SQL>>>>"+isql);
						
						reslultJson = dbmg.update(isql);
						
					// 判断状态是否是更新  执行update方法
					} else if ("u".equals(rowJo.get("STATUS").toString().toLowerCase())) {
						
						usql = "UPDATE " +tableNames + " SET " + dbmg.getUpdateStr(rowJo) + " WHERE ID='"
								+ rowJo.get("ID").toString() + "'";
						 
						log.info("执行update操作SQL>>>>"+usql);
						 
						 reslultJson = dbmg.update(usql);
						
					// 判断状态是否是删除 执行delete逻辑删除方法
					} else if ("d".equals(rowJo.get("STATUS").toString().toLowerCase())) {
						
						dsql = "delete  from " + tableNames +"  WHERE ID='" + rowJo.get("ID").toString() + "'";
						 
						log.info("执行delete操作SQL>>>>"+dsql);
						 
						 reslultJson = dbmg.update(dsql);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reslultJson;
	};
	
	/**
	 *  生成insert语句
	 * @param rowChildArray 出入行中的每个语句
	 * @param tableName
	 * @return insert sql
	 */
	public String getInsertSql(JSONObject rowChildobj,String tableName) {
		
		String cols = "";
		
		String values="";
		
		try {
			
			//获取json中的key
			String key ="";
			
			JSONArray  columnsArr = rowChildobj.getJSONArray("COLUMNS");
			
			cols = "INSERT INTO " + tableName + " (ID" ;
			
			values = " VALUES('" +  rowChildobj.get("ID").toString() + "'";
			
			//循环每行中的每句jsonarray
			for (int i=0;i<columnsArr.length();i++){
				
				//获取每个字段json
				JSONObject fieldObj=  columnsArr.getJSONObject(i);
				
				Iterator set =fieldObj.keys();
				
				while (set.hasNext()) {  
					key = (String) set.next();  
	            } 
				if(fieldObj.length()>=1){
					cols += "," +key.toLowerCase();
					values+=",'" + fieldObj.get(key) + "'";
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return cols + ")" + values + ")";
	}


	/**
	 *  拼接update中的 set语句
	 * @param rowArray
	 * @return col1='xxx',col2='xxx'
	 */
	public String getUpdateStr(JSONObject rowObj) {
		//定义 数组中每段json的键
		String key ="";
		
		String str="";
		
		try{
			
			JSONObject fieldObj;
			
			//获取columns 列数组
			JSONArray  columnsArr = rowObj.getJSONArray("COLUMNS");
			
			for (int i=0;i<columnsArr.length();i++){
				
				fieldObj =  columnsArr.getJSONObject(i);
				
				Iterator<String> set = fieldObj.keys();
				
				while (set.hasNext()) {  
					key = set.next();  
	            } 
				
				str += key.toLowerCase() +"="+"'"+fieldObj.get(key)+"',";
			}
			
			str = str.substring(0, str.length()-1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
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
	
	/**
	 *  获取总页数
	 * @param rowArray
	 * @return 
	 */
	private int getRowCount(String coutSql) {
		
		int rowCount = 0;
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			MapList list = db.query(coutSql);
			
			rowCount  = list.getRow(0).getInt("count",0);

		} catch (Exception e1) {
			e1.printStackTrace();
		}finally {
			try {
				if(db!=null){
					db.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rowCount;
	}
	
	
	/**
	 * @param sql
	 * @param ThisPageNumber 当前页
	 * @param PageCount 每页条数
	 * @return 
	 * {
		count : "" //总条数
		ThisPageNumber : "" //当前页数
		MapPageNumber : "" //最大页数
		DATA : [] //数据集合
		}
	 */
	public MapList query(String sql) {
		
		MapList contentList=null;
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			contentList = db.query(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return contentList;
	}
	
	
	/**
	 * DB queyr()  MapList 
	 * 					com.fastunit.jdbc.DB.query(String arg0, String[] arg1, int[] arg2) 
	 * 					throws JDBCException
	 * @param sql
	 * @param values
	 * @param types
	 * @return  MapList
	 */
	public MapList query(String sql,String[] values,int[] types) {
		
		MapList contentList=null; 
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			contentList = db.query(sql,values,types);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return contentList;
	}
	
	
	/**
	 * @param sql
	 * @param ThisPageNumber 当前页
	 * @param PageCount 每页条数
	 * @return 
	 * {
		count : "" //总条数
		ThisPageNumber : "" //当前页数
		MapPageNumber : "" //最大页数
		DATA : [] //数据集合
		}
	 */
	public JSONObject query(String sql, String thisPageNumber, String pageSize ,boolean closeDB) {
		
		int rowCount = 0;
		
		int pageCount = 0;
		
		String coutSql = "SELECT count(*) as count FROM ("+sql+") as tablecount_";
		
		log.info(sql);
		
		String result = "{}";
		
		JSONObject resultJson = null;
		
		DB db = null;
		
		try {
			db = DBFactory.newDB();
			
			MapList mapList = db.query(coutSql);
			
			rowCount = mapList.getRow(0).getInt("count", 0);
		
			int offset = 0; 
				
			if(thisPageNumber!=null || pageSize!=null){
				pageCount = (rowCount + Integer.parseInt(pageSize) - 1) /Integer.parseInt(pageSize);
				offset = Integer.parseInt(pageSize)*(Integer.parseInt(thisPageNumber)-1);
				sql += " limit "+pageSize+" offset "+offset;
			}
			
			MapList contentList = db.query(sql);
			
			resultJson = mapListToJSon(contentList).getJSONObject(0);

			resultJson.put("COUNT",rowCount);
			
			resultJson.put("THISPAGENUMBER",thisPageNumber);
			
			resultJson.put("PAGECOUNT",pageCount);
			
			resultJson = new JSONObject(result);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			result = "{CODE:40007,'MESG':'"+e.getMessage()+"'}";
		
		} finally {
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return resultJson;
	}
	
	
	/**
	 * mapList 转换为 jsonArray
	 * @param list
	 * @return
	 */
	public JSONArray mapListToJSon(MapList list){
		JSONArray returnJsonArray = new JSONArray();
		int row_count=list.size();
		try {
			
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
						jo.put(row.getKey(j).toUpperCase(),currentValue);
					}
				}
			
				returnJsonArray.put(jo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnJsonArray;
	}



	public int execute(String exeSQL) {
		
		int reuslt=0;
		DB db=null;
		try{
			db=DBFactory.newDB();
			reuslt=db.execute(exeSQL);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return reuslt;
		
	}


	/**
	 * SQL MapList com.fastunit.jdbc.DB.query(String arg0, String arg1, int arg2) 
	 * 		throws JDBCException
	 * @param sql
	 * @param value
	 * @param type
	 * @return
	 */
	public MapList query(String sql, String value,
			int type) {
		
		MapList reuslt=null;
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			reuslt=db.query(sql,value,type);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return reuslt;
	}
	
	
	public JSONArray queryToJSON(String sql) {
		JSONArray jsonArrayReturn=null;
		MapList reuslt=null;
		DB db=null;
		try{
			db=DBFactory.newDB();
			reuslt=db.query(sql);
			jsonArrayReturn=mapListToJSon(reuslt);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonArrayReturn;
	}
	
	
}
