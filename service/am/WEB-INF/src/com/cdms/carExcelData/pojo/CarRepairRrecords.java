package com.cdms.carExcelData.pojo;

import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 
 * 维修导入实体类
 * @author Administrator
 *
 */
public class CarRepairRrecords {
	
	@Excel(name="车牌号")
	private String license_plate_number;
	
	@Excel(name="维修费用")
	private String repair_fees;
	
	@Excel(name="维修日期")
	private String repair_time;

	@Excel(name="维修类型")
	private String repair_type;
	
	@Excel(name="驾驶员姓名")
	private String member_id;

	
	
	
	
	
	public String getLicense_plate_number() {
		return license_plate_number;
	}

	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}

	public String getRepair_fees() {
		return repair_fees;
	}

	public void setRepair_fees(String repair_fees) {
		this.repair_fees = repair_fees;
	}

	public String getRepair_time() {
		return repair_time;
	}

	public void setRepair_time(String repair_time) {
		this.repair_time = repair_time;
	}

	public String getRepair_type() {
		return repair_type;
	}

	public void setRepair_type(String repair_type) {
		this.repair_type = repair_type;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}   


}
