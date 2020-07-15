package com.wd.menu.action;

import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.wd.tools.Hmac;

/**
 * 验证授权表单在保存时：授权码必须与加密后的IEMI相等
 * 
 * @author 马锐利
 * 
 */
public class AuthorizationSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		ActionResult ar = ac.getActionResult();
		Table ABDP_TERMINALMANAGE = ac.getTable("ABDP_TERMINALMANAGE");
		TableRow tr = ABDP_TERMINALMANAGE.getRows().get(0);
		String authorizecode = tr.getValue("authorizecode");
		String IMEI = tr.getValue("imei");
		String imei = Hmac.encryptHMAC(IMEI);
		if (authorizecode.equals(imei)) {
			super.doAction(db, ac);
		} else {
			ar.setSuccessful(false);
			ar.addErrorMessage("授权码无效！");
		}
	}
}
