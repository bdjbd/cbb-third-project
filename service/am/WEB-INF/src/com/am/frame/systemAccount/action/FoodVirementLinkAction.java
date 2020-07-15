package com.am.frame.systemAccount.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * 食品安全追溯转账链接操作action
 * @author xc
 * 2016年12月6日17:04:57
 *
 */
public class FoodVirementLinkAction extends DefaultAction
{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		business.setOut_account_code(SystemAccountClass.GROUP_FOOD_SAFETY_TRACING_ACCOUNT);
		business.setIn_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		
		business.setOut_group_account_code(SystemAccountClass.GROUP_FOOD_SAFETY_TRACING_ACCOUNT);
		business.setIn_group_account_code(SystemAccountClass.CASH_ACCOUNT);
		business.formatBusiness(ac);
		ac.getActionResult().setUrl("/am_bdp/mall_account_virement.do?autoback=/am_bdp/mall_account_total_saftyfood_info.do&m=a");
		
		super.doAction(db, ac);
	}

}
