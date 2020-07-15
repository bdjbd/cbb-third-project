package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONException;
import com.wd.ICuai;

/**
 * 通过sql执行查询
 * 
 * @author zhouxn
 * @time 2012-5-14
 */
public class ExecutionSqlQueryCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String sql = null;
		try {
			sql = jsonArray.getString(0);
			sql = EncryptionDecryption.decrypt(sql);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return DatabaseAccess.query(sql);
	}
}
