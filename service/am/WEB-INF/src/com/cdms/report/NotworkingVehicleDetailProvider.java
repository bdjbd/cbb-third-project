package com.cdms.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * 非工作时间用车统计表
 * 
 * @author zyz
 *
 */
public class NotworkingVehicleDetailProvider implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_notworkingvehicledetail.query");
		Calendar c = Calendar.getInstance();
		  c.add(Calendar.MONTH, -1);
		  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		// 声明查询的条件字段：时间段
		String starttime = format.format(c.getTime());
		String endtime = format.format(new Date());
		endtime = DateTool.dateAddOne(endtime);
		if (queryRow != null) {
			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				starttime = queryRow.get("starttime");
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
			}
		}
		String sql = "";
		sql += " select '"+starttime+"' as starttime,'"+endtime+"' as endtime,"
				+ "a.car_id as car_id," + "count(a.id) as times," + "sum(duration_of_the_vehicle) as sumtime,"
				+ "sum(a.mileage) as summileage," + "sum(oil) as sumoil,"
				+ "b.license_plate_number as license_plate_number," + "c.orgname as orgname," + "b.orgcode as orgcode "
				+ "from CDMS_NOTWORKINGVEHICLEDETAIL a,cdms_vehiclebasicinformation b,aorg c "
				+ "where a.car_id=b.id and b.orgcode=c.orgid ";

		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();

		// 如果查询的条件字段的数据结果不为空，根据用户输入的字段查询，如果为空，直接查询当前登陆人所在机构及其下级机构的数据
		if (queryRow != null) {
			// 如果获取的车牌号不为空
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				String license_plate_number = queryRow.get("license_plate_number");
				sql += "and license_plate_number like '%" + license_plate_number + "%' ";
			}

			// 如果得到的orgid不为空，就按照用户输入的机构查询
			// 如果得到的orgid为空，也就是用户没有输入机构，那么就查询当前用户所在的机构，及其下级机构
			if (!Checker.isEmpty(queryRow.get("orgid"))) {
				orgcode = queryRow.get("orgid");
				sql += "and orgcode = '" + orgcode + "' ";
			}else{
				sql += "and orgcode like '" + orgcode + "%' ";
			}
		}else{
			sql += "and orgcode like '" + orgcode + "%' ";
		}
		sql += "and '" + starttime + "'<=a.abnormal_vehicle_start_time ";
		sql += "and '" + endtime + "'>=a.abnormal_vehicle_start_time ";
		

		// 添加分组条件
		sql += "group by car_id,orgcode,license_plate_number,orgname ";

		// 按照车牌号排序
		sql += "order by license_plate_number ";

		// 返回结果
		return sql;
	}

}
