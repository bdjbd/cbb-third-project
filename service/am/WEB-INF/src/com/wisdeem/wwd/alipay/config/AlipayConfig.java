package com.wisdeem.wwd.alipay.config;

import java.io.IOException;
import java.util.Properties;

/**
 * 功能：基础配置类
 * 详细：设置帐户有关信息及返回路径
 * 日期：2013-12-16
 * @author liyushuang
 * */

public class AlipayConfig {
	//接口名称 ***
	public static String service="";
	//合作者身份ID  ***
	public static String partner="";
	//商户的私钥
	public static String key="";
	//日志文件路径
	public static String log_path = "";
	//字符编码格式， 目前支持 gbk 或 utf-8   ***
	public static String _input_charset = "";
	//签名方式    ***
	public static String sign_type = "";
	//支付宝服务器主动通知商户网站里指定的页面http路径。
	public static String notify_url = "";
	//支付宝处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径。
	public static String return_url = "";
	//public static String return_url = "http://www.wisdeem.cn/domain/wwd/apply/return_url.jsp";
	//卖家支付宝帐号
	public static String seller_email="";
	//支付类型 , 默认值为：1（商品购买）。
	public static String payment_type="";
	
	static{
		try {
			Properties proper=new Properties();
			proper.load(AlipayConfig.class.getClassLoader().getResourceAsStream("ApliayConfig.properties"));
			service=proper.getProperty("service");
			partner=proper.getProperty("partner");
			key=proper.getProperty("key");
			log_path=proper.getProperty("log_path");
			_input_charset=proper.getProperty("_input_charset");
			sign_type=proper.getProperty("sign_type");
			notify_url=proper.getProperty("notify_url");
			return_url=proper.getProperty("return_url");
			seller_email=proper.getProperty("seller_email");
			payment_type=proper.getProperty("payment_type");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("初始化支付模块出错！");
		}
	}
}
