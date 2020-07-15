package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 作者：xiechao
 *@create 时间：2016年12月19日18:51:40
 *@version 说明：提现提交Action
 */
public class WithdrawalsAuditAction  extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("with_drawals.form.id");
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE withdrawals SET audit_state = 1 ,mention_time= now() WHERE id = '"+id+"'");
		db.execute(sql.toString());
	}
}
