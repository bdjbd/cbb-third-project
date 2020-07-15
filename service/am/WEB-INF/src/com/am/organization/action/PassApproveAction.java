package com.am.organization.action;


import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 审核通过
 *
 */
public class PassApproveAction extends DefaultAction{
	
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//获取被加入组织id
		String be_added_to_the_body_id=ac.getRequestParameter("lxny_organizational_relationshi.form.be_added_to_the_body_id");
		//获取加入组织id
		String joining_mechanism_id = ac.getRequestParameter("lxny_organizational_relationshi.form.joining_mechanism_idsss");
		//获取申请说明
		String add_description = ac.getRequestParameter("lxny_organizational_relationshi.form.add_description");
		//获取退出说明
		String exit_description = ac.getRequestParameter("lxny_organizational_relationshi.form.exit_description");
		//获取组织机构id
		String orgid=ac.getRequestParameter("lxny_organizational_relationshi.form.orgid");
		//id
		String id = ac.getRequestParameter("lxny_organizational_relationshi.form.id");
		
		if(!Checker.isEmpty(id)){
			//修改审核状态为审核通过，并修改审核说明
			String updateStatusSQL = " UPDATE  lxny_organizational_relationshi SET status = 12,orgid= '"+orgid+"' where id ='"+id+"' ";
			
			

	   }
   }
	
}
