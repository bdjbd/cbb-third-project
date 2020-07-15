package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 获取指定圈子的名称、封面、logo
 * */
public class GetTeamCircleInfo implements ICuai {

	/**
	 * 获取指定圈子的名称、封面、logo
	 * @param jsonArray 参数{"圈子Id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String cid="";
		try {
			cid=jsonArray.getString(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String selectSql="select cname,bgimage,logo from tc_teamCircle where cid='"+cid+"'";
		return DatabaseAccess.query(selectSql);
	}
}
