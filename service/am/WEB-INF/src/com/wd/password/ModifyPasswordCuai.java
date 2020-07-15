package com.wd.password;

import org.json.JSONArray;
import org.json.JSONException;

import com.fastunit.util.DigestUtil;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 修改密码
 * @author gj
 * 
 * 2012-11-06 集成到平台
 */
public class ModifyPasswordCuai implements ICuai {
	/**
	 * 修改密码
	 * @param 指定用户
	 * @return 执行结果
	 */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray retJsonArray = new JSONArray();
		try {
			// 得到输入用户名
			String userName = jsonArray.getString(0);
			// 得到密码
			String userPwd = jsonArray.getString(1);

			// 将密码转换成密文
			String encryptionPwd = DigestUtil.MD5(userPwd);
			String sql = "UPDATE auser "
				+ "SET password='" + encryptionPwd + "' "
				+ "WHERE UPPER(userid) = UPPER('" + userName + "') ";
			retJsonArray = DatabaseAccess.execute(sql);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retJsonArray;
	}

}
