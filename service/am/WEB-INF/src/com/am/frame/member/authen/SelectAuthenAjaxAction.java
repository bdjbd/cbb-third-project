package com.am.frame.member.authen;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月21日
 * @version 
 * 说明:<br />
 * AjaxAction ,会员认证，选择参数
 */
public class SelectAuthenAjaxAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String entBadgeId=ac.getRequestParameter("entbadgeid");
		
		String findParamsSQL="SELECT * FROM mall_EnterpriseBadge WHERE id=?";
		
		MapList map=db.query(findParamsSQL,entBadgeId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			//获取徽章参数
			String params=map.getRow(0).get("badgeparame");
			
			Ajax ajax=new Ajax(ac);
			ajax.addScript("setBadgeParams('"+params+"');");
			ajax.send();
		}
	}
	
}
