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
 * 
 * 
 * 车辆里程油耗统计表 
 * 
 * 刘扬
 */

public class CarMileageFuelConsumption implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		Calendar c = Calendar.getInstance();
		  c.add(Calendar.MONTH, -1);
		  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		// 声明查询的条件字段：时间段
		String starttime = format.format(c.getTime());
		String endtime = format.format(new Date());
		endtime = DateTool.dateAddOne(endtime);
		// 获取查询的所有条件字段的数据
		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "car_mileage_fuel_consumption_statistics.query");
		if (queryRow != null) {
			// 如果得到的时间段条件字段不为空
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				// 赋值
				starttime = queryRow.get("starttime");
				// 添加查询条件
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
			}
		}
		// 创建sql语句
		//定义车牌号
		String license_plate_number = "";
		String sql = "";
		sql += " select '"+starttime+"' as starttime,'"+endtime+"' as endtime,"
		+ " b.license_plate_number"
		+ " ,b.orgcode"
		+ " ,SUM(v.mileage_statistics) as mileage_statistics"  //总里程
		+ ",SUM(v.fuel_sum) as fuel_sum "	//总油耗
		+ ",SUM(v.work_haul) as work_haul"   //工作里程
		+ ",SUM(v.work_fuel) as work_fuel"   //工作油耗
		+ " ,SUM(v.mileage_statistics)-SUM(v.work_haul) as offoperation" //非工作里程
		+ ",SUM(v.fuel_sum)-SUM(work_fuel) as offwork_fuel " //非工作油耗
		+ ",SUM(v.the_operation_mileage) as task_m"   //作业里程
		+ ",SUM(v.the_operation_fuel) as task_o"   //作业油耗
		+ " ,SUM(v.work_haul)-SUM(v.the_operation_mileage) as not_task_m" //非作业里程
		+ ",SUM(v.work_fuel)-SUM(the_operation_fuel) as not_task_o " //非作业油耗
		+ " from cdms_VehicleDayUtilization v "
		+ "inner join cdms_VehicleBasicInformation b "
		+ "on v.car_id=b.id";
		
		// 如果搜索栏orgcode不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgcode = queryRow.get("orgcode");
				sql+="  and  b.orgcode = '"+orgcode+"' ";
			}else{
				sql+="  and  b.orgcode like '"+orgcode+"%' ";
			}
		}else {
			sql+="  and  b.orgcode like '"+orgcode+"%' ";
		}
		
		
		// 如果获取的车牌号不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				license_plate_number = queryRow.get("license_plate_number");
			}
			sql+="  and  b.license_plate_number like '%"+license_plate_number+"%' ";
		}
	
		sql += " where 1=1";
		
		sql += " and v.date >= '" + starttime + "' ";
		sql += " and v.date < '" + endtime + "'  ";
		sql += " group by car_id,b.license_plate_number,b.orgcode order by b.license_plate_number";
		return sql;
	}
}
