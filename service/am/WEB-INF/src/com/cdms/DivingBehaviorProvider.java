package com.cdms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.cdms.report.DateTool;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/*
 * 驾驶行为分析
 */

public class DivingBehaviorProvider implements SqlProvider {

	@Override
	public String getSql(ActionContext ac) {
		String orgcode=ac.getVisitor().getUser().getOrgId();
		Calendar c = Calendar.getInstance();
		  c.add(Calendar.MONTH, -1);
		  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		// 声明查询的条件字段：时间段
		String ks = format.format(c.getTime());
		String js = format.format(new Date());
		js = DateTool.dateAddOne(js);
		String sql = "";
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_diving_behavior.query");
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("starttime"))) {
				ks = queryRow.get("starttime");
			}
			if (!Checker.isEmpty(queryRow.get("endtime"))) {
				js = queryRow.get("endtime");
				
			}
			
		}
		
		sql = ""
				+ "select '"+ks+"' as starttime,'"+js+"' as endtime,"
				+ " a.orgname,b.* from aorg a,("
				+ "select cvb.id car_id,license_plate_number,cvb.orgcode ,";
				sql+= "sum(car.frequency) sum_frequency,cast(sum(car.time_length)/3600.0 as numeric(30,2)) sum_time_length,sum(car.mileage) sum_mileage,sum(car.oil) sum_oil "
				+ " from " + " cdms_AlarmRecord as car," + " cdms_VehicleBasicInformation as cvb "
				+ " where car.car_id=cvb.id  and car.fault_class_alarm_type in ('21','22','23','26')";
				
		
		sql += " and '" + ks + "'<=car.alarm_time and '";
		sql += js + "'>car.alarm_time ";
		sql+=""
		+ " group by cvb.id"
		+ ") b"
		+" where a.orgid=b.orgcode";
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgcode = queryRow.get("orgcode");
				sql+=" and a.orgid = '"+orgcode+"' ";
			}else{
				sql+=" and a.orgid like '"+orgcode+"%' ";
			}
			
		}else{
			sql+=" and a.orgid like '"+orgcode+"%' ";
		}
		
		if (queryRow != null) {
			if (!Checker.isEmpty(queryRow.get("license_plate_number"))) {
				String license_plate_number = queryRow.get("license_plate_number");
				sql += " and license_plate_number like '%" + license_plate_number + "%' ";
			}
			
		}
		sql+="order by b.license_plate_number";
		return sql;
	}

}
