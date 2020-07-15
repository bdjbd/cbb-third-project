package com.am.badge;

import org.json.JSONObject;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

public class BadgeTFillAjaxAction extends AjaxAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String id=ac.getRequestParameter("tempId");
		
		DB db=DBFactory.getDB();
		
		String sql="SELECT * FROM mall_BadgeTemplate WHERE id='"+id+"'";
		
		MapList map=db.query(sql);
		if(Checker.isEmpty(map))return ac;
		//enterpTaskparameId     enterpNameId
		
		Ajax ajax=new Ajax(ac);
		
		String name=map.getRow(0).get("badgename");
		String badgeParame=map.getRow(0).get("badgeparame");
		String entBadgeCode=map.getRow(0).get("badgecode");
		
		JSONObject obj=new JSONObject();
		obj.put("NAME", name);
		obj.put("BADGEPARAME", badgeParame);
		obj.put("ENT_BADGE_CODE", entBadgeCode);
		
		
		ajax.addScript("changeBadgeValue("+obj.toString()+")");
		
		ajax.send();
		
		return ac;
	}
}
