package com.p2p.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *   
 * 数据工具类
 *   @create 	Jul 15, 2014 
 *   @version	$Id
 */
public class DBUtil {

	private static DataSource mDataSource=null;
	private static ThreadLocal<Connection> connLocal=new ThreadLocal<Connection>();
	
	private DBUtil(){}
	
//	static{
//		try{
//			Properties prop=new Properties();
//			InputStream is=DBUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
//			prop.load(is);
//			mDataSource=BasicDataSourceFactory.createDataSource(prop);
//		}catch(Exception e){
//			throw new ExceptionInInitializerError(e);
//		}
//	}
	
	public static DataSource getDataSource(){
		return mDataSource;
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnections()throws SQLException{
		Connection conn=connLocal.get();
		if(conn==null){
			conn=mDataSource.getConnection();
			connLocal.set(conn);
		}
		return conn;
	}
	
	/**
	 *  关闭数据库连接
	 * @throws SQLException
	 */
	public static void closeConnections()throws SQLException{
		Connection conn=connLocal.get();
		connLocal.set(null);
		if(conn!=null){
			conn.close();
		}
	}
	
	/**
	 * ResultSet转换正JSON格式
	 * @param rst
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONObject resultSetToJSON(ResultSet rst) throws SQLException,JSONException{
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
