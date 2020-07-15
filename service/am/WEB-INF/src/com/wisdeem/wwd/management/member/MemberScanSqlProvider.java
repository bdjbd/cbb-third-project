package com.wisdeem.wwd.management.member;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;

public class MemberScanSqlProvider  implements SqlProvider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getSql(ActionContext ac) {
		String sql = "";
		try {
			DB db = DBFactory.getDB();
			String orgid = ac.getVisitor().getUser().getOrgId();
			String querySQL = "select * from WS_PUBLIC_ACCOUNTS where orgid='"
					+ orgid + "'";
			MapList listr = db.query(querySQL);
			// account_belong=0为私有公众帐号；account_belong=1为公有公众帐号
			String account_belong = listr.getRow(0).get("account_belong");
			String member_code = ac.getRequestParameter("ws_member1.form.member_code");
			if ("0".equals(account_belong)) {// 私有公众帐号
				sql = "select group_id as group_id,*,provice||city AS area from  WS_MEMBER where orgid = '" + orgid + "' and member_code="+member_code+"";
			} else if ("1".equals(account_belong)) {// 公有公众帐号
				sql = "select a.memgroup_id as group_id,*,provice||city AS area from WS_MBDEATIL a left join WS_MEMBER b "
					+ "on a.member_code=b.member_code where a.care_orgid = '" + orgid + "' and b.member_code="+member_code+"";
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return sql;
	}

}
