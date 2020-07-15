package com.ambdp.associationManage.sql;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月18日
 *@version
 *说明：社员帮扶信息列表SqlProvider
 */
public class HelpInfoListSqlProvider implements SqlProvider{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		StringBuilder querSQL = new StringBuilder();

		querSQL.append("SELECT zone.zonename,mai.help_amount/100 AS money,mai.*  ");
		querSQL.append(" ,get_membername_by_id(mai.be_helped_id) AS be_helped_name ");
		querSQL.append(" ,get_membername_by_id(mai.help_id) AS help_name ");
		querSQL.append(" FROM mall_help_info AS mai ");
		querSQL.append(" LEFT JOIN am_member AS bemem ON mai.be_helped_id=bemem.id ");
		querSQL.append(" LEFT JOIN am_member AS hmem ON mai.help_id=hmem.id ");
		querSQL.append(" LEFT JOIN zone AS zone ON bemem.zzone=zone.zoneid ");
		querSQL.append(" WHERE mai.help_role_type=1 ");
		
		// 区县
		String zonename = null;
		// 帮扶者
		String help_idq = null;
		// 被帮扶者
		String be_helped_idq = null;
		// 帮扶类型
		String help_types = null;

		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","mall_help_info.query");

		if (queryRow != null) {
			// 区县
			zonename = queryRow.get("zonename");
		}
		if (queryRow != null) {
			// 帮扶者
			help_idq = queryRow.get("help_idq");
		}
		if (queryRow != null) {
			// 被帮扶者
			be_helped_idq = queryRow.get("be_helped_idq");
		}
		if (queryRow != null) {
			// 帮扶类型
			help_types = queryRow.get("help_types");
		}
		// 3,如果查询单元有数据，将数据拼接到where中
		if (!Checker.isEmpty(zonename)) {
			querSQL.append(" AND zone.zonename  LIKE  '%" + zonename + "%' ");
		}
		if (!Checker.isEmpty(help_idq)) {
			querSQL.append(" AND  hmem.membername LIKE '%" + help_idq + "%' ");
		}
		if (!Checker.isEmpty(be_helped_idq)) {
			querSQL.append("  AND bemem.membername LIKE '%" + be_helped_idq + "%' ");
		}
		if(!Checker.isEmpty(help_types)){
			querSQL.append("  AND mai.help_type ='"+help_types+"' ");
		}
		querSQL.append(" ORDER BY mai.create_time DESC ");
		return querSQL.toString();
	}

}
