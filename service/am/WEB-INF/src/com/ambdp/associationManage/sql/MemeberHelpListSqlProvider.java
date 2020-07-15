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
 *说明：单位帮扶信息列表SqlProvider
 */
public class MemeberHelpListSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		StringBuilder querSQL = new StringBuilder();

		querSQL.append("SELECT beorg.membername,trim(to_char(COALESCE(mai.help_amount/100.0,0),'99999999999990D99')) as money,mai.*  ");
		querSQL.append(" FROM mall_help_info AS mai   ");
		querSQL.append(" LEFT JOIN am_member AS beorg ON mai.help_id=beorg.id   ");
		querSQL.append(" WHERE mai.help_role_type=1  ");
		
		// 联合社名称
		String help_idq = null;

		String type = null;
		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp","member_mall_help_info.query");

		if (queryRow != null) {
			// 帮扶者
			help_idq = queryRow.get("help_id");
			type  = queryRow.get("help_type");
		}
		
		
	
		// 3,如果查询单元有数据，将数据拼接到where中
//		if (!Checker.isEmpty(zonename)) {
//			querSQL.append(" AND zone.zonename  LIKE  '%" + zonename + "%' ");
//		}
		if (!Checker.isEmpty(help_idq)) {
			querSQL.append(" AND  beorg.membername LIKE '%" + help_idq + "%' ");
		}
//		if (!Checker.isEmpty(be_helped_idq)) {
//			querSQL.append("  AND beorg.membername LIKE '%" + be_helped_idq + "%' ");
//		}
		if (!Checker.isEmpty(type)) {
			querSQL.append(" AND  mai.help_type = '"+type+"'");
		}
		querSQL.append(" ORDER BY mai.create_time DESC ");
		return querSQL.toString();
	}

}
