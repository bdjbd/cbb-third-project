package com.wisdeem.wwd.WeChat.beans;

import java.io.Serializable;
import java.util.HashMap;

/**
 *   说明:
 *   公众帐号对象
 *   @creator	岳斌
 *   @create 	Feb 15, 2014 
 *   @version	$Id
 */
public class PublicAccount implements Serializable {
	private static final long serialVersionUID = -3516350237623183555L;
	/**机构ID**/
	private String orgind;
	/**机构名称**/
	private String orgname;
	/**机构帐号类型:0或者空，企业自己公众帐号。1，运营商提供公众帐号。**/
	private String accountBelong;
	/**APPID**/
	private String appId;
	/**APPSECRET**/
	private String appSecret;
	/**ISVALID**/
	private String isValid;
	/**PUBLIC_ID**/
	private String publicId;
	/**0,无效 1,选择关注企业**/
	private int replayStatus=0;
	/**菜单ID**/
	private HashMap<String,String> menuId=new HashMap<String,String>();
	/**
	 * 是否清除了当前机构
	 */
	private boolean clear=false;
	
	public String getOrgind() {
		return orgind;
	}
	public void setOrgind(String orgind) {
		if(orgind==null){
			clear=true;
		}else{
			clear=false;
		}
		this.orgind = orgind;
	}
	public String getOrgname() {
		return orgname;
	}
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getIsValid() {
		return isValid;
	}
	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}
	/**
	 * 机构帐号类型:0或者空，企业自己公众帐号。1，运营商提供公众帐号。
	 * @return
	 */
	public String getAccountBelong() {
		return accountBelong;
	}
	public void setAccountBelong(String accountBelong) {
		this.accountBelong = accountBelong;
	}
	public String getPublicId() {
		return publicId;
	}
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}
	public int getReplayStatus() {
		return replayStatus;
	}
	public void setReplayStatus(int replayStatus) {
		this.replayStatus = replayStatus;
	}
	public HashMap<String, String> getMenuId() {
		return menuId;
	}
	public void setMenuId(HashMap<String, String> menuId) {
		this.menuId = menuId;
	}
	public boolean isClear() {
		return clear;
	}
	public void setClear(boolean clear) {
		this.clear = clear;
	}
	
}
