package com.wd.database;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.jdbc.impl.DBImplementor;

/**
 * 数据库类型工厂类
 * 
 * @author zhouxn
 * */
public class DataBaseFactory {
	// 使用业务数据库类型，暂时只支持PostgresSql和Oracle
	//private static final String DataBase_Type = "Oracle";

	/**
	 * 根据数据库类型返回IDataBase对象
	 * 
	 * @return IDataBase
	 * */
	public static IDataBase getDataBase() {
		IDataBase returnDataBase = null;
		DB db=null;
		try {
			db = DBFactory.getDB();
			String databaseName = ((DBImplementor)db).getJDBCSettings().getDatabase().getName();
			if (databaseName.equals("postgresql")) {
				returnDataBase = new PostgresDataBase();
			} else {
				returnDataBase = new OracleDataBase();
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return returnDataBase;
	}
}
