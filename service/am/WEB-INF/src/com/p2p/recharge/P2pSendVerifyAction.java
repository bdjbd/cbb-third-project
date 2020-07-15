package com.p2p.recharge;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class P2pSendVerifyAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//
		String rmid=ac.getRequestParameter("rmid");
		String sql="UPDATE p2p_rechargemanage  SET verifystatus=1 WHERE rmid='"+rmid+"'";
		db.execute(sql);
		Ajax ajax=new Ajax(ac);
		ajax.addScript("window.location.reload();");
		ajax.send();
	}
}
