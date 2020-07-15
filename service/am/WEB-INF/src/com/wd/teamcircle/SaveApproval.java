package com.wd.teamcircle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.message.server.MessageOpertion;
import com.wd.tools.DatabaseAccess;

/**
 * 保存赞信息
 * */
public class SaveApproval implements ICuai {

	/**
	 * 保存赞
	 * @param jsonArray {"内容id";"用户id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String zid=UUID.randomUUID().toString();
		String id="";
		String userid="";
		String createtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		try {
			id=jsonArray.getString(0);
			userid=jsonArray.getString(1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sql="INSERT INTO tc_approval(zid, id, userid, createtime)" +
				" VALUES ('"+zid+"','"+id+"','"+userid+"','"+createtime+"');";
		DatabaseAccess.execute(sql);
		
		
		String qrySql="select username from auser where userid='"+userid+"'";
		String qryUser="select publisher from tc_contentpublish where id='"+id+"'";
		
		JSONArray p_js=DatabaseAccess.query(qryUser);
		JSONArray user_js=DatabaseAccess.query(qrySql);
		MessageOpertion ms=new MessageOpertion();
		String username="";
		if(user_js!=null && user_js.length()>0)
		{
			String publisher="";
			try {
				publisher=p_js.getJSONObject(0).getString("PUBLISHER");
				username = user_js.getJSONObject(0).getString("USERNAME");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userid!=null&&!userid.equals(publisher)){
				ms.addMessage("【"+username+"】赞了您发发布的内容，请及时查看！",
						userid,publisher,"班组圈子","","TeamCircleUserInfoListActivity()");
			}
			
		}
		return new JSONArray();
	}

}
