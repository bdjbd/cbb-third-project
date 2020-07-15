package com.wisdeem.wwd.news;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;
import com.wisdeem.wwd.WeChat.server.WeChatInfaceServer;

/**
 * 同步企业信息到微信
 * @author Administrator
 *
 */
public class SyncNewsMenuAjaxAction extends AjaxAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String orgid=ac.getVisitor().getUser().getOrgId();
		String getToken="SELECT token,app_id,app_secret,is_valid FROM ws_public_accounts  WHERE orgid='"+orgid+"'  AND token IS NOT NULL  AND app_id IS NOT NULL AND app_secret IS NOT NULL ";
		
		DB db=DBFactory.getDB();
		
		MapList map=db.query(getToken);
		
		if(Checker.isEmpty(map))return ac;
		
		Row row=map.getRow(0);
		
		try {
		
			String acctoken=Utils.getAccessToken(row.get("token"),row.get("app_id"),row.get("app_secret"));
			
			boolean isAuthor=row.getInt(3,0)==1?true:false;
			
			WeChatInfaceServer.getInstance();
			WeChatInfaceServer.setMenuByToken(acctoken, row.get("token"), row.get("app_id"), isAuthor);
			
		} catch (WeChatInfaceException e) {
			e.printStackTrace();
		}
		return ac;
	}
}
