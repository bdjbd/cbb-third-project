package com.am.withdrawals;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：xiechao
 * @date 创建时间：2016年10月27日18:38:15
 * @version 提现管理sqlprovider
 */
public class With_drawalsSqlProvider implements SqlProvider{
	
	private static final long serialVersionUID = 7294707103906812733L;

	@Override
	public String getSql(ActionContext ac) {
						
		StringBuilder sql = new StringBuilder();
		
		
		String org=ac.getVisitor().getUser().getOrgId();
		
		sql.append(" select ws.audit_state,ws.id,ws.member_id,msac.sa_name,ws.cash_withdrawal/100 AS cash_withdrawal, ");
		sql.append(" meb.bank_code,meb.account_type,ws.in_account_id,ws.second_director,ws.director,ws.audit_opinion,ws.main_audit_opinion,ws.settlement_state, ");
		sql.append(" to_char(ws.mention_time,'YYYY-MM-dd hh24:mi:ss') AS mention_time ");
		sql.append(" from withdrawals as ws ");
		sql.append(" LEFT JOIN MALL_MEMBER_BANK AS meb ON meb.id = ws.in_account_id  ");
		sql.append(" LEFT JOIN mall_account_info as mai ON ws.out_account_id = mai.id  ");
		sql.append(" LEFT JOIN mall_system_account_class AS msac ON msac.id = mai.a_class_id  ");
		sql.append(" LEFT JOIN aorg AS au ON au.orgid = ws.member_id  ");
		sql.append(" WHERE 1=1 ");
		sql.append(" and ws.member_id='"+org+"' ");
		
		
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","with_drawals.query");
		String accounttype = null;
		String member_id = null;
		String loginaccount = null;
		
		
		//2，支付宝；3，微信
		accounttype="2";
		
		//获取查询数据
		if(queryRow != null){
			member_id = queryRow.get("member_ids");
			accounttype = queryRow.get("account_types");
			loginaccount =  queryRow.get("loginaccount");
		}
		
		
		if(!Checker.isEmpty(accounttype)){
			sql.append(" AND meb.account_type = '"+accounttype+"' ");
		}
		if(!Checker.isEmpty(member_id)){
			sql.append(" AND au.orgname LIKE '%"+member_id+"%' ");
		}
		if(!Checker.isEmpty(loginaccount)){
			sql.append(" AND meb.bank_code LIKE '%"+loginaccount+"%' ");
		}

		
		sql.append(" order by ws.mention_time desc ");
		
		
		return sql.toString();
	}

}

