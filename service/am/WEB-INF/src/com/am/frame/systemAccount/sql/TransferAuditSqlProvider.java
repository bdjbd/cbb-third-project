package com.am.frame.systemAccount.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author yangdong
 *@create 2016年4月28日
 *@version
 *说明：提现管理流程SqlProvider
 */
public class TransferAuditSqlProvider  implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_transfer.query");
		
		String member_id = "";
		String out_account_id = "";
		String in_account_id = "";
		
		if (queryRow != null) {
			member_id = queryRow.get("member_ids");
			out_account_id = queryRow.get("out_account_ids");
			in_account_id = queryRow.get("in_account_ids");
		}
		
		StringBuilder querSql=new StringBuilder();
		querSql.append(" select trim(to_char(COALESCE(mt.cash_withdrawal/100.0,0),'99999999999990D99')) AS  ");
		querSql.append(" cash_withdrawal, ");
		querSql.append(" msac.sa_name as out_account_id,msa.sa_name as in_account_id, ");
		querSql.append(" to_char(mt.mention_time,'YYYY-MM-dd hh24:mi:ss') AS createtime, ");
		querSql.append(" mt.* from MALL_TRANSFER AS mt ");
		querSql.append(" left join mall_account_info as mai on mai.id = mt.out_account_id ");
		querSql.append(" left join mall_system_account_class as msac on msac.id = mai.a_class_id ");
		querSql.append(" left join mall_account_info as mbi on mbi.id =mt.in_account_id ");
		querSql.append(" left join mall_system_account_class as msa on msa.id = mbi.a_class_id ");
		querSql.append(" LEFT JOIN aorg AS au ON au.orgid = '$U{orgid}' where mai.member_orgid_id = '$U{orgid}' ");
		
		if(!Checker.isEmpty(member_id)){
			querSql.append(" AND au.orgname LIKE '"+member_id+"' ");
		}
		if(!Checker.isEmpty(out_account_id)){
			querSql.append(" AND msac.sa_name LIKE '"+out_account_id+"' ");
		}
		if(!Checker.isEmpty(in_account_id)){
			querSql.append(" AND msa.sa_name  LIKE '"+in_account_id+"' ");
		}
		querSql.append(" ORDER BY mt.mention_time DESC");
		return querSql.toString();
	}
}
