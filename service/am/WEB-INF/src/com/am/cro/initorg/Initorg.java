package com.am.cro.initorg;

import org.json.JSONObject;

import com.am.frame.systemAccount.groupAccount.GroupInitActionAbstract;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 汽修厂初始化类
 * 
 * @author guorenjie
 *
 */
public class Initorg extends GroupInitActionAbstract {

	@Override
	public JSONObject initOrg(DB db, String orgId) throws Exception {

		JSONObject result = new JSONObject();
		//查询机构创建时间
		String queryOrgCreateTime = "SELECT createtime from aorg where orgid='"+orgId+"'";
		String createTime = db.query(queryOrgCreateTime).getRow(0).get(0);
			// 初始化汽修厂 租期和状态，租期到期时间=当前时间+免费时间，状态=正常
			String leasedaysSql = "UPDATE aorg SET LeaseExpireDate=(to_timestamp('"+createTime+"','YYYY-MM-DD HH24:MI:SS') + interval '"
					+ getFreeTime(db)
					+ " D'),leasestate='1' WHERE orgid='"
					+ orgId + "' ";
			db.execute(leasedaysSql);

			result.put("code", "0");
			result.put("msg", "初始化完成!");

		return result;
	}

	// 获取变量中设置的免费试用天数
	public String getFreeTime(DB db) {
		String days = "";
		String sql = "select vvalue from avar where vid='days'";
		try {
			days = db.query(sql).getRow(0).get(0);
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return days;
	}

}
