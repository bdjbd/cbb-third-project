package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 删除内容、内容图片、评论、赞、权限信息
 * */
public class DeleteContent implements ICuai {

	/**
	 * 删除内容、内容图片、评论、赞、权限信息
	 * @param jsonArray 参数{"内容id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String id="";
		try {
			id = jsonArray.getString(0);//内容id
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String delSql="delete from tc_ContentPicture where id='"+id+"';";
		delSql+="delete from tc_TeamComment where id='"+id+"';";
		delSql+="delete from tc_Approval where id='"+id+"';";
		delSql+="delete from tc_purview where id='"+id+"';";
		delSql+="delete from tc_ContentPublish where id='"+id+"';";
		JSONArray rs=DatabaseAccess.execute(delSql);
		return rs;
	}

}
