package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 获取主题类别和当前登录人的圈子
 * */
public class GetSubClassAndCircle implements ICuai {

	/**
	 * 获取主题类别和当前登录人的圈子
	 * @param jsonArray {"用户id"}
	 * @return JSONArray 结果集里有两个子集，第一个子集是主题类别，第二个子集是圈子，每个子集都是一个JSONArray。
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		JSONArray js=new JSONArray();
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String userid="";
		try {
			userid = jsonArray.getString(0);
			JSONArray js_subClass= new GetSubClass().doAction(null);
			String sql="select a.cid,b.cname from tc_CircleMembers a,tc_teamCircle b" +
					" where a.cid=b.cid and a.userid='"+userid+"'";
			JSONArray js_Circle=DatabaseAccess.query(sql);
			js.put(0,js_subClass);//主题类别
			js.put(1,js_Circle);//当前用户所属圈子
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return js;
	}

}
