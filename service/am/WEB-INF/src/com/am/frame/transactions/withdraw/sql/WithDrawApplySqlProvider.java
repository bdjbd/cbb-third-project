package com.am.frame.transactions.withdraw.sql;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wz
 *@create 2016年4月28日
 *@version
 *说明：提现管理流程SqlProvider
 */
public class WithDrawApplySqlProvider  implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder querSql=new StringBuilder();
		
		String flowid=ac.getRequestParameter("flowinstid");
		
		
		querSql.append("select DISTINCT wd.id,wd.remarks,wd.in_account_id as in_account_ids,wd.audit_person,"
				+ " wd.audit_opinion,wd.main_audit_opinion,wd.second_director,wd.director,wd.audit_result,"
				+" wd.account_type,wd.no_reasons,wd.counter_fee,wd.batch_number "
				+ ",msac.sa_code as out_account_ids,msac.sa_code as code"
				+ ",trim(to_char(COALESCE(wd.counter_fee/100.0,0),'99999999999990D99')) as counter_feess"
				+ ",trim(to_char(COALESCE(wd.cash_withdrawal/100.0,0),'99999999999990D99')) "
				+ " as cash_withdrawals,meb.account_type AS in_account_type "
				+ " ,wd.cash_withdrawal+wd.counter_fee "
				+ " AS actual_cash_withdrawal,wd.member_id "
				+" from withdrawals as wd "
				+" left join mall_account_info as mai on mai.id = wd.out_account_id "
				+" left join mall_system_account_class as msac on msac.id = mai.a_class_id "
				+" LEFT JOIN mall_member_bank AS meb ON meb.id = wd.in_account_id ");
		
		querSql.append(" WHERE 1=1 ");
		
		if(!Checker.isEmpty(flowid)){
			querSql.append(" AND flowid='"+flowid+"'");
		}else{
			querSql.append(" $SQL[[ and wd.id = '$RS{withdrawals.form.id,am_bdp.withdrawals.form.id}']] ");
		}
		
//		querSql.append(" ORDER BY mp.create_time desc  ");
		return querSql.toString();
	}
}
