package com.cdms.carExcelData.pojo;

import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

public class OilCard {
	
	@Excel(name="车牌号")
	private String license_plate_number;
	@Excel(name="油卡编号")
	private String oil_card_number;
	@Excel(name="加油量")
	private String add_oil;
	@Excel(name="加油时间")
	private String add_oil_time;
	@Excel(name="驾驶员姓名")
	private String driver_name;
	
	
	
	
	public String getLicense_plate_number() {
		return license_plate_number;
	}
	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	public String getOil_card_number() {
		return oil_card_number;
	}
	public void setOil_card_number(String oil_card_number) {
		this.oil_card_number = oil_card_number;
	}
	public String getAdd_oil() {
		return add_oil;
	}
	public void setAdd_oil(String add_oil) {
		this.add_oil = add_oil;
	}
	public String getAdd_oil_time() {
		return add_oil_time;
	}
	public void setAdd_oil_time(String add_oil_time) {
		this.add_oil_time = add_oil_time;
	}
	public String getDriver_name() {
		return driver_name;
	}
	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

}
