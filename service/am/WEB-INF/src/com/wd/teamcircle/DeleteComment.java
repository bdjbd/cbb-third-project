package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 删除评论
 * */
public class DeleteComment implements ICuai {

	/**
	 * 删除评论
	 * @param 参数{"评论id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String pid="";
		try {
			pid = jsonArray.getString(0);//评论id
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String del="delete from tc_TeamComment where pid='"+pid+"'";
		JSONArray rs=DatabaseAccess.execute(del);
		return rs;
	}

}
