package com.am.organization.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 后台组织机构强制踢出
 *
 */
public class ConstraintOutAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("lxny_organizational_relationshi");
		//获取加入组织id
//		String be_added_to_the_body_id=ac.getRequestParameter("lxny_organizational_relationshi.form.be_added_to_the_body_id");
		//获取状态
		String status = ac.getRequestParameter("lxny_organizational_relationshi.form.ststus");
		//获取当前机构id
//		String joining_mechanism_id = ac.getRequestParameter("lxny_organizational_relationshi.form.joining_mechanism_idsss");
		//获取申请说明
//		String add_description = ac.getRequestParameter("lxny_organizational_relationshi.form.add_description");
		
		
		//获取退出说明
		String exit_description = ac.getRequestParameter("lxny_organizational_relationshi.form.exit_description");
		//id
		String id = ac.getRequestParameter("lxny_organizational_relationshi.form.id");
		
		//判断退出状态
		String updateStatusSQL=null;
	
				
		if(!Checker.isEmpty(exit_description)){
			updateStatusSQL="update lxny_organizational_relationshi SET status ='30' where id='"+id+"'";
			db.save(table);
			db.execute(updateStatusSQL);
		}
	}
}
