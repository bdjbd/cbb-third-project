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
 * 保存班组圈子评论内容
 * */
public class SaveComment implements ICuai {

	/**
	 * 保存班组圈子评论内容
	 * @param jsonArray 参数{"内容id";"发布者";"评论内容";"被回复者"}
	 * @return JSONArray {"评论id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String id="";
		String userid="";
		String content="";
		String replyuser="";
		try {
			id=jsonArray.getString(0);//内容id
			userid=jsonArray.getString(1);//发布者
			content=jsonArray.getString(2);//评论内容
			replyuser=jsonArray.getString(3);//被回复者
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String pid=UUID.randomUUID().toString();
		String createtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String sql="INSERT INTO tc_teamcomment(id,pid,userid,content,replyuser,createtime)" +
				" values ('"+id+"','"+pid+"','"+userid+"','"+content+"','"+replyuser+"','"+createtime+"')";
		JSONArray rs=DatabaseAccess.execute(sql);
		JSONArray js=new JSONArray();
		if(rs!=null)
		{
			js.put(pid);
		}
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
			ms.addMessage("【"+username+"】对您的班组圈子内容发表了评论，请及时查看！",userid,publisher,"班组圈子","","TeamCircleUserInfoListActivity()");
			
			if(replyuser!=null && !replyuser.equalsIgnoreCase(""))
			{
				ms.addMessage("【"+username+"】回复了您在班组圈子里发表的评论，请及时查看！",userid,replyuser,"班组圈子","","TeamCircleUserInfoListActivity()");
			}
		}
		
		return js;
	}

}
