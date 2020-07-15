package com.cdms.alarmrecordSql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.MapListFactory;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/*
 * 异常用车类
 * 
 * */

public class AlarmrecordAbnormalListSql implements SqlProvider {
	private Logger logger = LoggerFactory.getLogger(com.am.frame.order.SubmitOrderWebApi.class);

	@Override
	public String getSql(ActionContext ac) {
		String sql = "";
		String orgids = ac.getVisitor().getUser().getOrgId();// 机构id

		sql += "select ss.*, am.name from \r\n"
				+ "(select ca.*,cvb.license_plate_number,cvb.orgcode from CDMS_ALARMRECORD ca,CDMS_VEHICLEBASICINFORMATION cvb  \r\n"
				+ "where  1=1  and ca.car_id =cvb.id  and ca.operation_status='0'  and ca.alarm_type= '3"
				+ "'  and cvb.orgcode like '" + orgids + "%')  ss\r\n" + "LEFT JOIN am_member am\r\n"
				+ " on ss.driver=am.id ";

		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_alarmrecord_abnormal.query");

		// 声明查询的条件字段：时间段
		String license_plate_number = "";// 按车牌号查询
		String names = "";// 按驾驶员姓名查询
		String starttime = "";// 按开始时间查询
		String endtime = "";// 结束时间查询
		String fault_class_alarm_type = "";// 按故障类报警类型查询
		// 如果查询的条件字段的数据结果不为空
		if (queryRow != null) {
			sql += "where 1=1 ";

			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				// 赋值
				starttime = queryRow.get("starttime");
				// 添加查询条件
				sql += "and ss.alarm_time >'" + starttime + "'";
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
				sql += "and ss.alarm_time <='" + endtime + "'";
			}

			// 如果得到的name不为空，就按照用户输入的姓名查询
			if (!Checker.isEmpty(queryRow.get("name"))) {
				names = queryRow.get("name");
				sql += "and am.name like '%" + names + "%' ";
			}

			// 如果获取的车牌号不为空
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				license_plate_number = queryRow.get("license_plate_number");
				sql += "and ss.license_plate_number like '%" + license_plate_number + "%' ";
			}
			// 如果获取的故障类型不为空
			if (!Checker.isEmpty(queryRow.get("fault_class_alarm_type"))) {

				fault_class_alarm_type = queryRow.get("fault_class_alarm_type");
				logger.info("---------" + fault_class_alarm_type);
				sql += "and ss.fault_class_alarm_type='" + fault_class_alarm_type + "' ";
			}
		}

		// 添加分组条件
		sql += "order by alarm_time desc ";

		return sql.toString();
	}

}
