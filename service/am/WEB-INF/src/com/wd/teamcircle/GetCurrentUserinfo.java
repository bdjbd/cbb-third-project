package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 获取当前成员的封面图片路径，姓名，头像路径
 * */
public class GetCurrentUserinfo implements ICuai{

	/**
	 * 获取当前成员的封面图片路径，姓名，头像路径
	 * @param jsonArray 参数{"当前用户id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String userid="";
		try {
			userid=jsonArray.getString(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String str="select a.userid,b.username,bgimage,photopath,sex from tc_CircleMembers a,auser b where a.userid=b.userid and a.userid='"+userid+"'";
		return DatabaseAccess.query(str);
	}

}
