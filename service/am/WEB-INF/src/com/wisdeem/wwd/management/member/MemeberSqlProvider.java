package com.wisdeem.wwd.management.member;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class MemeberSqlProvider implements SqlProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql = "";
		try {
			DB db = DBFactory.getDB();
			Row query = FastUnit.getQueryRow(ac, "wwd", "ws_member1.query");
			String orgid = ac.getVisitor().getUser().getOrgId();
			String querySQL = "select *  from WS_PUBLIC_ACCOUNTS where orgid='"
					+ orgid + "'";
			MapList listr = db.query(querySQL);
			if (listr.size() > 0) {

				// account_belong=0为私有公众帐号；account_belong=1为公有公众帐号
				String account_belong = listr.getRow(0).get("account_belong");
				String nickname = "";
				String group_id = "";
				String type="";
				if ("0".equals(account_belong)) {// 私有公众帐号
					sql = "  select openid, headimgurl,member_code as member_code,nickname as nickname,mem_name ||'/'||phone as namephone,gender as gender,age as age," +
							"group_id as groupid,public_id as public_id,data_status as data_status,provice||city AS area,type from WS_MEMBER where orgid= '"+orgid+"'  ";
					if (query != null) {
						nickname = query.get("nickname");
						group_id = query.get("groupid");
						type=query.get("types");
					}
					if (!Checker.isEmpty(nickname)) {
						sql += " and nickname like '%" + nickname + "%'";
					}
					if (!Checker.isEmpty(group_id)) {
						sql += " and group_id = " + group_id + " ";
					}
					if (!Checker.isEmpty(type)) {
						sql += " and type = " + type + " ";
					}
					sql += "  order by member_code desc";
				} else if ("1".equals(account_belong)) {// 公有公众帐号
					sql = "select b.type, b.openid, b.headimgurl,b.member_code as member_code,b.nickname as nickname,b.mem_name ||'/'||b.phone as namephone,b.gender as gender,b.provice||b.city AS area ,"
							+ "b.age as age,a.memgroup_id as groupid,b.public_id as public_id,a.data_status as data_status from WS_MBDEATIL a left join WS_MEMBER b "
							+ "on a.member_code=b.member_code where a.care_orgid = '"
							+ orgid + "'";
					if (query != null) {
						nickname = query.get("nickname");
						group_id = query.get("groupid");
						type=query.get("types");
					}
					if (!Checker.isEmpty(nickname)) {
						sql += " and b.nickname like '%" + nickname + "%'";
					}
					if (!Checker.isEmpty(group_id)) {
						sql += " and a.memgroup_id = " + group_id + " ";
					}
					if (!Checker.isEmpty(type)) {
						sql += " and type = " + type + " ";
					}
					sql += "  order by b.member_code desc";
				}
				
			}
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sql;
	}
}
