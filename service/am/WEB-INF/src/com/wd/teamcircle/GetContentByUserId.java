package com.wd.teamcircle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wd.ICuai;
import com.wd.comp.Constant;
import com.wd.tools.CommonUtil;
import com.wd.tools.DatabaseAccess;

/**
 * 按照分页，获取班组圈子列表内容
 * */
public class GetContentByUserId implements ICuai {

	/**
	 * 获取班组圈子列表内容
	 * @param jsonArray 参数{"当前用户";"当前页数"}
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		String userid="";
		int currentPage=1;//当前要加载的数据页数
		try {
			userid=jsonArray.getString(0);//当前用户
			currentPage=jsonArray.getInt(1);//当前页数
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int pagesize=10;
		pagesize=Constant.PAGE_SIZE;
		String sql="select a.id,publisher,b.username,idea,a.createtime,a.value,c.name,photopath,b.sex,ROW_NUMBER() OVER(order by a.createtime desc)" +
				" from tc_ContentPublish a,auser b,tc_SubClass c" +
				" where a.publisher=b.userid and a.value=c.value and id in" +
				" (select b.id from tc_CircleMembers a,tc_purview b where a.cid=b.cid and a.userid='"+userid+"') order by a.createtime desc" +
				" OFFSET ("+currentPage+"-1)*"+pagesize+" LIMIT "+pagesize;
		JSONArray js=DatabaseAccess.query(sql);
		if(js!=null && js.length()>0)
		{
			for(int i=0;i<js.length();i++)
			{
				JSONObject jo=null;
				String id="";
				try {
					jo = js.getJSONObject(i);
					
					String createtime=jo.getString("CREATETIME");
					jo.put("CREATETIME",CommonUtil.getDiffTimeToFormat(createtime));
					
					id=jo.getString("ID");
					String psql="select path from tc_ContentPicture where id='"+id+"' order by cid";
					JSONArray pjs=DatabaseAccess.query(psql);
					jo.put("PICTURE",pjs);//内容图片
					
					String csql="select pid,a.userid,b.username,replyuser,c.username as replyusername,content,paths,a.createtime" +
							" from tc_TeamComment a" +
							" left join auser b on a.userid=b.userid" +
							" left join auser c on a.replyuser=c.userid" +
							" where id='"+id+"'" +
							" order by a.createtime";
					JSONArray cjs=DatabaseAccess.query(csql);
					jo.put("COMMENT",cjs);//评论
					
					String zsql="select id,a.userid,username,a.createtime from tc_Approval a,auser b" +
							" where a.userid=b.userid and id='"+id+"' order by a.createtime";
					JSONArray zjs=DatabaseAccess.query(zsql);
					jo.put("APPROVAL",zjs);//赞
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return js;
	}
}
