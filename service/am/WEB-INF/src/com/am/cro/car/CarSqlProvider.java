package com.am.cro.car;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/**
 * 车辆管理查询sql
 * @author guorenjie
 *
 */
public class CarSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		DBManager db = new DBManager();		
		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp",
				"cro_carmanager.query");
		String orgid = ac.getVisitor().getUser().getOrgId();
		StringBuilder qsb = new StringBuilder();
		qsb.append("select * from cro_carmanager WHERE ORGCODE like '"+orgid+"%'");
		if (queryRow != null) {
			//会员id
			if (!Checker.isEmpty(queryRow.get("memberid1"))) {
				qsb.append("and memberid in (select id from am_member where membername like '%"+queryRow.get("memberid1")+"%')");
			}
		}
		qsb.append("order by createdate desc");
		return qsb.toString();
	}

}
