package com.am.frame.other.taskInterface.impl.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 	<identityInfo>
		<corpCode>TESTFX</corpCode>企业码
		<userName>admin</userName>用户名 
	</identityInfo>
  
 */
@XmlRootElement(name="identityInfo")
public class IdentityInfo {
	private String corpCode="CESHI";
	private String userName="otaceshi";
	
	/**
	 * 有默热值
	 * <identityInfo>
		<corpCode>TESTFX</corpCode>企业码
		<userName>admin</userName>用户名 
	</identityInfo>
	 */
	public IdentityInfo(){}
	
	/**
	 * 企业码
	 * @return
	 */
	@XmlElement(name="corpCode")
	public String getCorpCode() {
		return corpCode;
	}
	
	/**
	 * 企业码
	 * @param corpCode
	 */
	public void setCorpCode(String corpCode) {
		this.corpCode = corpCode;
	}
	
	/**
	 * 用户名 
	 * @return
	 */
	@XmlElement(name="userName")
	public String getUserName() {
		return userName;
	}
	
	/**
	 * 用户名 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
