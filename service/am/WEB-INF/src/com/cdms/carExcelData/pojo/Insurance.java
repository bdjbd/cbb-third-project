package com.cdms.carExcelData.pojo;
/**
 * 
 * 保险导入实体类
 */
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

public class Insurance {
	
	
	@Excel(name="车牌号码")
	private String license_plate_number;
	
	@Excel(name="保险费金额")
	private String insurance_amount;
	
	@Excel(name="保险缴纳时间")
	private String insurance_payment_time;
	
	@Excel(name="保险到期时间")
	private String term_insurance_time;
	
	@Excel(name="备注")
	private String notes;
	
	
	
	
	public String getInsurance_amount() {
		return insurance_amount;
	}

	public String getTerm_insurance_time() {
		return term_insurance_time;
	}

	public void setTerm_insurance_time(String term_insurance_time) {
		this.term_insurance_time = term_insurance_time;
	}

	public void setInsurance_amount(String insurance_amount) {
		this.insurance_amount = insurance_amount;
	}

	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getLicense_plate_number() {
		return license_plate_number;
	}

	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	
	public String getInsurance_payment_time() {
		return insurance_payment_time;
	}

	public void setInsurance_payment_time(String insurance_payment_time) {
		this.insurance_payment_time = insurance_payment_time;
	}
}
