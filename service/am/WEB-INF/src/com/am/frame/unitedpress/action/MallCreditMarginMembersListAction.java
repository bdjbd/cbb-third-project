package com.am.frame.unitedpress.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：wz
 * @date 创建时间：2016年4月27日 下午12:11:52
 * @version 信用保证金选人按钮操作
 */
public class MallCreditMarginMembersListAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 通过审核
		String id = ac.getRequestParameter("mall_credit_margin_manager.form.id");
		
		String member_type = ac.getRequestParameter("mall_credit_margin_manager.form.member_type");
		
	}
	
	
}
