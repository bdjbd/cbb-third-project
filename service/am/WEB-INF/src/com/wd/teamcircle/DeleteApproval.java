package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;
import com.wd.ICuai;
import com.wd.tools.DatabaseAccess;

/**
 * 删除赞
 * */
public class DeleteApproval implements ICuai {

	/**
	 * 删除赞
	 * @param jsonArray {"内容id";"用户id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		String id="";
		String userid="";
		try {
			id=jsonArray.getString(0);
			userid=jsonArray.getString(1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String sql="DELETE FROM tc_approval WHERE id='"+id+"' and userid='"+userid+"';";
		JSONArray rs=DatabaseAccess.execute(sql);
		return rs;
	}
}
