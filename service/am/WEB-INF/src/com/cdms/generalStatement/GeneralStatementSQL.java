package com.cdms.generalStatement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.report.DateTool;
import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class GeneralStatementSQL implements SqlProvider {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String getSql(ActionContext ac) {
		String str = "";
		String carId = "";
		String name = "";
		String driverSql = "";
		String castratimesql = "";// 报警开始时间
		String caendtimesql = "";// 报警结束时间(报警表)
		String vvstartimesql = "";// 违章时间（违章表）
		String vvendtimesql = "";// 违章时间
		String cnstratimesql = "";// 非工作时间开始用车时间
		String cnendtimesql = "";// 非工作时间结束用车时间（非工作时间用车表）
		String arstratimesql = "";// 考勤时间（考勤表）
		String arendtimesql = "";
		String cestratimesql = "";// 进出区域时间（进出区域表）
		String ceendtimesql = "";
		String cvrcstratimesql = "";// 怠速（车辆运行情况表）
		String cvrcendtimesql = "";
		String vucstratimesql = "";// 里程油耗（车辆天利用情况表）
		String vucendtimesql = "";
		String orgsql = "";// 机构
		String carIdsql = "";// 车牌
		String driverstsql = "";// 人车匹配历史开始时间
		String driverensql = "";// 人车匹配历史结束时间
		String membername = "";// 駕駛員
		String endtime = "";
		// 获取查询的所有条件字段的数据
		Row queryRow = FastUnit.getQueryRow(ac, "cdms", "cdms_general_statement.query");

		// 声明查询的条件字段：时间段
		String carmembers = "";// 车牌id
		String orgids = ac.getVisitor().getUser().getOrgId();// 获取当前机构id
		//String orgids ="";//查询机构
		Calendar c = Calendar.getInstance();
		  c.add(Calendar.MONTH, -1);
		  SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
		// 声明查询的条件字段：时间段
		String starttime = format.format(c.getTime());
		endtime = format.format(new Date());
		endtime = DateTool.dateAddOne(endtime);
		// 如果查询的条件字段的数据结果不为空
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

			// 如果得到的车牌号不为空，就按照用户输入的车牌号查询
			if (!Checker.isEmpty(queryRow.get("car_number"))) {
				carmembers = queryRow.get("car_number");
				logger.info("carmembers++++++++++++++++++++++" + carmembers);
				carIdsql = "and cv.license_plate_number like '%" + carmembers + "%' ";
			}

			// 如果获取的机构不为空
			if (!Checker.isEmpty(queryRow.get("orgcode"))) {
				orgids= queryRow.get("orgcode");
				orgsql = "and cv.orgcode = '" + orgids + "' ";
			}else{
				orgsql = "and cv.orgcode like '" + orgids + "%' ";
			}
	   }else{
		   orgsql = " and cv.orgcode like '" + orgids + "%' ";
	   }
		vvstartimesql = " and vv.illegal_time>='" + starttime + "'";
		castratimesql = " and ca.start_time >='" + starttime + "'";
		cnstratimesql = " and cn.abnormal_vehicle_start_time>='" + starttime + "'";
		cestratimesql = " and ce.start_time>='" + starttime + "'";
		arstratimesql = " and ar.date_of_attendance>='" + starttime + "'";
		vucstratimesql = " and vu.date>='" + starttime + "'";
		cvrcstratimesql = " and cvrc.acc_start_time>='" + starttime + "'";
		driverstsql = " and cp.start_time>='" + starttime + "'";
		caendtimesql = " and ca.end_time <'" + endtime + "'";
		vvendtimesql = " and vv.illegal_time<'" + endtime + "'";
		cnendtimesql = " and cn.abnormal_vehicle_end_time<'" + endtime + "'";
		arendtimesql = " and ar.date_of_attendance<'" + endtime + "'";
		ceendtimesql = " and ce.end_time<'" + endtime + "'";
		vucendtimesql = " and vu.date<'" + endtime + "'";
		cvrcendtimesql = " and cvrc.acc_end_time<'" + endtime + "'";
		driverensql = " and cp.end_time<'" + endtime + "'";
		String backparams = "&autoback=1";//跳转参数，用于返回总报表
       str = "select  \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_illegal_statistics.list.do?m=s&clear=cdms.cdms_illegal_statistics.query&cdms_illegal_statistics.list.car_id='||cv.id||'&cdms_illegal_statistics.list.starttime="
		+ starttime + "&cdms_illegal_statistics.list.enttime=" + endtime + "&b=true"+backparams+">'||"
		+ "	(select COALESCE(count(*),0) from cdms_VehicleViolationRecord vv where vv.car_id=cv.id " + vvstartimesql
		+ vvendtimesql + ") " + "||'</a>'" + " as illegal_sum, \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=超速报警"+backparams+">'||"
		+ " (select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='21' "
		+ castratimesql + caendtimesql + ") " + "||'</a>'" + " as speeding_sum, \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=急加速"+backparams+">'||"
		+ " (select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='22' "
		+ castratimesql + caendtimesql + ")" + "||'</a>'" + "  as rapid_acceleration_sum, \r\n" + ""
		+ "'<a href=" + ""
		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=急减速"+backparams+">'||"
		+ "	(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='23' "
		+ castratimesql + caendtimesql + ") " + "||'</a>'" + " as deceleration_sum, \r\n" + "" 
		
//		+ "'<a href="
//		+ ""
//		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
//		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=急转弯"+backparams+">'||"
		+ "	(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='24' "
		+ castratimesql + caendtimesql + ") " 
//		+ "||'</a>'" 
		+ " as corner_sum, \r\n" + "" 
//		+ "'<a href=" + ""
//		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
//		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=急变道报警"+backparams+">'||"
		+ "	(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='25' "
		+ castratimesql + caendtimesql + ") " 
//		+ "||'</a>'" 
		+ " as lane_change_sum, \r\n" + "" 
		
		+ "'<a href=" + ""
		+ "/cdms/cdms_diving_behavior_details.list.do?m=s&cdms_diving_behavior_details.list.car_id='||cv.id||'&cdms_diving_behavior_details.list.starttime="
		+ starttime + "&cdms_diving_behavior_details.list.enttime=" + endtime + "&cdms_diving_behavior_details.list.fatname1=疲劳驾驶报警"+backparams+">'||"
		+ "	(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='26' "
		+ castratimesql + caendtimesql + ") " + "||'</a>'" + " as fatigue_sum, \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_notworkingvehicledetail_details.list.do?m=s&cdms_notworkingvehicledetail_details.list.car_id='||cv.id||'&cdms_notworkingvehicledetail_details.list.starttime="
		+ starttime + "&cdms_notworkingvehicledetail_details.list.enttime=" + endtime + backparams+">'||"
		+ "		(select COALESCE(CAST((sum(duration_of_the_vehicle)/3600)as numeric(30,2)),0) from cdms_NotworkingVehicleDetail cn where cn.car_id=cv.id  "
		+ cnstratimesql + cnendtimesql + ") " + "||'</a>'" + " as notwork_time_car_sum, \r\n" + "" + "'<a href="
		+ ""
		+ "/cdms/cdms_enclosurerecord.list.do?m=s&cdms_enclosurerecord.list.car_id1='||cv.id||'&cdms_enclosurerecord.list.starttime="
		+ starttime + "&cdms_enclosurerecord.list.enttime=" + endtime + "&b=true"+backparams+">'||"
		+ "	(select COALESCE(count(*),0) from cdms_EnclosureRecord ce,cdms_electronicfence f where ce.fence_id=f.id and ce.car_id=cv.id  " + cestratimesql + ceendtimesql
		+ ") " + "||'</a>'" + " as region_out_in_sum, \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_outsideusecarform.list.do?m=s&cdms_outsideusecarform.list.car_id='||cv.id||'&cdms_outsideusecarform.list.starttime="
		+ starttime + "&cdms_outsideusecarform.list.enttime=" + endtime + "&b=true"+backparams+">'||"
		+ "	(select COALESCE(CAST((sum(duration)/3600)as numeric(30,2)),0)  from cdms_EnclosureRecord ce,cdms_electronicfence f where ce.fence_id=f.id and f.electronic_fence_type='2' and ce.car_id=cv.id  "
		+ cestratimesql + ceendtimesql + " ) " + "||'</a>'" + " as region_out_tim, \r\n" + "" + "'<a href=" + ""
		+ "/cdms/cdms_attendancerecord.list.do?m=s&cdms_attendancerecord.list.car_id='||cv.id||'&cdms_attendancerecord.list.starttime="
		+ starttime + "&cdms_attendancerecord.list.enttime=" + endtime + "&b=true"
		+ "&cdms_attendancerecord.list.name='||"
//		加上之后考勤为0时无法显示0而显示空
//		+ "(select distinct(am.name)  from cdms_PeopleCarsHistory cp,am_member am where cp.member_id=am.id  and cp.car_id=cv.id  LIMIT 1)||"
		+ "'"+backparams+">'||"
		+ "	(select COALESCE(CAST((sum(clocking_in_duration)/3600)as numeric(30,2)),0) from cdms_attendancerecord ar where ar.car_id=cv.id  "
		+ arstratimesql + arendtimesql + " ) " + "||'</a>'" + " as work_time_sum, \r\n"
		+ " ((select COALESCE(sum(vu.mileage_statistics),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ") ||'(KM)/'|| \r\n"
		+ "(select COALESCE(round( CAST(sum(vu.fuel_sum) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id " + vucstratimesql + vucendtimesql + "   ) ||'(L)') as mileage_sum,\r\n"
		+"((select COALESCE(sum(vu.work_haul),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + " ) ||'(KM)/'||  \r\n"
		+ "(select COALESCE(round( CAST(sum(vu.work_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + ")||'(L)') as work_oil, \r\n"
		+"((select COALESCE(sum(vu.mileage_statistics-vu.work_haul),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id " + vucstratimesql + vucendtimesql + ")  ||'(KM)/'|| \r\n"
		+ "(select COALESCE(round( CAST(sum(vu.fuel_sum-vu.work_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ")||'(L)') as notwork_oil_sum, \r\n"
		+"((select COALESCE(sum(vu.the_operation_mileage),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + ") ||'(KM)/'|| \r\n"
		+ "(select COALESCE(round( CAST(sum(vu.the_operation_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ")||'(L)') as task_oil_sum,  \r\n"
		+"((select COALESCE(sum(vu.work_haul-vu.the_operation_mileage),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + " ) ||'(KM)/'||  \r\n"
		+ "(select COALESCE(round( CAST(sum(vu.work_fuel-vu.the_operation_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ")||'(L)') as notask_oil_sum, \r\n"
		+"(select array_to_string(array(select  distinct(am.name)  from cdms_PeopleCarsHistory cp,am_member am where cp.member_id=am.id  and cp.car_id=cv.id "+driverstsql+driverensql+"),',')) as dirve_name,"
//		+ "(select round( CAST(sum(vu.the_operation_fuel) as numeric), 1) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  "
//		+ vucstratimesql + vucendtimesql + "  ) as task__oil_sum,  \r\n"
//		+ "(select round( CAST(sum(vu.fuel_sum-vu.the_operation_fuel) as numeric), 1) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  "
//		+ vucstratimesql + vucendtimesql + "  ) as nottask_oil_sum, \r\n" 
		+ "" + "'<a href=" + ""
		+ "/cdms/cdms_ldle_speed_statistics.list.do?m=s&cdms_ldle_speed_statistics.list.car_id='||cv.id||'&cdms_ldle_speed_statistics.list.starttime="
		+ starttime + "&cdms_ldle_speed_statistics.list.enttime=" + endtime + "&b=true"+backparams+">'||"
		+ "	(select COALESCE(CAST((sum(run_time)/3600)as numeric(30,2)),0) \r\n"
		+ "	from cdms_VehiclesRunningCondition cvrc \r\n"
		+ "	where cv.id=cvrc.car_id  " + cvrcstratimesql + cvrcendtimesql + ")  " + "||'</a>'"
		+ "  as idling_abnormality,  \r\n" 
		+ "	cv.license_plate_number,cv.id,ao.orgname, '"+carmembers+"' as carmembers, '"+orgids+"' as orgids, '"+starttime+"' as starttime, '"+endtime+"' as endtime \r\n"
		+ "from cdms_vehiclebasicinformation cv, aorg ao  where ao.orgid=cv.orgcode " + carIdsql
		+ orgsql + " group by cv.id,cv.license_plate_number,ao.orgname order by cv.license_plate_number ";

       return str.toString();
	}
}