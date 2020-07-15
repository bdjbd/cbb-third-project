package com.am.frame;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/***
 * 获取变量信息
 * @author xianlin
 *
 */
public class VariableAcquisition {

		//私有的默认构造子
		private VariableAcquisition() {}
		//注意，这里没有final
		private static VariableAcquisition singleva=null;
		//静态工厂方法 
		public synchronized  static VariableAcquisition getInstance() {
			 if (singleva == null) { 
				 singleva = new VariableAcquisition();
			 }
			 return singleva;
		}
		
	
		public String returnstr(String varid,DB db) throws JDBCException
		{
			String SQL="SELECT vvalue AS value FROM avar WHERE vid = '"+varid+"'";
			MapList maplist = db.query(SQL);
			String value = maplist.getRow(0).get("value");
			
			return value;
			
		}
	
}
