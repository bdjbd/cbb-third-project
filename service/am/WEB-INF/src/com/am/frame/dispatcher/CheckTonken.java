package com.am.frame.dispatcher;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.sdk.md5.MD5Util;
import com.fastunit.util.Checker;

/**
 * 校验tonken
 * @author xinalin
 *2016-10-11
 */

public class CheckTonken {
	
	private static final Logger log = LoggerFactory.getLogger(CheckTonken.class);

	private volatile static CheckTonken singleton;  
	private CheckTonken (){}   
	public static CheckTonken getSingleton() {  
		if (singleton == null) {  
			synchronized (CheckTonken.class) {  
				if (singleton == null) {  
					singleton = new CheckTonken();  
				}  
			}  
		}  
		return singleton;  
	}  
	
	
	/**
	 * 处理tonken
	 * @param request
	 */
	public boolean tonken(HttpServletRequest request,String actionName)
	{
		boolean result = false;
		
		int net_count = Integer.parseInt(PropertiesUtil.getPropertiesUtil().getValue("Network_White_count"))-1;
		
		String networkWhite_str = "";
		//获取网络请求白名单
		for (int i = 0; i <= net_count; i++) {
			networkWhite_str += PropertiesUtil.getPropertiesUtil().getValue("Network_White_"+i)+",";
		}
		
		//判断是否满足网络白名单配置
		if(PropertiesUtil.getPropertiesUtil().getValue("Tonken_API").equals(actionName))
		{
			result = true;
		}else if(networkWhite_str.indexOf(actionName)!=-1)
		{
			log.info("白名单actionName："+actionName);
			result = true;
			
		}
		else
		{
			if(Checker.isEmpty(request.getParameter("serialization")) || Checker.isEmpty(request.getParameter("platform_sign")))
			{
				result = false;
			}else
			{
				log.info("验证签名actionName："+actionName);
				//获取参数签名信息
				String str = "";
				String secretKey = (String) request.getSession().getAttribute("platform_secretKey");
				//base64转换请求参数key序列化
				byte[] serialization_decode = Base64.decodeBase64(request.getParameter("serialization").getBytes());
				
				String[] serialization = null;
				try {
					serialization = new String(serialization_decode,"UTF-8").split(",");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
				//参数拼接
				if(!Checker.isEmpty(secretKey))
				{
					
					for(int i=0;i<serialization.length;i++)
					{
						if(!serialization[i].equals("platform_sign"))
						{
							str +=request.getParameter(serialization[i])+"@";
						}
					}
					
					str = str+"@"+secretKey;
					
					System.out.println("值:"+str);
					
					
					byte[] srtbyte = null;
					try {
						srtbyte = str.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//参数拼接完成后base64加密
					String params = Base64.encodeBase64String(srtbyte);
					//md5签名
					String sign = MD5Util.getSingleton().textToMD5L32(params);
					
					log.info("sign签名信息:"+sign);
					//判断签名是否一致，如不一致则接口调用失败，反之成功调用
					if(sign.equals(request.getParameter("platform_sign")))
					{
						result = true;
					}
				}
			}
			//判断是否为开发环境，如果为开发环境不做接口校验，反之生产环境进行接口校验
			if("1".equals(PropertiesUtil.getPropertiesUtil().getValue("IS_Development")))
			{
				result = true;
			}
		}
		log.info("resultfffff:"+result);
		return result;
	}
}
