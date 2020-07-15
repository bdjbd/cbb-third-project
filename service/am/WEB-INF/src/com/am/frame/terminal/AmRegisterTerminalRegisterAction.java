package com.am.frame.terminal;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;


/**
 * 终端激活Action
 * @author yuebin
 *
 */
public class AmRegisterTerminalRegisterAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		super.doAction(db, ac);
		
		//设备授权ID
		String trOrgId=ac.getRequestParameter("am_register_terminal.form.orgid");
		//是否授权
		String isAuthor=ac.getRequestParameter("am_register_terminal.form.is_author");
		
		
		if("1".equals(isAuthor)){
			//授权设备
			String updateSQL="UPDATE am_terminal_order_manager SET order_status=5 WHERE org_id=? ";
			db.execute(updateSQL,trOrgId,Type.VARCHAR);
		}
	}
	
}

