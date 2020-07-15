package com.wd.teamcircle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.comp.Constant;
import com.wd.message.server.MessageOpertion;
import com.wd.tools.DatabaseAccess;

/**
 * 保存发布的内容
 * */
public class SaveContent implements ICuai {

	/**
	 * 保存发布的内容
	 * @param jsonArray,参数{"发布者";"发布内容";"主题类别";"圈子id1,圈子id2"}
	 * @return JSONArray {"内容id"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		JSONArray js=new JSONArray();
		String id=UUID.randomUUID().toString();
		String publisher="";
		String idea="";
		String createtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String value="";
		String cid="";
		try {
			publisher=jsonArray.getString(0);//发布者
			idea=jsonArray.getString(1);//发布内容
			value=jsonArray.getString(2);//主题类别
			cid=jsonArray.getString(3);//圈子id
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//发布者
		String sql="INSERT INTO tc_contentpublish(id,publisher,idea,createtime,value)" +
				" VALUES ('"+id+"','"+publisher+"','"+idea+"','"+createtime+"','"+value+"');";
		JSONArray rs=DatabaseAccess.execute(sql);
		
		String []cids=cid.split(",");
		
		String ins="";
		for(int i=0;i<cids.length;i++)
		{
			String currentCid=cids[i];
			if(currentCid!=null && !currentCid.trim().equalsIgnoreCase(""))
			{
				ins+="INSERT INTO tc_purview(id, cid) VALUES ('"+id+"','"+currentCid+"');";
			}
		}
		if(!ins.equalsIgnoreCase(""))
		{
			DatabaseAccess.execute(ins);
		}
		if(rs!=null)
		{
			js.put(id);
		}
		//推送消息提醒
		String qrySql="select username from auser where userid='"+publisher+"'";
		JSONArray user_js=DatabaseAccess.query(qrySql);
		if(user_js!=null && user_js.length()>0)
		{
			String username="";
			try {
				username = user_js.getJSONObject(0).getString("USERNAME");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addMessageByCircle("【"+username+"】在班组圈子里发布了新内容，请及时查看！",publisher,"班组圈子","","TeamCircleUserInfoListActivity()",cids);
		}
		
		return js;
	}
	
	/**
	 * 给当前人员所在圈子的所有成员推送消息
	 * @param content
	 *            消息内容
	 * @param senderuser
	 *            发送者
	 * @param receiver
	 *            接收者，多个接收者以英文逗号隔开
	 * @param msgType
	 *            消息类型
	 * @param pcurl
	 *            pc端链接
	 * @param androidurl
	 *            android端链接
	 * @param cids
	 *  		  所属班组圈子
	 * @return 成功失败标识
	 * */
	private String addMessageByCircle(String content,String senderuser,String msgType, String pcUrl, String androidUrl,String []cids) {
		String receivers = "";
		String currentCipS="";
		if(cids==null || cids.length <=0)
		{
			return "";
		}
		for(int i=0;i<cids.length;i++)
		{
			currentCipS+="'"+cids[i]+"',";
		}
		currentCipS+="''";
		String strSql = "select string_agg(distinct userid,',')userid from tc_circlemembers" +
				" where cid in("+currentCipS+") and userid!='"+senderuser+"'";
		JSONArray js = DatabaseAccess.query(strSql);
		if(js!=null && js.length()>0)
		try {
			receivers  = js.getJSONObject(0).getString("USERID");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MessageOpertion ms=new MessageOpertion();
		ms.addMessage(content, senderuser, receivers, msgType, pcUrl, androidUrl);
		return Constant.SUCCESS;
	}
}
