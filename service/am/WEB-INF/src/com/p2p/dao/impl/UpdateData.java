package com.p2p.dao.impl;

import java.sql.SQLException;
import java.sql.Statement;


/**
 * Author: Mike
 * 2014年7月15日
 * 说明：Insert,Update，Delete
 *
 **/
public class UpdateData {
	//返回影响行数
	public int execute(Statement stat,String sql) throws SQLException{
		return stat.executeUpdate(sql);
	}
}
