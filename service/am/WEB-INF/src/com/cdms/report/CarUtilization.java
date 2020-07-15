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
 * 车辆利用率统计表 
 * 
 * 刘扬
 */

public class CarUtilization implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		// 获取查询的所有条件字段的数据
		// 获取当前登录人员所在机构的机构id
		String orgcode = ac.getVisitor().getUser().getOrgId();
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_car_utilization_statistics.query");
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
				// 赋值
				starttime = queryRow.get("starttime");
				// 添加查询条件
				
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				endtime = queryRow.get("endtime");
			}
		}
		// 创建sql语句
		String license_plate_number = "";
		String sql = "";
		sql +=  " select '"+starttime+"' as starttime,'"+endtime+"' as endtime,"
		+ " b.license_plate_number" + " ,b.orgcode" ;
		//是否有调度（编辑案件）权限
		boolean isDiaoDu = ac.getVisitor().getUser().hasUnitPrivilege("cdms", "cdms_case_edit.form", "e");
		if(isDiaoDu){
			sql+=" ,SUM(v.the_travel_time) as travel";
			sql+= " ,SUM(v.the_operation_time) as operation";
			sql+= " ,(case when SUM(the_travel_time) <= 0 then SUM(the_travel_time)   when SUM(the_operation_time)<=0 then 0    else (SUM(the_operation_time)/SUM(the_travel_time))*100 end)* 0.01  as percent ";
		}else {
			sql+=" ,SUM(v.the_travel_time) as travel";
			sql+= " ,SUM(v.word_time) as operation";
			sql+= " ,(case when SUM(the_travel_time) <= 0 then SUM(the_travel_time)   when SUM(word_time)<=0 then 0    else (SUM(word_time)/SUM(the_travel_time))*100 end)* 0.01  as percent ";
		}
				
		sql+= " from cdms_VehicleDayUtilization v " + "inner join cdms_VehicleBasicInformation b "
		+ "on v.car_id=b.id";

		// 如果搜索栏orgcode不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgcode = queryRow.get("orgcode");
				sql += "  and  b.orgcode = '" + orgcode + "' ";
			}else{
				sql += "  and  b.orgcode like '" + orgcode + "%' ";
			}
		}else {
			sql += "  and  b.orgcode like '" + orgcode + "%' ";
		}
		

		// 如果获取的车牌号不为空
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				license_plate_number = queryRow.get("license_plate_number");
			}
			sql += "  and  b.license_plate_number like '%" + license_plate_number + "%' ";
		}

		// 声明查询的条件字段：时间段
		sql += " where 1=1";
		sql += " and  v.date< '" + endtime + "'  ";
		sql += " and v.date >= '" + starttime + "' ";
		sql += " group by car_id,b.license_plate_number,b.orgcode";
		// 按照车牌号倒序排序
		sql += " order by license_plate_number ";
		return sql;
	}
}
