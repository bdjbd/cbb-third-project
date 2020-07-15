package com.am.borrowing.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：xiechao
 * @date 创建时间：2016年12月22日09:22:52
 * @version 精准扶贫sql
 */
public class FundfarmersSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String orgid = ac.getVisitor().getUser().getOrgId();
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_college_student_aid_farmer.query");
		String membername = null;
		String phone = null;
		if(queryRow != null){
			membername = queryRow.get("member_name");
			phone = queryRow.get("statuss");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  apj.stock_price,mem.id AS mid,mem.membername,mem.identitycardnumber,mem.phone,mem.memberaddress,  ");
		sql.append(" CASE hi.help_type WHEN 1 THEN '帮赠' WHEN 2 THEN '帮借' END AS type,  ");
		sql.append(" CASE hi.repayment_status WHEN 1 THEN '无需还款' WHEN 2 THEN '未还款' WHEN 3 THEN '已还款' END AS repaystatus,  ");
		sql.append(" COALESCE((hi.help_amount/100),0) AS money,to_char(hi.create_time,'yyyy-mm-dd') AS createtime,hi.*  ");
		sql.append(" FROM mall_help_info AS hi  ");
		sql.append(" LEFT JOIN am_member AS mem ON hi.be_helped_id=mem.id  ");
		sql.append(" LEFT JOIN mall_agriculture_projects AS apj ON apj.org_id=mem.orgcode  ");
		sql.append(" WHERE 1=1 AND hi.help_id = '"+orgid+"' ");
		
		if(!Checker.isEmpty(membername)){
			sql.append(" AND UPPER(mem.membername) LIKE UPPER('%"+membername+"%') ");
		}
		
		if(!Checker.isEmpty(phone)){
			sql.append(" AND UPPER(mem.phone) LIKE UPPER('%"+phone+"%') ");
		}
		
		sql.append(" ORDER BY hi.create_time DESC ");
		return sql.toString();
	}

}
