package com.am.cro.orderallot;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/**
 * 订单分配查询sql
 * @author guorenjie
 *
 */
public class OrderAllotSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		DBManager db = new DBManager();		
		// 1,获取查询单元
		Row queryRow = FastUnit.getQueryRow(ac, "am_bdp",
				"cro_carrepairorder.query");
		String orgid = ac.getVisitor().getUser().getOrgId();
		StringBuilder qsb = new StringBuilder();
		qsb.append("select * from CRO_CARREPAIRORDER cro,am_member am  WHERE cro.memberid = am.id and cro.orderstate = '0' ");
		
		qsb.append("and cro.ORGCODE like '"+orgid+"%'");
		
		if (queryRow != null) {
			//机构查询
			if (!Checker.isEmpty(queryRow.get("orgcode1"))) 
			{
				qsb.append("and cro.orgcode like '"+queryRow.get("orgcode1")+"%'");
			}
			//会员id
			if (!Checker.isEmpty(queryRow.get("memberid1"))) {
				qsb.append("and cro.memberid in (select id from am_member where membername like '%"+queryRow.get("memberid1")+"%')");
			}
		}
		qsb.append("order by createdate desc");
		return qsb.toString();
	}

}
