package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月25日 下午3:20:37
 *@version 说明：通过审核
 */
public class WidthDrawAuditAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("account_withdrawals.apply.id");
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE withdrawals SET audit_state = 1 WHERE id = '"+id+"'");
		db.execute(sql.toString());
	}
	
}
