package com.am.food_safety.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016.06.14
 *@version
 *说明：冷柜申请表单Action
 */
public class FreezerApplyFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("mall_freezer_apply.form.id");
		String remarks = ac.getRequestParameter("mall_freezer_apply.form.remarks");
		String paramStatus = ac.getActionParameter();
		String sql = "";
		if(Checker.isEmpty(remarks)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("审核说明不能为空");
		}else{
			if("1".equals(paramStatus)){
				//审核通过
				sql = " UPDATE mall_freezer_apply SET status = 2,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			}else if("2".equals(paramStatus)){
				//审核驳回
				sql = " UPDATE mall_freezer_apply SET status = 3,remarks = '"+remarks+"' WHERE id = '"+id+"' ";
			}
			db.execute(sql);
		}
		
	}
	
}