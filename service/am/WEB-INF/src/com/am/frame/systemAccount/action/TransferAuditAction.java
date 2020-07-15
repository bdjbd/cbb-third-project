package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月26日 下午12:04:49
 *@version 说明：转账提交Action
 */
public class TransferAuditAction  extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("mall_transfer.form.id");
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE mall_transfer SET audit_state = 1 ,mention_time= now() WHERE id = '"+id+"'");
		db.execute(sql.toString());
	}
}
