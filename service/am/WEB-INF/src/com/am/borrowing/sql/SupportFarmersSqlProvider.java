package com.am.borrowing.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：xiechao
 * @date 创建时间：2016年12月21日18:38:48
 * @version 精准帮扶sql
 */
public class SupportFarmersSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_college_student_aid_farmer_match.query");
		
		String account = null;
		if(queryRow != null){
			
			account = queryRow.get("account");
		}
		
		StringBuilder sql = new StringBuilder();
		
		if(!Checker.isEmpty(account)){
			sql.append(" SELECT (SELECT sum(vvalue::integer) FROM avar WHERE vid IN ('help_amount')),row_number() over() AS ind,m.* FROM am_member AS m ");
			sql.append(" LEFT JOIN mall_agriculture_projects AS apj ON m.orgcode=apj.org_id ");
			sql.append(" WHERE m.total_aid_times <1  AND m.member_type='3' ");
			sql.append(" AND apj.org_id IS NOT NULL AND m.orgcode IS NOT NULL ");
			sql.append(" AND m.loginaccount = '"+account+"' ");
			  
		}else{
			
			sql.append(" SELECT (SELECT sum(vvalue::integer) FROM avar WHERE vid IN ('help_amount')),* FROM ( ");
			sql.append(" SELECT row_number() over() AS ind,m.* FROM am_member AS m ");
			sql.append(" LEFT JOIN mall_agriculture_projects AS apj ON m.orgcode=apj.org_id ");
			sql.append(" WHERE m.total_aid_times <1  AND m.member_type='3' ");
			sql.append(" AND apj.org_id IS NOT NULL AND m.orgcode IS NOT NULL ");
			sql.append(" ) D1 WHERE ind=( ");
			sql.append(" SELECT round( ");
			sql.append(" (SELECT random()*( ");
			sql.append(" SELECT count(*) FROM am_member AS m ");
			sql.append(" LEFT JOIN mall_agriculture_projects AS apj ON m.orgcode=apj.org_id ");
			sql.append(" WHERE m.total_aid_times <1  AND m.member_type='3' ");
			sql.append(" AND apj.org_id IS NOT NULL AND m.orgcode IS NOT NULL ");
			sql.append(" ))::numeric,0) ");
			sql.append(" ) ");
			
		} 
						
		return sql.toString();
	}

}