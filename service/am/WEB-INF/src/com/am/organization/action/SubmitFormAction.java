package com.am.organization.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 后台组织机构退出提交
 * 
 */
public class SubmitFormAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("lxny_organizational_relationshi");
		//获取当前组织id
		String joining_mechanism_id=ac.getRequestParameter("lxny_organizational_relationshi_join.form.joining_mechanism_id");
		//获取退出组织id
		String be_added_to_the_body_id=ac.getRequestParameter("lxny_organizational_relationshi_join.form.be_added_to_the_body_id");
		//获取状态
		String status=ac.getRequestParameter("lxny_organizational_relationshi_join.form.status");
		//获取退出原因
		String exit_description = ac.getRequestParameter("lxny_organizational_relationshi_join.form.exit_description");
		String id = ac.getRequestParameter("lxny_organizational_relationshi_join.form.id");
		String updateStatusSQL=null;
		
		
		if(!Checker.isEmpty(exit_description)){
			
			//db.save(table);
			updateStatusSQL="update lxny_organizational_relationshi SET status ='20',exit_description='"+exit_description+"' where id='"+id+"'";
			
			db.execute(updateStatusSQL);
			
		}
	}
}
