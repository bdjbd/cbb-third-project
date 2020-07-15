package com.cdms.carExcelData.pojo;

import org.jeecgframework.poi.excel.annotation.Excel;

public class AmMember {
	
	
	@Excel(name="姓名")
	private String name;
//	@Excel(name="身份证号码")
//	private String identitycardnumber;
	@Excel(name="联系电话")
	private String contact_number;
	@Excel(name="注册账号")
	private String loginaccount;
	@Excel(name="身份识别码")
	private String id_number2;
	@Excel(name="工号")
	private String job_number;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public String getIdentitycardnumber() {
//		return identitycardnumber;
//	}
//	public void setIdentitycardnumber(String identitycardnumber) {
//		this.identitycardnumber = identitycardnumber;
//	}
	public String getContact_number() {
		return contact_number;
	}
	public void setContact_number(String contact_number) {
		this.contact_number = contact_number;
	}
	public String getLoginaccount() {
		return loginaccount;
	}
	public void setLoginaccount(String loginaccount) {
		this.loginaccount = loginaccount;
	}
	public String getId_number2() {
		return id_number2;
	}
	public void setId_number2(String id_number2) {
		this.id_number2 = id_number2;
	}
	public String getJob_number() {
		return job_number;
	}
	public void setJob_number(String job_number) {
		this.job_number = job_number;
	}
}
