package com.wd.terminalmanage;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
import com.wd.database.DataBaseFactory;

/**
 * 终端管理查询
 * 
 * @author lu
 * 
 */
@SuppressWarnings("serial")
public class TerminalSqlPrivide implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// TODO Auto-generated method stub
		// 获得页面上的查询条件
		Row r = FastUnit.getQueryRow(ac, "app", "abdp_terminalmanage.query");
		String imei = "";
		String staffid = "";

		if (r != null) {
			imei = r.get("imei");
			staffid = r.get("staffid");

		}

		String sqlwhere = " ";
		// 拼接sql 条件语句

		if (!Checker.isEmpty(imei) && !"".equals(imei)) {
			sqlwhere = sqlwhere + " and a.imei like '%" + imei + "%'";
		}
		if (!Checker.isEmpty(staffid) && !"".equals(staffid)) {
			sqlwhere = sqlwhere + " and a.staffid like '%" + staffid + "%'";
		}

		// 查询sql语句
		String sql = "";
		sql = sql
				+ "select a.*,(case when ("
				+ DataBaseFactory.getDataBase().getDateTimeDifferenceStr(
						"a.heartbeat")
				+ ")*1000 < "
				+ "(2*(select  "
				+ DataBaseFactory.getDataBase().getTo_NumberStr("vvalue",
						"999999")
				+ " vvalue  from avar where vid ='GPS')) then '在线' else '离线'  end) as status "
				+ " from abdp_terminalmanage a where 1=1 " + sqlwhere
				+ " order by heartbeat desc";
		return sql;
	}

}
