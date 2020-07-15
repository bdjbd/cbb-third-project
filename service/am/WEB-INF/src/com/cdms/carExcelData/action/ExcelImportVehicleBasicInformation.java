package com.cdms.carExcelData.action;

/**
 * 
 * 
 * 车辆基础信息导入功能
 */
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cdms.carExcelData.action.ExcelImportVehicleBasicInformation;
import com.cdms.carExcelData.pojo.VehicleBasicInformation;
import com.am.frame.webapi.db.DBManager;
import com.cdms.carExcelData.action.DefaultImportDataAction;
import com.cdms.carExcelData.service.ExcelImportService;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.util.Checker;

public class ExcelImportVehicleBasicInformation extends DefaultImportDataAction implements ExcelImportService {
	private Logger logger = LoggerFactory.getLogger(ExcelImportVehicleBasicInformation.class);

	@Override
	public Class<?> newTclass(String className)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		log.info("进入车辆基础信息导入类，获得类名" + cls);
		return cls;
	}

	@Override
	public ActionContext saveDB(ActionContext ac, File file, String className, String import_year, String import_rule,
			String menuname) {
		Class<?> cls = null;
		try {
			cls = newTclass(className);
			logger.debug("执行反射，进入saveDB方法，cls：" + cls);
			logger.debug("saveDB方法className：" + className);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		int count = 0; // 行数
		int count1 = 0; // 新增行数
		int count2 = 0; // 修改行数
		String msg = ""; // 影响行数
		String errorMsg = "";// 失败数据
		logger.debug("开始导入参数设置");
		ImportParams importParams = new ImportParams();// 导入参数设置
		// 走到这一步时，线下报错：找不到工具类ImportParams
		log.debug("已引入参数设置");
		System.err.println("file:" + file);
		System.err.println("importParams:" + importParams);
		List<VehicleBasicInformation> us = ExcelImportUtil.importExcel(file, (Class<?>) cls, importParams);
		System.err.println("size----->"+us.size());
		log.debug("准备for循环");
		
		DBManager db = new DBManager();
		
		for (VehicleBasicInformation i : us) {
			log.debug("进入for循环");
			UUID uuID = UUID.randomUUID();
			
			
			// 新增语句
			String insertSql1 = "insert into cdms_VehicleBasicInformation ( id ";
			String insertSql2 = "  values ( '" + uuID + "' ";
			String sumInsertSql = "";

			// 更新语句
			String updateSql = "update cdms_VehicleBasicInformation set  ";

			//获得车牌号 如果车牌号不为空    insertSql和updateSql更新
			String license_plate_number = i.getLicense_plate_number();
			if (!Checker.isEmpty(license_plate_number)) {
				insertSql1 += " ,license_plate_number ";
				insertSql2 += " ,'" + license_plate_number + "'";
				updateSql += " license_plate_number ='" + license_plate_number + "' ";
			}
			log.debug("获得车牌号码(必填)"+license_plate_number);
			System.err.println(license_plate_number);
			System.err.println(license_plate_number);
			System.err.println(license_plate_number);
			
			//获得sim卡号  如果sim卡号不为空  insertSql和updateSql更新
			String sim_card_number = i.getSim_card_number();
			if (!Checker.isEmpty(sim_card_number)) {
				insertSql1 += " ,sim_card_number ";
				insertSql2 += " ,'" + sim_card_number + "' ";
				updateSql += " ,sim_card_number ='" + sim_card_number + "' ";
			}
			log.debug("获得sim卡号"+sim_card_number);
			
			
			
			//百公里油耗 如果不为空  insertSql和updateSql更新
			String one_hundred_kilometers_fuel_con = i.getOne_hundred_kilometers_fuel_con();
			if (!Checker.isEmpty(one_hundred_kilometers_fuel_con)) {
				insertSql1 += " ,one_hundred_kilometers_fuel_con ";
				insertSql2 += " ," + one_hundred_kilometers_fuel_con + " ";
				updateSql += " ,one_hundred_kilometers_fuel_con =" + one_hundred_kilometers_fuel_con + " ";
			}
			log.debug("获得百公里油耗"+one_hundred_kilometers_fuel_con);
			
			
			String maintenance_mileage = i.getMaintenance_mileage();
			if (!Checker.isEmpty(maintenance_mileage)) {
				insertSql1 += " ,maintenance_mileage ";
				insertSql2 += " ," + maintenance_mileage + " ";
				updateSql += " ,maintenance_mileage =" + maintenance_mileage + " ";
			}
			log.debug("获得保养里程"+maintenance_mileage);
			
			
			String engine_number = i.getEngine_number();
			if (!Checker.isEmpty(engine_number)) {
				insertSql1 += " ,engine_number ";
				insertSql2 += " ,'" + engine_number + "' ";
				updateSql += " ,engine_number ='" + engine_number + "' ";
			}
			log.debug("获得发动机号"+engine_number);
			
			
			String annual_inspection_interval = i.getAnnual_inspection_interval();
			if (!Checker.isEmpty(annual_inspection_interval)) {	
				insertSql1 += " ,annual_inspection_interval ";
				insertSql2 += " ," + annual_inspection_interval + " ";
				updateSql += " ,annual_inspection_interval =" + annual_inspection_interval + " ";
			}else{
				annual_inspection_interval = "1.0";
				insertSql1 += " ,annual_inspection_interval ";
				insertSql2 += " ," + annual_inspection_interval + " ";
				updateSql += " ,annual_inspection_interval =" + annual_inspection_interval + " ";
			}
			log.debug("获得年检间隔时间"+annual_inspection_interval);
			
			
			String reminder_mileage = i.getReminder_mileage();
			if (!Checker.isEmpty(reminder_mileage)) {
				insertSql1 += " ,reminder_mileage ";
				insertSql2 += " ," + reminder_mileage + " ";
				updateSql += " ,reminder_mileage =" + reminder_mileage + " ";
			}
			log.debug("获得保养提醒里程"+reminder_mileage);
			
			
			String frame_number = i.getFrame_number();
			if (!Checker.isEmpty(frame_number)) {
				insertSql1 += " ,frame_number ";
				insertSql2 += " ,'" + frame_number + "' ";
				updateSql += " ,frame_number ='" + frame_number + "' ";
			}
			log.debug("获得车架号(必填)"+frame_number);
			
			
			
			String vehicle_state = i.getVehicle_state();
//			  //设计要求新增 导入默认运行状态为8（离线） 
//			     vehicle_state = "8";
//			     insertSql1 += " ,vehicle_state ";
//				insertSql2 += " ,'" + vehicle_state + "' ";
//				updateSql += " ,vehicle_state ='" + vehicle_state + "' ";
			if (!Checker.isEmpty(vehicle_state)) {
			   log.debug("获得车辆状态"+vehicle_state);
//			     vehicle_state = "8";
			     //vehicle_state = "";
			if (vehicle_state.equals("正常使用中")) {
				vehicle_state = "1";
			}
			if (vehicle_state.equals("车辆维修中")) {
				vehicle_state = "2";
			}
			if (vehicle_state.equals("终端维修中")) {
				vehicle_state = "3";
			}
			if (vehicle_state.equals("车辆已报废")) {
				vehicle_state = "4";
			}
			if (vehicle_state.equals("停车")) {
				vehicle_state = "5";
			}
			if (vehicle_state.equals("怠速")) {
				vehicle_state = "6";
			}
			if (vehicle_state.equals("报警")) {
				vehicle_state = "7";
			}
			if (vehicle_state.equals("离线")) {
				vehicle_state = "8";
			}
			  insertSql1 += " ,vehicle_state ";
				insertSql2 += " ,'" + vehicle_state + "' ";
				updateSql += " ,vehicle_state ='" + vehicle_state + "' ";
			}
			
			System.err.println(666);
			String annual_inspection_time = i.getAnnual_inspection_time();
			System.err.println(!Checker.isEmpty(annual_inspection_time));
			if (!Checker.isEmpty(annual_inspection_time)) {
				insertSql1 += " ,annual_inspection_time ";
				insertSql2 += " ,'" +HSSFDateUtil.getJavaDate(Double.valueOf(annual_inspection_time)) + "' ";
				updateSql += " ,annual_inspection_time ='" + HSSFDateUtil.getJavaDate(Double.valueOf(annual_inspection_time)) + "' ";
				log.debug("获得上次年检时间" + HSSFDateUtil.getJavaDate(Double.valueOf(annual_inspection_time)));
			}

			
			String equipment_model = i.getEngine_number();
			if (!Checker.isEmpty(equipment_model)) {
				insertSql1 += " ,equipment_model ";
				insertSql2 += " ,'" + equipment_model + "' ";
				updateSql += " ,equipment_model ='" + equipment_model + "' ";
			}
			log.debug("获得设备型号"+equipment_model);
			
			
			String vehicle_purchase_date = i.getVehicle_purchase_date();
			if (!Checker.isEmpty(vehicle_purchase_date)) {
				
				insertSql1 += " ,vehicle_purchase_date ";
				insertSql2 += " ,'" + HSSFDateUtil.getJavaDate(Double.valueOf(vehicle_purchase_date)) + "' ";
				updateSql += " ,vehicle_purchase_date ='" + HSSFDateUtil.getJavaDate(Double.valueOf(vehicle_purchase_date)) + "' ";
				log.debug("获得车辆购买日期"+HSSFDateUtil.getJavaDate(Double.valueOf(vehicle_purchase_date)));
			}
			
			
			String car_start_time_hour = i.getCar_start_time_hour();
			if (!Checker.isEmpty(car_start_time_hour)) {
				insertSql1 += " ,car_start_time_hour ";
				insertSql2 += " ," + car_start_time_hour + " ";
				updateSql += " ,car_start_time_hour =" + car_start_time_hour + " ";
			}
			log.debug("获得用车开始时间(时)"+car_start_time_hour);
			
			
			String car_start_time_min = i.getCar_start_time_min();
			if (!Checker.isEmpty(car_start_time_min)) {
				insertSql1 += " ,car_start_time_min ";
				insertSql2 += " ," + car_start_time_min + " ";
				updateSql += " ,car_start_time_min =" + car_start_time_min + " ";
			}
			log.debug("获得用车开始时间(分)"+car_start_time_min);
			
			
			String car_stop_time_hour = i.getCar_stop_time_hour();
			if (!Checker.isEmpty(car_stop_time_hour)) {
				insertSql1 += " ,car_stop_time_hour ";
				insertSql2 += " ," + car_stop_time_hour + " ";
				updateSql += " ,car_stop_time_hour =" + car_stop_time_hour + " ";
			}
			log.debug("获得用车结束时间(时)"+car_stop_time_hour);
			
			
			String car_stop_time_min = i.getCar_stop_time_min();
			if (!Checker.isEmpty(car_stop_time_min)) {
				insertSql1 += " ,car_stop_time_min ";
				insertSql2 += " ," + car_stop_time_min + " ";
				updateSql += " ,car_stop_time_min =" + car_stop_time_min + " ";
			}	
			log.debug("获得用车结束时间(分)"+car_stop_time_min);
			
			String car_statr_time ="";
			
			
//			if(!car_start_time_hour.equals("00") && !car_start_time_min.equals("00")){
//				log.debug("进入第一个if");
//				car_statr_time="'"+car_start_time_hour +"':'"+car_start_time_min+"'";
//			}
//			
//			if(car_start_time_hour.equals("00") && !car_start_time_min.equals("00")){
//				log.debug("进入第二个if");
//				car_statr_time=" 00 : '"+car_start_time_min+"' ";
//			}
//			if(!car_start_time_hour.equals("00") && car_start_time_min.equals("00")){
//				log.debug("进入第三个if");
//				car_statr_time=" "+car_start_time_min+":00  ";
//			}
			
			if(!Checker.isEmpty(car_start_time_hour) && !Checker.isEmpty(car_start_time_min)){
				log.debug("进入第四个if");
				car_statr_time="'"+car_start_time_hour +":"+car_start_time_min+"'";
			}
			
			if(Checker.isEmpty(car_start_time_hour) && !Checker.isEmpty(car_start_time_min)){
				log.debug("进入第五个if");
				car_statr_time=" '00 : "+car_start_time_min+"' ";
			}
			if(!Checker.isEmpty(car_start_time_hour) && Checker.isEmpty(car_start_time_min)){
				log.debug("进入第六个if");
				car_statr_time=" '"+car_start_time_hour+":00  '";
			}
			else {
				car_statr_time="";
			}
			
			if(!Checker.isEmpty(car_statr_time)){
				insertSql1 += " ,'car_statr_time ' ";
				insertSql2 += " ,  "+ car_statr_time +" ";
				updateSql += " ,car_statr_time =" + car_statr_time + " ";
			}
//			else{
//				car_statr_time="";
//				insertSql1 += " ,'car_statr_time ' ";
//				insertSql2 += " ,  "+ car_statr_time +" ";
//				updateSql += " ,car_statr_time ='" + car_statr_time + "' ";
//			}
			
			
			String car_stop_time = "";
			if(!Checker.isEmpty(car_stop_time_hour) && !Checker.isEmpty(car_stop_time_min)){
				log.debug("进入第四个if");
				car_stop_time="'"+car_stop_time_hour +":"+car_stop_time_min+"'";
			}
			
			if(Checker.isEmpty(car_stop_time_hour) && !Checker.isEmpty(car_stop_time_min)){
				log.debug("进入第五个if");
				car_stop_time=" '00 : "+car_stop_time_min+"' ";
			}
			if(!Checker.isEmpty(car_stop_time_hour) && Checker.isEmpty(car_stop_time_min)){
				log.debug("进入第六个if");
				car_stop_time=" '"+car_stop_time_hour+":00  '";
			}
			else {
				car_stop_time="";
			}
			
			//String car_stop_time = "'"+car_stop_time_hour + ":" + car_stop_time_min+"'";
			
			if(!Checker.isEmpty(car_stop_time)){
				log.info("进入if方法"+car_stop_time);
				insertSql1 += " ,car_stop_time  ";
				insertSql2 += " ,  "+ car_stop_time +" ";
				updateSql += " ,car_stop_time =" + car_stop_time + " ";
			}
//			else{
//				log.info("进入else方法"+car_stop_time);
//				car_stop_time="";
//				insertSql1 += " ,'car_stop_time ' ";
//				insertSql2 += " ,  "+ car_stop_time +" ";
//				updateSql += " ,car_stop_time ='" + car_stop_time + "' ";
//			}
			
			
			String device_sn_number = i.getDevice_sn_number();
			if (!Checker.isEmpty(device_sn_number)) {
				insertSql1 += " ,device_sn_number ";
				insertSql2 += " ,'" + device_sn_number + "' ";
				updateSql += " ,device_sn_number ='" + device_sn_number + "' ";
			}
			log.debug("获得车辆终端编号(必填)"+device_sn_number);
			
			
			String installation_date_of_equipment = i.getInstallation_date_of_equipment();
			if (!Checker.isEmpty(installation_date_of_equipment)) {
				
				insertSql1 += " ,installation_date_of_equipment ";
				insertSql2 += " ,'" + HSSFDateUtil.getJavaDate(Double.valueOf(installation_date_of_equipment)) + "' ";
				updateSql += " ,installation_date_of_equipment ='" + HSSFDateUtil.getJavaDate(Double.valueOf(installation_date_of_equipment)) + "'  ";
				log.debug("获得设备安装日期");
			}
			
			String orgcode = ac.getVisitor().getUser().getOrgId();
			if(!Checker.isEmpty(orgcode)){
				insertSql1 += " ,orgcode ";
				insertSql2 += " ,'" + orgcode + "' ";
			}
			
			insertSql1 += " ) ";
			insertSql2 += " )  ";
			sumInsertSql = insertSql1 + insertSql2;
			updateSql += " where device_sn_number='" + device_sn_number + "' and  license_plate_number='"+license_plate_number+"' and frame_number='"+frame_number+"'";

			boolean b = true;
			System.err.println(Checker.isEmpty(license_plate_number));
			if (import_rule.equals("1")) {
				if (Checker.isEmpty(license_plate_number)) {
					System.err.println("进入判断");
					ac.getActionResult().addErrorMessage("车牌号码不能为空");
					ac.getActionResult().setSuccessful(false);		
					b = false;
				}
			}

			
			if (import_rule.equals("1")) {
				if (Checker.isEmpty(device_sn_number)) {
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("终端编号不能为空");
					b = false;
				}
			}
			
			if (import_rule.equals("1")) {
				if (Checker.isEmpty(frame_number)) {
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("车架号不能为空");
					b = false;
				}
			}
			
			
			String checkSql = "select count(*) from cdms_VehicleBasicInformation where license_plate_number='"
					+ license_plate_number + "'" + " and device_sn_number='" + device_sn_number + "'"
					+ " and frame_number='" + frame_number + "' ";
			int count5 = 0;

			if (b) {
				MapList mapList = db.query(checkSql);
				count5 = mapList.getRow(0).getInt(0, 0);
			}else{	
				continue;		
			}
			
			
			
			System.err.println("count5"+count5);
			System.err.println(checkSql);
			System.err.println(checkSql);
			System.err.println(checkSql);
			
			
			
			

			if (!Checker.isEmpty(license_plate_number) && !Checker.isEmpty(frame_number) && !Checker.isEmpty(device_sn_number)) {
				MapList mapList = db.query(checkSql);
				count5 = mapList.getRow(0).getInt(0, 0);
				// 判断导入规则 1=重复数据覆盖 2=不导入重复数据
				if (import_rule.equals("1")) {
					if (count5 <= 0) {				
						count1 += db.execute(sumInsertSql);
						System.err.println(count1);
					} else {				
						// 更新行数
						count2 += db.execute(updateSql);
					}
				} else {
					if (count5 <= 0) {			
						// 新增行数
						count1 += db.execute(sumInsertSql);
					} else {
						errorMsg += "失败数据：车辆终端号" + device_sn_number + "已存在" + "<br>";
					}
				}
				// int number=db.execute(sumInsertSql);
			}
		}
		logger.debug("for循环结束");

		count = count1 + count2;
		String msg1 = "共增加行数:" + count1 + "行!";
		String msg2 = "共更新行数:" + count2 + "行!";
		msg = "操作成功!共影响行数:" + count + "行!";

		ac.getActionResult().setSuccessful(true);
		ac.getActionResult().addSuccessMessage(msg1);
		ac.getActionResult().addSuccessMessage(msg2);
		ac.getActionResult().addSuccessMessage(msg);
		ac.getActionResult().addErrorMessage(errorMsg);
		ac.getActionResult().setUrl("/cdms/" + menuname + ".do?m=s");

		return ac;
	}

}
