package com.wisdeem.wwd.management.member;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;

public class SyncMemberAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String memberCode = ac.getRequestParameter("membercode");
		String sql = "SELECT m.openid, p.token, p.app_id,p.app_secret FROM ws_public_accounts AS p LEFT JOIN ws_member AS m ON p.orgid=m.orgid WHERE m.member_code="
				+ memberCode;
		DB db = DBFactory.getDB();
		MapList map = db.query(sql);
		if (!Checker.isEmpty(map)) {
			try {
				String token = map.getRow(0).get("token");
				String appid = map.getRow(0).get("app_id");
				String appSecret = map.getRow(0).get("app_secret");
				String openid=map.getRow(0).get("openid");
				String accToken = Utils.getAccessToken(token, appid, appSecret);
				
				JSONObject userInfo=Utils.getUserInfo(accToken, openid);
				System.out.println(userInfo);
				if(userInfo==null||userInfo.toString().contains("errmsg")){
					return ac;
				}
				String gender="0";
				if(userInfo.toString().contains("sex")){
					gender=userInfo.getString("sex");
				}
				String updateMemberSQL="UPDATE ws_member SET "
						+ "city='"+userInfo.getString("city")+"',"
						+ "gender='"+gender+"',"
						+ "country='"+userInfo.getString("country")+"',"
						+ "provice='"+userInfo.getString("province")+"',"
						+ "language='"+userInfo.getString("language")+"',"
						+ "headimgurl='"+userInfo.getString("headimgurl")+"',"
						+ "subscribe_time='"+userInfo.getString("subscribe_time")+"' "
						+ "WHERE openid='"+openid+"' ";
				db.execute(updateMemberSQL);
			} catch (WeChatInfaceException e) {
				e.printStackTrace();
			}
		}

		return ac;
	}
}
