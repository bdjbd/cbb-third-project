package com.am.agriculturalScience.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月26日
 *@version
 *说明：报名管理SqlProvider
 */
public class SignUpSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		StringBuilder querSQL = new StringBuilder();

		querSQL.append("SELECT mem.membername,mp.*  ");
		querSQL.append(" FROM mall_players  AS mp ");
		querSQL.append(" LEFT JOIN am_member AS mem ON mp.member_id=mem.id ");
		querSQL.append(" WHERE 1=1 ");
		
		
		// 报名人
		String membernameq = null;
		// 报名时间
		String create_timeq = null;
		String create_timeq_t = null;
		// 发布内容ID
		String pc_id = ac.getRequestParameter("id");
		
		if(!Checker.isEmpty(pc_id)){
			ac.setSessionAttribute("mall_playerss.list.pc_id", pc_id);
		}else{
			pc_id=ac.getSessionAttribute("mall_playerss.list.pc_id", null);
		}
		
		
		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_players.query");
		if (queryRow != null) {
			// 报名人
			membernameq = queryRow.get("membernameq");
		}
		if (queryRow != null) {
			// 报名时间前
			create_timeq = queryRow.get("create_timeq");
		}
		if (queryRow != null) {
			// 报名时间后
			create_timeq_t = queryRow.get("create_timeq.t");
		}
		// 3,如果查询单元有数据，将数据拼接到where中
		if (!Checker.isEmpty(membernameq)) {
			querSQL.append(" AND mem.membername  LIKE  '%" + membernameq + "%' ");
		}
		if (!Checker.isEmpty(create_timeq)) {
			querSQL.append("  AND mp.create_time >='"+create_timeq+"' ");
		}
		if(!Checker.isEmpty(create_timeq_t)){
			querSQL.append("  AND mp.create_time <='"+create_timeq_t+"' ");
		}
		

		querSQL.append(" AND mp.pc_id='"+pc_id+"' ");
		querSQL.append(" ORDER BY mp.create_time DESC ");
		
		return querSQL.toString();
		
	}

}
