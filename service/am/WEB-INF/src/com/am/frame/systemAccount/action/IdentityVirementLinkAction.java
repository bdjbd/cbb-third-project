package com.am.frame.systemAccount.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * 身份股金账户转账链接操作action
 * @author 谢超
 * 创建时间：2016年11月7日16:07:44
 */
public class IdentityVirementLinkAction extends DefaultAction
{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		business.setOut_account_code(SystemAccountClass.GROUP_IDENTITY_STOCK_ACCOUNT);
		business.setIn_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		
		business.formatBusiness(ac);
		ac.getActionResult().setUrl("/am_bdp/mall_account_virement.form.do?autoback=/am_bdp/mall_account_share_info.do&m=a");
		
		super.doAction(db, ac);
	}

}
