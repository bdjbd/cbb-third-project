package com.am.techapply.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月3日 下午5:45:34
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class TechApplyListSqlProvider implements SqlProvider{
	
	@Override
	public String getSql(ActionContext ac) {
		// 获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp", "mall_tech_apply.query");
		
		String membername = null;
		String applystatus = null;
		String memberphone = null;
		
		if(queryRow != null){
			membername = queryRow.get("member_name");
			applystatus = queryRow.get("apply_status");
			memberphone = queryRow.get("member_phone");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT mta.id,mta.membername AS member_name,mta.is_auth,mta.membersex AS member_sex, ");
		sql.append(" mta.phone AS member_phone,mta.email AS member_email ");
		sql.append(" FROM am_member AS mta ");
		sql.append(" LEFT JOIN am_member_identity AS ame ON ame.id = mta.member_identity ");
		sql.append(" WHERE 1=1 AND UPPER(ame.id_code) = 'AM_TECHNOLOGIST' ");
		
		if(!Checker.isEmpty(membername)){
			sql.append(" AND UPPER(mta.membername) LIKE UPPER('%"+membername+"%') ");
		}
		
		if(!Checker.isEmpty(applystatus)){
			sql.append(" AND mta.status = "+applystatus+" ");
		}
		
		if(!Checker.isEmpty(memberphone)){
			sql.append(" AND UPPER(mta.phone) LIKE UPPER('%"+memberphone+"%') ");
		}

		sql.append(" ORDER BY mta.create_time DESC,mta.is_auth ASC ");
		
		
		return sql.toString();
	}

}
