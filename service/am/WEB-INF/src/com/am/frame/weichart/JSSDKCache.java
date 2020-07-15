package com.am.frame.weichart;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.weichart.beans.JsAPITicket;


/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 
 * 说明:<br />
 * JS jsapi_ticket cache
 */
public class JSSDKCache {
	
	//有效期7200秒
	private String jsapiTicket;
	private static JSSDKCache jsSDKCache;
	private long startTime;
	
	private JSSDKCache(){};
	
	/**jsAPiTicker cache**/
	private Map<String,JsAPITicket> cache=new HashMap<String,JsAPITicket>();
	
	public static JSSDKCache getInstance(){
		if(jsSDKCache==null){
			jsSDKCache=new JSSDKCache();
		}
		return jsSDKCache;
	}

	public void cacheJSApiTicket(JsAPITicket ticket) {
		cache.put(ticket.getToken(), ticket);
	}

	public JsAPITicket getJSApiTicket(String token) {
		return cache.get(token);
	}
	
}
