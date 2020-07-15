package com.p2p.business;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;

public class DispatcherOrder extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String memberCode=ac.getRequestParameter("member_code");
		String orderCode=ac.getRequestParameter("orderCode");
		String sql="UPDATE p2p_DispatchRecod SET MEMBER_CODE="+memberCode
				+",ORStatus=0 WHERE order_code='"+orderCode+"'";
		DBFactory.getDB().execute(sql);
		DispatcherOrderService.sendOrdMsgToMember(memberCode,orderCode,db);
		ac.getActionResult().setSuccessful(true);
		Ajax ajax=new Ajax(ac);
		ajax.addScript("history.back();");
		ajax.send();
	}

}
