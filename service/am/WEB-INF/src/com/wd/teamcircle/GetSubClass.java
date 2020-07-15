package com.wd.teamcircle;

import org.json.JSONArray;

import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 获取所有启用的主题类别
 * */
public class GetSubClass implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		String sql="select value,name from tc_SubClass where startflag=1";
		return DatabaseAccess.query(sql);
	}

}
