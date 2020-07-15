package com.p2p.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Author: Mike
 * 2014年7月15日
 * 说明：执行查询语句
 *
 **/
public class QueryDate {

	public ResultSet execute(Statement stat,String sql) throws SQLException{
		return stat.executeQuery(sql);
	}
}
