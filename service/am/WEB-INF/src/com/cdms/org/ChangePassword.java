package com.cdms.org;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.sdk.md5.MD5Util;
import com.fastunit.Var;
import com.fastunit.adm.org.SaveOrgAction;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;

/**
 * 重置密码
* @author guorenjie  
* @date 2018年5月8日
 */
public class ChangePassword extends SaveOrgAction {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String userid = ac.getRequestParameter("userid");
		String password = Var.get("password");
		password = MD5Util.getSingleton().textToMD5L32(password);
		String sql = "update auser set password='"+password+"' where userid='"+userid+"'";
		db.execute(sql);		

	}
}
