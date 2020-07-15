package com.cdms.ExcelOutPut.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.cdms.ExcelOutPut.pojo.JavaBean;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class ExcelOutPut implements IWebApiService {
	private Logger log = LoggerFactory.getLogger(getClass());
	HSSFWorkbook wb = null;

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse res) {
		MapList rValue = new MapList();
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

		MapList mList = null;
		DBManager db = new DBManager();
		// 声明查询的条件字段：时间段
		String carmembers = "";
		String orgcode = "";//当前登陆人机构
		String orgids = "";//搜索框所选机构
		String starttime = "";
		String endtime = "";

		// 如果查询的条件字段的数据结果不为空

		// 如果得到的时间段条件字段不为空

		// 赋值
		starttime = request.getParameter("starttime");
		log.info("总报表导出参数开始时间==" + starttime);
		if (!"".equals(starttime)) {
			// 添加查询条件
			vvstartimesql = "and vv.illegal_time>='" + starttime + "'";
			castratimesql = "and ca.start_time >='" + starttime + "'";
			cnstratimesql = "and cn.abnormal_vehicle_start_time>='" + starttime + "'";
			cestratimesql = "and ce.start_time>='" + starttime + "'";
			arstratimesql = "and ar.date_of_attendance>='" + starttime + "'";
			vucstratimesql = "and vu.date>='" + starttime + "'";
			cvrcstratimesql = "and cvrc.acc_start_time>='" + starttime + "'";
			driverstsql = "and cp.start_time>='" + starttime + "'";
		}

		endtime = request.getParameter("endtime");
		log.info("总报表导出参数结束时间==" + endtime);
		if (!"".equals(endtime)) {
			caendtimesql = "and ca.end_time <='" + endtime + "'";
			vvendtimesql = "and vv.illegal_time<='" + endtime + "'";
			cnendtimesql = "and cn.abnormal_vehicle_end_time<='" + endtime + "'";
			arendtimesql = "and ar.date_of_attendance<='" + endtime + "'";
			ceendtimesql = " and ce.end_time<='" + endtime + "'";
			vucendtimesql = "and vu.date<='" + endtime + "'";
			cvrcendtimesql = "and cvrc.acc_end_time<='" + endtime + "'";
			driverensql = "and cp.end_time<='" + endtime + "'";
		}

		// 如果得到的车牌号不为空，就按照用户输入的车牌号查询
		carmembers = request.getParameter("carmembers");
		log.info("总报表导出参数车牌号==" + carmembers);
		if (!"".equals(carmembers)) {
			carIdsql = "and cv.license_plate_number like '%" + carmembers + "%' ";
		}

		
		// 如果获取的搜索机构不为空
		orgids = request.getParameter("orgids");
		log.info("总报表导出参数机构==" + orgids);
		if (!"".equals(orgids)) {
			orgsql = "and cv.orgcode = '" + orgids + "' ";
		}else {
			// 如果搜索机构为空，则查询当前以及下级机构的数据
			orgcode = request.getParameter("orgcode");
			log.info("总报表导出参数当前机构==" + orgcode);
			if (!"".equals(orgcode)) {
				orgsql = "and cv.orgcode like '" + orgcode + "%' ";
			}
		}

		// 获取字段加入<a>标签 并给下级页面传参
		String sql = "select "
				+ "(select COALESCE(count(*),0) from cdms_VehicleViolationRecord vv where vv.car_id=cv.id " + vvstartimesql
				+ vvendtimesql + ")  as illegal_sum,  \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='21' "
				+ castratimesql + caendtimesql + ")  as speeding_sum, \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='22' "
				+ castratimesql + caendtimesql + ")  as rapid_acceleration_sum, \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='23' "
				+ castratimesql + caendtimesql + ")  as deceleration_sum,  \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='24' "
				+ castratimesql + caendtimesql + ")  as corner_sum,  \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='25' "
				+ castratimesql + caendtimesql + ")  as lane_change_sum,  \r\n"
				+ "(select COALESCE(sum(ca.frequency),0) from cdms_alarmrecord ca where ca.car_id=cv.id and ca.fault_class_alarm_type='26' "
				+ castratimesql + caendtimesql + ")  as fatigue_sum, \r\n"
				+ "(select COALESCE(CAST((sum(duration_of_the_vehicle)/3600)as numeric(30,2)),0) from cdms_NotworkingVehicleDetail cn where cn.car_id=cv.id  "
				+ cnstratimesql + cnendtimesql + ")  as notwork_time_car_sum,  \r\n"
				+ "(select COALESCE(count(*),0) from cdms_EnclosureRecord ce,cdms_electronicfence f where ce.fence_id=f.id and ce.car_id=cv.id  " + cestratimesql + ceendtimesql
				+ ")  as region_out_in_sum, \r\n"
				+ "(select COALESCE(CAST((sum(duration)/3600)as numeric(30,2)),0)  from cdms_EnclosureRecord ce,cdms_electronicfence f where ce.fence_id=f.id and f.electronic_fence_type='2' and ce.car_id=cv.id  "
				+ cestratimesql + ceendtimesql + " )  as region_out_tim,  \r\n"
				+ "(select COALESCE(CAST((sum(clocking_in_duration)/3600)as numeric(30,2)),0) from cdms_attendancerecord ar where ar.car_id=cv.id  "
				+ arstratimesql + arendtimesql + " )  as work_time_sum, \r\n"
				+ "(select COALESCE(sum(vu.mileage_statistics),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ") as mileage_sum,\r\n"
				+ "(select COALESCE(round( CAST(sum(vu.fuel_sum) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id " + vucstratimesql + vucendtimesql + "   ) as oil_sum, \r\n"
				+ "(select COALESCE(round( CAST(sum(vu.work_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + ") as work_oil,\r\n"
				+ "(select COALESCE(round( CAST(sum(vu.fuel_sum-vu.work_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ") as notwork_oil,  \r\n"
				+ "(select COALESCE(sum(vu.work_haul),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + " ) as work_mileage,  \r\n"
				+ "(select COALESCE(sum(vu.mileage_statistics-vu.work_haul),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id " + vucstratimesql + vucendtimesql + ") as notwork_mileage, \r\n"
				+ "(select COALESCE(sum(vu.the_operation_mileage),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + ") as task_mileage_sum ,\r\n"
				+ "(select COALESCE(sum(vu.work_haul-vu.the_operation_mileage),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id   " + vucstratimesql + vucendtimesql + " ) as nottask_mileage_sum, \r\n"
				+ "(select COALESCE(round( CAST(sum(vu.the_operation_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ") as task__oil_sum,   \r\n"
				+ "(select COALESCE(round( CAST(sum(vu.work_fuel-vu.the_operation_fuel) as numeric), 1),0) from cdms_VehicleDayUtilization vu where vu.car_id=cv.id  " + vucstratimesql + vucendtimesql + ") as nottask__oil_sum, 		\r\n"
				+ "(select COALESCE(CAST((sum(run_time)/3600)as numeric(30,2)),0) \r\n"
		+ "	from cdms_VehiclesRunningCondition cvrc \r\n"
		+ "	where cv.id=cvrc.car_id  " + cvrcstratimesql + cvrcendtimesql + ")    as idling_abnormality,   \r\n"
				+ "cv.license_plate_number,cv.id,ao.orgname \r\n"
				+ "from cdms_vehiclebasicinformation cv, aorg ao  where ao.orgid=cv.orgcode " + carIdsql + orgsql
				+ " group by cv.id,cv.license_plate_number,ao.orgname order by cv.license_plate_number ";

		mList = db.query(sql);

		String mileage_sum = "";// 行车总里程
		String oil_sum = "";// 行车总油耗
		String work_oil = "";// 工作总油耗
		String notwork_oil = "";// 非工作总油耗
		String work_mileage = "";// 工作总里程
		String task_mileage_sum = "";// 作业总里程
		String nottask_mileage_sum = "";// 非作业总里程
		String task__oil_sum = "";// 作业总油耗
		String nottask__oil_sum = "";// 非作业总油耗
		String notwork_mileage = "";// 非工作总里程
		if (!Checker.isEmpty(mList)) {
			log.info("mList++++++++++++++++++++++" + mList);
			for (int i = 0; i < mList.size(); i++) {
				carId = mList.getRow(i).get("id");
				mileage_sum = mList.getRow(i).get("mileage_sum");
				oil_sum = mList.getRow(i).get("oil_sum");
				work_oil = mList.getRow(i).get("work_oil");
				notwork_oil = mList.getRow(i).get("notwork_oil");
				work_mileage = mList.getRow(i).get("work_mileage");
				task_mileage_sum = mList.getRow(i).get("task_mileage_sum");
				nottask_mileage_sum = mList.getRow(i).get("nottask_mileage_sum");
				task__oil_sum = mList.getRow(i).get("task__oil_sum");
				nottask__oil_sum = mList.getRow(i).get("nottask__oil_sum");

				log.info("nottask__oil_sum++++++++++++++++++++++" + nottask__oil_sum);
				notwork_mileage = mList.getRow(i).get("notwork_mileage");
				// 查询历史驾驶员
//				driverSql = "select  distinct(am.name) from cdms_PeopleCarsHistory cp,am_member am where cp.member_id=am.id and cp.car_id='"
//						+ carId + "'" + driverstsql + driverensql + "";
				driverSql = "select array_to_string(array(select  distinct(am.name)  from cdms_PeopleCarsHistory cp,am_member am where cp.member_id=am.id  and cp.car_id='"+carId+"' "+driverstsql+driverensql+"),',') as name";
				MapList driverlist = db.query(driverSql);
				if (!Checker.isEmpty(driverlist)) {
//					for (int j = 0; j < driverlist.size(); j++) {
//						name += driverlist.getRow(j).get("name") + " ";
//						log.info("carId++++++++++++++++++++++" + name);
//					}
					name = driverlist.getRow(0).get("name");
				} else {
					name = "";
				}
				mList.getRow(i).put("driver", name);
				name = "";

				// 让字段显示成（里程/油耗样式）拼接
				// 总里程/油耗

				String xmileage_sum = "";
				if (!Checker.isEmpty(mileage_sum)) {
					xmileage_sum = mileage_sum.replace(mileage_sum, mileage_sum + "/" + oil_sum);
					mList.getRow(i).put("mileage_oil_sum", xmileage_sum);
				}
				// 工作总里程/油耗

				String xwork_mileage = "";
				if (!Checker.isEmpty(work_mileage)) {
					xwork_mileage = work_mileage.replace(work_mileage, work_mileage + "/" + work_oil);
					mList.getRow(i).put("work_oil", xwork_mileage);
				}

				// 非工作总里程/油耗
				String xnotwork_mileage = "";
				if (!Checker.isEmpty(notwork_mileage)) {
					xnotwork_mileage = notwork_mileage.replace(notwork_mileage, notwork_mileage + "/" + notwork_oil);
					mList.getRow(i).put("notwork_oil_sum", xnotwork_mileage);
				}
				// 作业总里程/油耗
				String xtask_mileage_sum = "";
				if (!Checker.isEmpty(task_mileage_sum)) {
					xtask_mileage_sum = task_mileage_sum.replace(task_mileage_sum,
							task_mileage_sum + "/" + task__oil_sum);
					mList.getRow(i).put("task_oil_sum", xtask_mileage_sum);
				}
				// 非作业总里程/油耗
				String xnottask_mileage_sum = "";
				if (!Checker.isEmpty(nottask_mileage_sum)) {
					xnottask_mileage_sum = nottask_mileage_sum.replace(nottask_mileage_sum,
							nottask_mileage_sum + "/" + nottask__oil_sum);
					mList.getRow(i).put("notask_oil_sum", xnottask_mileage_sum);
				}

			}
		}
		rValue = mList;
		// 1.创建一个workbook，对应一个Excel文件
		wb = new HSSFWorkbook();
		// 2.在workbook中添加一个sheet，对应Excel中的一个sheet
		HSSFSheet sheet = wb.createSheet("综合报表");
		// 3.在sheet中添加表头第0行，老版本poi对excel行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 4.创建单元格，设置值表头，设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		// 居中格式
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFCell cell = row.createCell(0);
		cell.setCellValue("车牌号码");
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue("机构");
		cell.setCellStyle(style);

		cell = row.createCell(2);
		cell.setCellValue("驾驶员");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("超速次数(次)");
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue("疲劳驾驶次数(次)");
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue("急加速(次)");
		cell.setCellStyle(style);

		cell = row.createCell(6);
		cell.setCellValue("急减速(次)");
		cell.setCellStyle(style);

		cell = row.createCell(7);
		cell.setCellValue("急转弯(次)");
		cell.setCellStyle(style);

		cell = row.createCell(8);
		cell.setCellValue("急变道次数(次)");
		cell.setCellStyle(style);

		cell = row.createCell(9);
		cell.setCellValue("行车总里程/总油耗(km/l)");
		cell.setCellStyle(style);

		cell = row.createCell(10);
		cell.setCellValue("工作里程/油耗(km/l)");
		cell.setCellStyle(style);

		cell = row.createCell(11);
		cell.setCellValue("非工作里程/油耗(km/l)");
		cell.setCellStyle(style);

		cell = row.createCell(12);
		cell.setCellValue("作业里程/油耗(km/l)");
		cell.setCellStyle(style);

		cell = row.createCell(13);
		cell.setCellValue("非作业里程/油耗(km/l)");
		cell.setCellStyle(style);

		cell = row.createCell(14);
		cell.setCellValue("违章次数(次) ");
		cell.setCellStyle(style);

		cell = row.createCell(15);
		cell.setCellValue("怠速总时长(h)");
		cell.setCellStyle(style);

		cell = row.createCell(16);
		cell.setCellValue("非工作时间用车总时长(h)");
		cell.setCellStyle(style);

		cell = row.createCell(17);
		cell.setCellValue("区域外用车总时长(h)");
		cell.setCellStyle(style);

		cell = row.createCell(18);
		cell.setCellValue("进出区域总次数(次)");
		cell.setCellStyle(style);

		cell = row.createCell(19);
		cell.setCellValue("考勤总时长(h)");
		cell.setCellStyle(style);

		List<JavaBean> lists = new ArrayList<JavaBean>();
		for (int i = 0; i < rValue.size(); i++) {
			JavaBean jb = new JavaBean();
			jb.setLicense_plate_number(rValue.getRow(i).get("license_plate_number"));
			jb.setOrgname(rValue.getRow(i).get("orgname"));
			jb.setDriver(rValue.getRow(i).get("driver"));
			jb.setSpeeding_sum(rValue.getRow(i).get("speeding_sum"));
			jb.setFatigue_sum(rValue.getRow(i).get("fatigue_sum"));
			jb.setRapid_acceleration_sum(rValue.getRow(i).get("rapid_acceleration_sum"));
			jb.setDeceleration_sum(rValue.getRow(i).get("deceleration_sum"));
			jb.setCorner_sum(rValue.getRow(i).get("corner_sum"));
			jb.setLane_change_sum(rValue.getRow(i).get("lane_change_sum"));
			jb.setMileage_oil_sum(rValue.getRow(i).get("mileage_oil_sum"));
			jb.setWork_oil(rValue.getRow(i).get("work_oil"));
			jb.setNotwork_oil_sum(rValue.getRow(i).get("notwork_oil_sum"));
			jb.setTask_oil_sum(rValue.getRow(i).get("task_oil_sum"));
			jb.setNotask_oil_sum(rValue.getRow(i).get("notask_oil_sum"));
			jb.setIllegal_sum(rValue.getRow(i).get("illegal_sum"));
			jb.setIdling_abnormality(rValue.getRow(i).get("idling_abnormality"));
			jb.setNotwork_time_car_sum(rValue.getRow(i).get("notwork_time_car_sum"));
			jb.setRegion_out_tim(rValue.getRow(i).get("region_out_tim"));
			jb.setRegion_out_in_sum(rValue.getRow(i).get("region_out_in_sum"));
			jb.setWork_time_sum(rValue.getRow(i).get("work_time_sum"));
			lists.add(jb);
		}

		for (int i = 0; i < lists.size(); i++) {
			row = sheet.createRow((int) i + 1);
			JavaBean list = lists.get(i);
			row.createCell(0).setCellValue(list.getLicense_plate_number());
			row.createCell(1).setCellValue(list.getOrgname());
			row.createCell(2).setCellValue(list.getDriver());
			row.createCell(3).setCellValue(list.getSpeeding_sum());
			row.createCell(4).setCellValue(list.getFatigue_sum());
			row.createCell(5).setCellValue(list.getRapid_acceleration_sum());
			row.createCell(6).setCellValue(list.getDeceleration_sum());
			row.createCell(7).setCellValue(list.getCorner_sum());
			row.createCell(8).setCellValue(list.getLane_change_sum());
			row.createCell(9).setCellValue(list.getMileage_oil_sum());
			row.createCell(10).setCellValue(list.getWork_oil());
			row.createCell(11).setCellValue(list.getNotwork_oil_sum());
			row.createCell(12).setCellValue(list.getTask_oil_sum());
			row.createCell(13).setCellValue(list.getNotask_oil_sum());
			row.createCell(14).setCellValue(list.getIllegal_sum());
			row.createCell(15).setCellValue(list.getIdling_abnormality());
			row.createCell(16).setCellValue(list.getNotwork_time_car_sum());
			row.createCell(17).setCellValue(list.getRegion_out_tim());
			row.createCell(18).setCellValue(list.getRegion_out_in_sum());
			row.createCell(19).setCellValue(list.getWork_time_sum());
		}
		// 不弹出下载框
		// FileOutputStream out =new FileOutputStream("");
		// wb.write(out);
		// out.close();


		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		String fileName = "综合报表";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			byte[] content = os.toByteArray();
			InputStream is = new ByteArrayInputStream(content);
			// 设置response参数，可以打开下载页面
			res.reset();
			res.setContentType("application/vnd.ms-excel;charset=utf-8");
			res.setHeader("Content-Disposition",
					"attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
			ServletOutputStream out = res.getOutputStream();

			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			// Simple read/write loop.
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

		} catch (Exception e) {
		} finally {
			if (bis != null)
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (bos != null)
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
}
