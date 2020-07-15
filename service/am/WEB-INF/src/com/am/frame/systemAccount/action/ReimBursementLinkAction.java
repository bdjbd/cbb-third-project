package com.am.frame.systemAccount.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 还款转账链接操作action
 * @author 谢超
 * 创建时间：2016年11月24日21:49:10
 */
public class ReimBursementLinkAction extends DefaultAction
{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		String max_long = "";
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		String max_message = "您所输入的金额已经超出您的待还款金额，请您确定后操作！";
		
		if(!Checker.isEmpty(orgid)){
			
			String max_longSql = "SELECT mai.loan_amount,mai.repayment_amount,(mai.loan_amount-mai.repayment_amount)/100 as waitmoney  "
					+ " FROM mall_account_info as mai "
					+ " LEFT JOIN mall_system_account_class as msac on msac.id = mai.a_class_id "
					+ " WHERE mai.member_orgid_id= '"+orgid+"' and msac.sa_code='GROUP_LOAN_ACCOUNT'";
			
			MapList maxlongSql = db.query(max_longSql);
			if(maxlongSql.size()>0&&!Checker.isEmpty(maxlongSql.getRow(0).get("waitmoney"))){
				
				max_long = maxlongSql.getRow(0).get("waitmoney");			
			}
	
		}

		business.setMax_long(max_long);
		business.setMax_message(max_message);
		business.setIn_member_id(orgid);
		business.setSuccess_call_back("com.am.app_plugins.CashDividends.ReimBursement");

		business.setOut_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		business.setIn_account_code(SystemAccountClass.GROUP_LOAN_ACCOUNT);
		
		business.formatBusiness(ac);
		ac.getActionResult().setUrl("/am_bdp/mall_account_virement.form.do?autoback=/am_bdp/mall_account_business_dividend.do&m=a");
		
		super.doAction(db, ac);
	}

}