package com.cdms.remind;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

//保险到期提醒
public class InsuranceExpireRemindSql implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		DBManager db = new DBManager();

		// 取得当前登录人所在机构的机构id
		String orgId = ac.getVisitor().getUser().getOrgId() + "%";
		// 查询变量表，取得保险提前提醒天数
		String vvalue = Var.get("insurance");
		int remindTime = Integer.parseInt(vvalue);

		String sql = "select * from (select " + "id," + "license_plate_number," + "device_sn_number,"
				+ "sim_card_number,"
				+ "to_char(duration_of_insurance,'yyyy-MM-dd HH24:MI:SS') as duration_of_insurance,"
				+ "(select DATE_PART('day',duration_of_insurance-now()) from CDMS_VEHICLEBASICINFORMATION where id=a.id) insurancelastday,"
				+ "orgcode " + "from CDMS_VEHICLEBASICINFORMATION a) temp where orgcode like '" + orgId
				+ "' and insurancelastday<=" + remindTime + " ";

		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_vehiclebasicinformation3.query");

		// 如果查询的条件字段的数据结果不为空，根据用户输入的字段查询
		if (queryRow != null) {
			// 如果得到的车牌号条件字段不为空
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				String license_plate_number = queryRow.get("license_plate_number");
				sql += "and license_plate_number like '%" + license_plate_number + "%' ";
			}
			// 如果得到的终端编号条件字段不为空
			if (!Checker.isEmpty(queryRow.get("device_sn_number"))) {
				String device_sn_number = queryRow.get("device_sn_number");
				sql += "and device_sn_number like '%" + device_sn_number + "%' ";
			}
			// 如果得到的SIM卡号条件字段不为空
			if (!Checker.isEmpty(queryRow.get("sim_card_number"))) {
				String sim_card_number = queryRow.get("sim_card_number");
				sql += "and sim_card_number like '%" + sim_card_number + "%' ";
			}
			// 如果得到的剩余天数条件字段不为空
			if (!Checker.isEmpty(queryRow.get("lastday1"))) {
				String lastday1 = queryRow.get("lastday1");
				sql += "and insurancelastday>=" + lastday1 + " ";
			}
			if (!Checker.isEmpty(queryRow.get("lastday2"))) {
				String lastday2 = queryRow.get("lastday2");
				sql += "and insurancelastday<=" + lastday2 + " ";
			}
		}

		sql += " order by insurancelastday";

		return sql;

	}

	// 计划任务，推送提醒消息时调用
	public MapList getRemindMapList() {

		DBManager db = new DBManager();
		// 查询变量表，取得保险提前提醒天数
				String vvalue = Var.get("insurance");
				int remindTime = Integer.parseInt(vvalue);

		String sql = "select * from (select " + "id," + "license_plate_number," + "device_sn_number,"
				+ "sim_card_number,"
				+ "to_char(duration_of_insurance,'yyyy-MM-dd HH24:MI:SS') as duration_of_insurance,"
				+ "(select DATE_PART('day',duration_of_insurance-now()) from CDMS_VEHICLEBASICINFORMATION where id=a.id) insurancelastday,"
				+ "orgcode " + "from CDMS_VEHICLEBASICINFORMATION a) temp where insurancelastday<=" + remindTime + " ";

		MapList mapList = new MapList();

		// 执行sql查询
		mapList = db.query(sql);
		return mapList;
	}
}
