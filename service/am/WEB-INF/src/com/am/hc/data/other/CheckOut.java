package com.am.hc.data.other;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.am.sdk.md5.MD5Util;
import com.fastunit.MapList;
/**
 * 白名单及校验码检查类
 * @author guorenjie
 * 2017-06-03
 */
public class CheckOut {
	private static final Logger log = LoggerFactory.getLogger(CheckOut.class);
	
	public boolean check(HttpServletRequest request)
	{
		boolean rValue=false;
		String account = request.getParameter("account");//账号
		String password = request.getParameter("password");//密码
		String data = request.getParameter("data");
		String token = request.getParameter("token");//校验码		
		if(checkWhite(account,password)&& checkJym(account,password,data,token))
		{
			rValue=true;
		}
		
		
		return rValue;
	}
	/**
	 * 检查校验码
	 * @param request
	 * @return
	 */
	private boolean checkJym(String account,String password,String data,String token)
	{
		boolean rValue=false;		
		DBManager db = new DBManager();
		String sql = "select private_key from AM_OTHERSYSTEMWHITE where account='" + account + "'";
		
		MapList mapList = db.query(sql);
		
		if(mapList.size()>0)
		{
			String private_key = mapList.getRow(0).get("private_key");//白名单表中的私key
			String str = account+password+data+private_key;
			//完整参数+私keymd5加密
			String sign = MD5Util.getSingleton().textToMD5L32(str);
			if (sign.equals(token)) {
				rValue = true;
			}
		}
		
		return rValue;
	}


	/**
	 * 检查白名单
	 * 
	 * @param request
	 */
	private boolean checkWhite(String account,String password) 
	{
		boolean rValue=false;		
		DBManager db = new DBManager();
		String sql = "select password from AM_OTHERSYSTEMWHITE where account='" + account + "'";
		
		MapList mapList = db.query(sql);
		if(mapList.size()>0)
		{
			String password1 = mapList.getRow(0).get("password");//白名单表中的密码			
			String sign = MD5Util.getSingleton().textToMD5L32(password1);//md5加密
			if (sign.equals(password)) {
				rValue = true;
			}
		}
		
		return rValue;
	}
	

}
