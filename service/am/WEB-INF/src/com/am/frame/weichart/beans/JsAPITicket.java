package com.am.frame.weichart.beans;

import org.json.JSONObject;

/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 
 * 说明:<br />
 * JsApiTicket
 */
public class JsAPITicket {
	
	private String errcode;
	private String errmsg;
	private String ticket;
	private long expires_in=7200;
	private long startTime;
	private String token;
	private JSONObject jsapi;
	private boolean isEnale=false;;
	
	public JsAPITicket(String token, JSONObject result){
		this.token=token;
		this.jsapi=result;
		try{
			/**
			 * "errcode":0,
			 * "errmsg":"ok",
			 * "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
			 * "expires_in":7200
			 */
			this.errcode=jsapi.getString("errcode");
			this.errmsg=jsapi.getString("errmsg");
			if("ok".equalsIgnoreCase(errmsg)){
				this.ticket=jsapi.getString("ticket");
				this.expires_in=jsapi.getLong("expires_in");
				isEnale=true;
				startTime=System.currentTimeMillis();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 是否有效
	 * @return 有效返回true，无效返回false
	 */
	public boolean isEnable(){
		if(isEnale&&System.currentTimeMillis()-startTime<this.expires_in*1000){
			isEnale=true;
		}else{
			isEnale=false;
		}
		return isEnale;
	}
	
	public String getErrcode() {
		return errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public String getTicket() {
		return ticket;
	}

	public long getExpires_in() {
		return expires_in;
	}

	public long getStartTime() {
		return startTime;
	}

	public String getToken() {
		return token;
	}

	public JSONObject getJsapi() {
		return jsapi;
	}
	
}
