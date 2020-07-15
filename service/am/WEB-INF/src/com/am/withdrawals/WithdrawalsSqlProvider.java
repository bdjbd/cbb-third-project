package com.am.withdrawals;

import com.am.frame.systemAccount.SystemAccountClass;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月11日 上午9:37:46
 * @version 提现管理sqlprovider
 */
public class WithdrawalsSqlProvider implements SqlProvider{
	
	private static final long serialVersionUID = 7294707103906812733L;

	@Override
	public String getSql(ActionContext ac) {
						
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT d1.membername AS member_name,meb.bank_code AS in_account_id,msac.sa_name AS out_account_id, ");
		sql.append(" to_char(ws.payment_time,'YYYY-MM-dd hh24:mi:ss') AS payment_time,  ");
		sql.append("  to_char(ws.mention_time,'YYYY-MM-dd hh24:mi:ss') AS mention_time,  ");
		sql.append(" to_char(ws.odd_numbers,'YYYY-MM-dd hh24:mi:ss') AS odd_numbers, ");
		sql.append(" meb.account_type AS in_account_type, ");//WHEN '1' THEN '银行卡' WHEN '2' THEN '支付宝' WHEN '3' THEN '微信' END 
		sql.append(" trim(to_char(COALESCE(ws.cash_withdrawal/100.0,0),'99999999999990D99')) AS cash_withdrawals,ws.* ");
		sql.append(" FROM withdrawals  AS ws  ");
		sql.append(" LEFT JOIN mall_account_info AS mai ON ws.out_account_id=mai.id ");
		sql.append(" LEFT JOIN mall_member_bank AS meb ON meb.member_orgid_id=ws.member_id AND meb.id=ws.in_account_id ");
		sql.append(" LEFT JOIN (SELECT id ,membername FROM am_member  UNION SELECT orgid AS id,orgname AS membername FROM aorg) d1 ON d1.id=ws.member_id ");
		sql.append(" LEFT JOIN mall_system_account_class AS msac ON mai.a_class_id=msac.id  ");
		sql.append(" WHERE 1=1 ");
		
		String status = ac.getRequestParameter("am_bdp.withdrawals.type");
		
		if("1".equals(status)){
			sql.append("and msac.sa_code = '"+SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT+"'");
		}else{
			sql.append("and msac.sa_code <> '"+SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT+"'");
		}
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","withdrawals.query");
		String accounttype = null;
		String in_account_id = null;
		String member_id = null;
		String out_account_id = null;
		
		
		//获取查询数据
		if(queryRow != null){
			member_id = queryRow.get("member_ids");
			accounttype = queryRow.get("account_types");
			in_account_id = queryRow.get("in_account_ids");
			out_account_id = queryRow.get("out_account_ids");
		}
		
		
		if(!Checker.isEmpty(accounttype)){
			sql.append(" AND meb.account_type = '"+accounttype+"' ");
		}
		if(!Checker.isEmpty(member_id)){
			sql.append(" AND d1.membername LIKE '%"+member_id+"%' ");
		}
		if(!Checker.isEmpty(in_account_id)){
			sql.append(" AND meb.bank_code LIKE '%"+in_account_id+"%' ");
		}
		if(!Checker.isEmpty(out_account_id)){
			sql.append(" AND msac.sa_name LIKE '%"+out_account_id+"%' ");
		}
			
		String currentOrgCode=ac.getVisitor().getUser().getOrgId();
		//帐号类型    1：社员帐号   2：机构帐号
		if(!"org".equalsIgnoreCase(currentOrgCode)){
			sql.append(" AND ws.account_type=2 AND  ws.member_id LIKE '"+currentOrgCode+"' ");
		}
		
		
		sql.append(" order by ws.mention_time desc ");
		
		
		return sql.toString();
	}

}
