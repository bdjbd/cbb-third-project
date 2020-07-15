package com.wd.tools;

import org.json.JSONArray;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Transaction;
import com.fastunit.jdbc.exception.JDBCException;
import com.wd.ICuai;

/**
 * 新增、修改、删除执行sql
 * 
 * @author zhouxn
 * @time 2012-5-15
 */
public class ExecutionSqlCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJSONArray = new JSONArray();
		Transaction t = null;
		try {
			// 启动事务
			
			String sql = jsonArray.getString(0);
			sql = EncryptionDecryption.decrypt(sql);
			String userId = jsonArray.getString(1).toString();
			t = DBFactory.getDB().beginTransaction();
			returnJSONArray = DatabaseAccess.execute(sql);
			if (returnJSONArray.length() > 0) {
				CommonUtil.addBusinesslog(sql, userId);
			}
			t.commit();
		} catch (Exception e) {
			try {
				t.rollback();
			} catch (JDBCException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return returnJSONArray;
	}
}
