package com.wd.init.login;

import org.json.JSONArray;
import org.json.JSONException;

import com.fastunit.util.DigestUtil;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 查询指定用户信息
 * 
 * @author wy
 * 
 */
public class FindUserInfoCuai implements ICuai {
	/**
	 * 查询用户信息
	 * 
	 * @param 指定用户
	 * @return 用户信息
	 */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray==null)
		{
			return null;
		}
		JSONArray retJsonArray = new JSONArray();
		try {
			// 得到输入用户名
			String userName = jsonArray.getString(0);
			// 得到密码
			String userPwd = jsonArray.getString(1);
			
			// 将密码转换成密文
			String encryptionPwd = DigestUtil.MD5(userPwd);
			String sql = "select u.userid,u.username,u.orgid,(select a.orgname from AORG a where a.orgid =  u.orgid) as orgname from auser u where UPPER(u.userid) = UPPER('"
					+ userName + "') and u.password='" + encryptionPwd + "'";
			retJsonArray = DatabaseAccess.query(sql);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retJsonArray;
	}

}
