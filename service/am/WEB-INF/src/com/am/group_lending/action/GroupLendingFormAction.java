package com.am.group_lending.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月10日
 *@version
 *说明：联保贷款表单Action
 */
public class GroupLendingFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("mall_loan_appy.form.id");
		String remarks = ac.getRequestParameter("mall_loan_appy.form.review_remarks");
		String paramStatus = ac.getActionParameter();
		String sql = "";
		boolean flag = true;
		if("1".equals(paramStatus)){
			if(Checker.isEmpty(remarks)){
				flag = false;
			}
			//审核通过
			sql = " UPDATE mall_loan_appy SET lp_stauts = 4,review_remarks = '"+remarks+"' WHERE id = '"+id+"' ";
		}else if("2".equals(paramStatus)){
			//审核驳回
			sql = " UPDATE mall_loan_appy SET lp_stauts = 5,review_remarks = '"+remarks+"' WHERE id = '"+id+"' ";
		}
		
		if(flag){
			
			db.execute(sql);
			ac.getActionResult().setSuccessful(true);
		
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("审核说明不能为空");
		}
	}
	
	
}