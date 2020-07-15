package com.cdms.remind;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.util.Checker;

//年检到期提醒
public class AnnualInspectionRemindSql implements MapListFactory {

	@Override
	public MapList getMapList(ActionContext ac) {
		DBManager db = new DBManager();

		// 取得当前登录人所在机构的机构id
		String orgId = ac.getVisitor().getUser().getOrgId() + "%";
		// 查询变量表，取得年检提前提醒天数
		String vvalue = Var.get("annual_inspection");
		int remindTime = Integer.parseInt(vvalue);

		String sql = "select * from "
				+ "(select id,license_plate_number,device_sn_number,sim_card_number,annual_inspection_time,orgcode,"
				+ "DATE_PART('day',(annual_inspection_time + (annual_inspection_interval||'y')::interval)-now()) as annualinspectionlastday,"
				+ "to_char((annual_inspection_time + (annual_inspection_interval||'y')::interval),'yyyy-MM-dd HH24:MI:SS') as annualInspectionMaturity "
				+ "from CDMS_VEHICLEBASICINFORMATION) temp " + "where orgcode like '" + orgId
				+ "' and annualinspectionlastday<='" + remindTime + "' ";

		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_vehiclebasicinformation2.query");

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
				sql += "and annualinspectionlastday>=" + lastday1 + " ";
			}
			if (!Checker.isEmpty(queryRow.get("lastday2"))) {
				String lastday2 = queryRow.get("lastday2");
				sql += "and annualinspectionlastday<=" + lastday2 + " ";
			}
		}

		sql += " order by annualinspectionlastday";

		MapList mapList = new MapList();
		// 执行sql查询
		mapList = db.query(sql);
		return mapList;

	}

	// 计划任务，推送提醒消息时调用
	public MapList getRemindMapList() {

		DBManager db = new DBManager();

		// 查询变量表，取得年检提前提醒天数
		String vvalue = Var.get("annual_inspection");
		int remindTime = Integer.parseInt(vvalue);

		String sql = "select * from "
				+ "(select id,license_plate_number,device_sn_number,sim_card_number,annual_inspection_time,orgcode,"
				+ "DATE_PART('day',(annual_inspection_time + (annual_inspection_interval||'y')::interval)-now()) as annualinspectionlastday,"
				+ "to_char((annual_inspection_time + (annual_inspection_interval||'y')::interval),'yyyy-MM-dd HH24:MI:SS') as annualinspectionmaturity "
				+ "from CDMS_VEHICLEBASICINFORMATION) temp " + "where annualinspectionlastday<='" + remindTime + "' ";

		MapList mapList = new MapList();
		// 执行sql查询
		mapList = db.query(sql);
		return mapList;
	}

}
