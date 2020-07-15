package com.wd.user;

import org.json.JSONArray;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

public class FindUserInfoCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String sql = "select t.tid,t.username,t.usersex,to_char(t.birthday,'YYYY-MM-DD') as birthday,t.hobby,t.email,t.remark from USERINFO t";
		return DatabaseAccess.query(sql);
	}
}
