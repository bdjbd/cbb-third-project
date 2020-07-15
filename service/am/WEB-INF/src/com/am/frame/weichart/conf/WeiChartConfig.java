package com.am.frame.weichart.conf;

import java.util.Properties;

import com.fastunit.Var;

/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 
 * 说明:<br />
 * 微信公众号配置
 */
public class WeiChartConfig {

	/**微信公众号配置**/
	private static WeiChartConfig weiConf;
	
	private Properties properties;
	
	private WeiChartConfig(){
	}
	
	public String getAppId(){
		
		return Var.get("weiChartAppId");
	}
	
	public String getAppSecret(){
		return Var.get("weiChartAppSecret");
	}
	
	public static WeiChartConfig getInstance(){
		if(weiConf==null){
			weiConf=new WeiChartConfig();
		}
		return weiConf;
	}
	
	public String getValue(String key){
		return Var.get(key);
	}
	
	public String getValue(String key,String defaultValue){
		if(Var.get(key)!=null){
			return Var.get(key);
		}else{
			return defaultValue;
		}
	}
	
}
