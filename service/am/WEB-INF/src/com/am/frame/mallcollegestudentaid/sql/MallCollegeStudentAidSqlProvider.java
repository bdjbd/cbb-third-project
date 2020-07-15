package com.am.frame.mallcollegestudentaid.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午3:54:45
 * @version 
 */
public class MallCollegeStudentAidSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		//获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "mall_college_student_aid.query");
		
		String memberid = null;
		String status = null;
		if(queryRow != null){
			memberid = queryRow.get("member_ids");
			status = queryRow.get("statuss");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT to_char(COALESCE(amount_of_subsidy/100.0),'99999999999990D99') AS money,am.membername AS member_id,");
		sql.append(" mcs.* FROM mall_college_student_aid AS mcs ");
		sql.append(" LEFT JOIN am_member AS am ON am.id = mcs.member_id WHERE mcs.status!=1 ");
		
		if(!Checker.isEmpty(memberid)){
			sql.append(" AND am.membername LIKE '%"+memberid+"%' ");
		}
		
		if(!Checker.isEmpty(status)){
			sql.append(" AND mcs.status = "+status+" ");
		}
		
		sql.append(" ORDER BY mcs.create_time DESC ");
		return sql.toString();
	}
		
}
