package com.cdms;

import org.apache.log4j.Logger;

import com.am.sdk.md5.MD5Util;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * @author 刘扬
 *人员管理  重置密码功能
 */
public class UpdatePassword extends DefaultAction{
	Logger log = Logger.getLogger(UpdatePassword.class);
	public void doAction(DB db, ActionContext ac) throws Exception {
		 //Table table = ac.getTable("am_memberinformation");
		//获取人员ID
		String id=ac.getRequestParameter("id");
		//设置默认密码
		String password="000000";
		String str=MD5Util.getSingleton().textToMD5L32(password);
		log.info("-------------------------------------------------------------------");
		log.info(id);
		log.info(str);
		
		//更新密码SQL
		String sql="update AM_MEMBER set loginpassword='"+str+"' where id='"+id+"'";
		log.info(sql);
		db.execute(sql);
	}
}
