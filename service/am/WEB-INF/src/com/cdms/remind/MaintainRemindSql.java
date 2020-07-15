package com.cdms.remind;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

//保养到期提醒
public class MaintainRemindSql implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		DBManager db = new DBManager();

		// 取得当前登录人所在机构的机构id
		String orgId = ac.getVisitor().getUser().getOrgId() + "%";
		// 查询变量表，取得保养提前提醒里程
		//String vvalue = Var.get("reminder_mileage");
		//int remindMileage = Integer.parseInt(vvalue);

		String sql = "select license_plate_number," + "device_sn_number," + "sim_card_number," + "current_mileage, (maintenance_mileage-current_mileage) as residue,"
				+ "to_char(maintenance_mileage_time,'yyyy-MM-dd HH24:MI:SS') as maintenance_mileage_time "
				+ "from CDMS_VEHICLEBASICINFORMATION " + "where orgcode like '" + orgId + "' and current_mileage>="
				+ "reminder_mileage ";

		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_vehiclebasicinformation1.query");

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
			// 如果得到的上次里程保养时间条件字段不为空
			System.err.println("==========");
			if (!Checker.isEmpty(queryRow.get("last_mileage_time1"))) {
				String last_mileage_time1 = queryRow.get("last_mileage_time1");
				System.err.println(last_mileage_time1);
				sql += "and maintenance_mileage_time>='" + last_mileage_time1 + "' ";
			}
			if (!Checker.isEmpty(queryRow.get("last_mileage_time2"))) {
				String last_mileage_time2 = queryRow.get("last_mileage_time2");
				System.err.println(last_mileage_time2);
				sql += "and maintenance_mileage_time<='" + last_mileage_time2 + "' ";
			}
		}
		
		
		sql+="order by  (maintenance_mileage-current_mileage)";
		return sql.toString();

	}

	// 计划任务，推送提醒消息时调用
	public MapList getRemindMapList() {

		DBManager db = new DBManager();
		String sql = "select id,orgcode,license_plate_number," + "device_sn_number," + "sim_card_number," + "current_mileage,"
				+ "to_char(maintenance_mileage_time,'yyyy-MM-dd HH24:MI:SS') as maintenance_mileage_time "
				+ "from CDMS_VEHICLEBASICINFORMATION " + "where current_mileage>=reminder_mileage ";

		MapList mapList = new MapList();

		// 执行sql查询
		mapList = db.query(sql);
		return mapList;

	}
}
