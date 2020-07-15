package com.fastunit.demo.unit;

import com.fastunit.FastUnit;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.QueryParameters;
import com.fastunit.support.UnitInterceptor;

public class MultitableQueriesUI implements UnitInterceptor {

	private static final String QUERY_UNIT_ID = "query.multitable2.query";
	private static final String LIST_UNIT_ID = "query.multitable2.list";

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String sql = "select t1.cdid,t1.cdname,t1.artist,t2.id,t2.musicname from CD t1,MUSIC t2 where t1.cdid=t2.cdid";
		DB db = DBFactory.getDB("demo");
		// 设置CD表查询条件
		sql = FastUnit.addWhere(ac, db, sql, "demo", QUERY_UNIT_ID, "CD", "t1");
		// 设置MUSIC表查询条件
		sql = FastUnit.addWhere(ac, db, sql, "demo", QUERY_UNIT_ID, "MUSIC", "t2");
		// 设置排序字段
		sql = FastUnit.addOrderBy(ac, sql, "demo", QUERY_UNIT_ID);
		// 获得分页参数
		QueryParameters qp = FastUnit.getQueryParameters(ac, db, sql, "demo",
				LIST_UNIT_ID);
		unit.setData(db.query(sql, qp));
		unit
				.setContent("<br>参见单元“"
						+ LIST_UNIT_ID
						+ "”、UI“com.fastunit.demo.unit.MultitableQueriesUI”，自动设置单元的条件，自动设置分页参数。<br><br>最终执行的sql语句：<br><div style='border:1px solid #dddddd;padding:5px;'>"
						+ sql + "</div>");
		return unit.write(ac);
	}

}
