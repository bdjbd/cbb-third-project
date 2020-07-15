package com.wisdeem.wwd.WeChat.beans;
/**
 *   说明:
 *   AccessToken bean 对象
 *   @creator	岳斌
 *   @create 	Dec 10, 2013 
 *   @version	$Id
 */
public class AccessToken {
	private long liveTime=0;
	private String accessToken;
	private String token;
	/**
	 * 构造器
	 * @param acToken accesstoken
	 * @param token   token
	 */
	public AccessToken(String acToken,String token){
		liveTime=System.currentTimeMillis();
		this.accessToken=acToken;
		this.token=token;
	}
	/**
	 * 获取token
	 * @return
	 */
	public String getToken(){
		return this.token;
	}
	/**
	 * 获取accesstoken
	 * @return
	 */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	 * 返回AccessToken是否有效
	 * @return 有效返回true否则返回false
	 */
	public boolean getEnable(){
		return System.currentTimeMillis()-liveTime>6640?false:true;
	}
}
