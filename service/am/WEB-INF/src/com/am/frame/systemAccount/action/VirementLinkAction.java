package com.am.frame.systemAccount.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * 现金转账链接操作action
 * @author mac
 *
 */
public class VirementLinkAction extends DefaultAction
{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		business.setOut_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		business.setIn_account_code(SystemAccountClass.GROUP_CREDIT_MARGIN_ACCOUNT+","+SystemAccountClass.GROUP_IDENTITY_STOCK_ACCOUNT);
		
		business.setOut_group_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		business.setIn_group_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT+","+SystemAccountClass.CASH_ACCOUNT);
		
		business.setPerson_remaker("现金账户转账操作");
		business.formatBusiness(ac);
		ac.getActionResult().setUrl("/am_bdp/mall_account_virement.do?autoback=/am_bdp/mall_account_cash_info.do&m=a");
		
		super.doAction(db, ac);
	}

}
