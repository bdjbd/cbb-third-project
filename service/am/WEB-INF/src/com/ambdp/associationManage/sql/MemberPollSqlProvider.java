package com.ambdp.associationManage.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月15日
 *@version
 *说明：社员代表大会列表SqlProvider
 */
public class MemberPollSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		StringBuilder querSQL = new StringBuilder();

		querSQL.append("SELECT to_char(push_time,'yyyy-mm-dd')AS time,* ");
		querSQL.append(" FROM mall_member_poll ");
		querSQL.append(" WHERE 1=1 ");
		
		
		// 议题标题
		String titleq = null;
		// 状态
		String statusq = null;
		// 发布时间
		String push_timeq = null;
		String push_timeq_t = null;

		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_member_poll.query");

		if (queryRow != null) {
			// 议题标题
			titleq = queryRow.get("titleq");
		}
		if (queryRow != null) {
			// 状态
			statusq = queryRow.get("statusq");
		}
		if (queryRow != null) {
			// 发布时间前
			push_timeq = queryRow.get("push_timeq");
		}
		if (queryRow != null) {
			// 发布时间后
			push_timeq_t = queryRow.get("push_timeq.t");
		}
		// 3,如果查询单元有数据，将数据拼接到where中
		if (!Checker.isEmpty(titleq)) {
			querSQL.append(" AND title  LIKE  '%" + titleq + "%' ");
		}
		if (!Checker.isEmpty(statusq)) {
			querSQL.append(" AND status  =  '" + statusq + "' ");
		}
		if (!Checker.isEmpty(push_timeq)) {
			querSQL.append("  AND push_time >='"+push_timeq+"' ");
		}
		if(!Checker.isEmpty(push_timeq_t)){
			querSQL.append("  AND push_time <='"+push_timeq_t+"' ");
		}
	

	
		querSQL.append(" ORDER BY create_time DESC ");
		
		return querSQL.toString();
	}

}
