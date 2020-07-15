package com.am.frame.other.taskInterface.impl.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * <header>
		<application>SendCode</application>固定值
		<requestTime>2016-12-20</requestTime>
	</header>
 */
@XmlRootElement(name="PWBHeader")
public class PWBHeader {
	private String application="SendCode";
	private String requestTime;
	
	/***
	 * PWBHeader 两字段 application 默认为SendCode
	 * requestTime 默认为当前时间,格式为yyyy-mm-dd
	 */
	public PWBHeader(){
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-mm-dd");
		requestTime=sf.format(new Date());
	}
	
	@XmlElement(name="application")
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
	@XmlElement(name="requestTime")
	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	
	
}
