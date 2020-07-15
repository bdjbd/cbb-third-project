package com.cdms.carExcelData.pojo;
/**
 * 
 *出险工具实体类
 */
import java.util.Date;

import org.jeecgframework.poi.excel.annotation.Excel;

public class DangerousTools {
		
	
	@Excel(name="车牌号码")
	private String license_plate_number;
	@Excel(name="工具名称")
	private String tool_name;
	@Excel(name="工具数量")
	private String  tool_number;
	@Excel(name="领取时间")
	private String receive_time;
	@Excel(name="负责人")
	private String principal;
	@Excel(name="变更记录")
	private String change_log;
	
	
	
	public String getLicense_plate_number() {
		return license_plate_number;
	}
	public void setLicense_plate_number(String license_plate_number) {
		this.license_plate_number = license_plate_number;
	}
	public String getTool_name() {
		return tool_name;
	}
	public void setTool_name(String tool_name) {
		this.tool_name = tool_name;
	}
	public String getTool_number() {
		return tool_number;
	}
	public void setTool_number(String tool_number) {
		this.tool_number = tool_number;
	}
	public String getReceive_time() {
		return receive_time;
	}
	public void setReceive_time(String receive_time) {
		this.receive_time = receive_time;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getChange_log() {
		return change_log;
	}
	public void setChange_log(String change_log) {
		this.change_log = change_log;
	}
}
