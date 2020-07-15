package com.wisdeem.wwd.WeChat.beans;

import java.io.Serializable;

/**
 *   说明:
 * 	 公众帐号访问者
 *   @creator	岳斌
 *   @create 	Feb 15, 2014 
 *   @version	$Id
 */
public class Interviewer implements Serializable {
	private static final long serialVersionUID = 1L;
	/**帐号机构ID**/
	private String orgid;
	/**帐号机构名称**/
	private String orgname;
	/**访问者OPENID**/
	private String openid;
	/**当前访问者帐号类型，运营商提供，企业私有**/
	private String accountType;
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
}
