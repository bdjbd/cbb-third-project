package com.cdms.carExcelData.pojo;
/**
 * 
 * 车辆基础信息导入类
 */
/**
 * 
 * 刘扬
 * 车辆基础信息表Excel导入功能
 */
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

public class VehicleBasicInformation {
	
	//@Excel(name="id")
	//private String id; //id
	@Excel(name="车牌号码")
	private String license_plate_number; //车牌号码
	
	@Excel(name="SIM卡号")
	private String  sim_card_number;	 //SIM卡号
	
	@Excel(name="百公里油耗")
	private String one_hundred_kilometers_fuel_con; //百公里油耗
	
	@Excel(name="保养里程")
	private String maintenance_mileage;  //保养里程
	
	@Excel(name="发动机号")
	private String engine_number; //发动机号
	
	//@Excel(name="所属机构")
	//private String orgcode;  //所属机构
	
	@Excel(name="年检间隔时间")
	private String annual_inspection_interval;  //年检间隔时间
	
	@Excel(name="保养提醒里程")
	private String reminder_mileage ;   //保养提醒里程
	
	@Excel(name="车架号")
	private String frame_number;   //车架号
	
	@Excel(name="车辆状态")
	private String  vehicle_state;  //车辆状态
	
	@Excel(name="上次年检时间")
	private String annual_inspection_time;  //上次年检时间
	
	@Excel(name="设备型号")
	private String equipment_model;   //设备型号
	
	@Excel(name="车辆购买日期")
	private String vehicle_purchase_date;  //车辆购买日期
	
	@Excel(name="用车开始时间(时)")
	private String  car_start_time_hour;  //用车开始时间(时)
	
	@Excel(name="用车开始时间(分)")
	private String  car_start_time_min;   //用车开始时间(分)
	
	@Excel(name="用车结束时间(时)")
	private String car_stop_time_hour;  //用车结束时间(时)
	
	@Excel(name="用车结束时间(分)")
	private String car_stop_time_min;   //用车结束时间(分)
	
	@Excel(name="终端编号")
	private String device_sn_number; //终端编号
	
	@Excel(name="设备安装日期")
	private String installation_date_of_equipment; //设备安装日期
	
	
	
	
	
	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	public String getLicense_plate_number() {
		return license_plate_number;
	}
	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	public String getSim_card_number() {
		return sim_card_number;
	}
	public void setSim_card_number(String sim_card_number) {
		this.sim_card_number = sim_card_number;
	}
	public String getOne_hundred_kilometers_fuel_con() {
		return one_hundred_kilometers_fuel_con;
	}
	public void setOne_hundred_kilometers_fuel_con(String one_hundred_kilometers_fuel_con) {
		this.one_hundred_kilometers_fuel_con = one_hundred_kilometers_fuel_con;
	}
	public String getMaintenance_mileage() {
		return maintenance_mileage;
	}
	public void setMaintenance_mileage(String maintenance_mileage) {
		this.maintenance_mileage = maintenance_mileage;
	}
	public String getEngine_number() {
		return engine_number;
	}
	public void setEngine_number(String engine_number) {
		this.engine_number = engine_number;
	}
//	public String getOrgcode() {
//		return orgcode;
//	}
//	public void setOrgcode(String orgcode) {
//		this.orgcode = orgcode;
//	}
	public String getAnnual_inspection_interval() {
		return annual_inspection_interval;
	}
	public void setAnnual_inspection_interval(String annual_inspection_interval) {
		this.annual_inspection_interval = annual_inspection_interval;
	}
	public String getReminder_mileage() {
		return reminder_mileage;
	}
	public void setReminder_mileage(String reminder_mileage) {
		this.reminder_mileage = reminder_mileage;
	}
	public String getFrame_number() {
		return frame_number;
	}
	public void setFrame_number(String frame_number) {
		this.frame_number = frame_number;
	}
	public String getVehicle_state() {
		return vehicle_state;
	}
	public void setVehicle_state(String vehicle_state) {
		this.vehicle_state = vehicle_state;
	}
	public String getAnnual_inspection_time() {
		return annual_inspection_time;
	}
	public void setAnnual_inspection_time(String annual_inspection_time) {
		this.annual_inspection_time = annual_inspection_time;
	}
	public String getEquipment_model() {
		return equipment_model;
	}
	public void setEquipment_model(String equipment_model) {
		this.equipment_model = equipment_model;
	}
	public String getVehicle_purchase_date() {
		return vehicle_purchase_date;
	}
	public void setVehicle_purchase_date(String vehicle_purchase_date) {
		this.vehicle_purchase_date = vehicle_purchase_date;
	}
	public String getCar_start_time_hour() {
		return car_start_time_hour;
	}
	public void setCar_start_time_hour(String car_start_time_hour) {
		this.car_start_time_hour = car_start_time_hour;
	}
	public String getCar_start_time_min() {
		return car_start_time_min;
	}
	public void setCar_start_time_min(String car_start_time_min) {
		this.car_start_time_min = car_start_time_min;
	}
	public String getCar_stop_time_hour() {
		return car_stop_time_hour;
	}
	public void setCar_stop_time_hour(String car_stop_time_hour) {
		this.car_stop_time_hour = car_stop_time_hour;
	}
	public String getCar_stop_time_min() {
		return car_stop_time_min;
	}
	public void setCar_stop_time_min(String car_stop_time_min) {
		this.car_stop_time_min = car_stop_time_min;
	}
	public String getDevice_sn_number() {
		return device_sn_number;
	}
	public void setDevice_sn_number(String device_sn_number) {
		this.device_sn_number = device_sn_number;
	}
	public String getInstallation_date_of_equipment() {
		return installation_date_of_equipment;
	}
	public void setInstallation_date_of_equipment(String installation_date_of_equipment) {
		this.installation_date_of_equipment = installation_date_of_equipment;
	}
	
	

	


}
