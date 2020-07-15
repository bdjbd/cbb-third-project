package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.4
 *修改日期：2016-03-08
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，
	//查看地址：https://b.alipay.com/order/pidAndKey.htm
	//理性农业 2088421377935311
	public static String partner = "2088721691303534";
	
	// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
	public static String seller_id = partner;

	//商户的私钥,需要PKCS8格式，
	//RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
	public static String private_key =
			"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANn2Ku9Uq2C+SKMD"
			+"MGDX3TvyWY3pjYkNtB8IIohe7u2ZFBSguNqwN5rDMB81mOKdYYYQhSDzgr+OSw0E"
			+"KaT36k1p9W59dWk2q8Im1jYzwCpnW5fC7y1GBVjm+y90UzTDYvAizBRoZzLr3h99"
			+"O1qePFJ+wyiXa2fZ0oWJMeL/gWr5AgMBAAECgYAuf2qHZhutZsqeJAcFEef7aucZ"
			+"9DIig87iM90SFJaKD3cOM39b/+3M0UkJz2hPJ93vOMT905UYAmYWX8X2kIDmICPD"
			+"tD3H9Y0YjkFzbTz7uVF5N/0+m2M/zh8JEpBp+baWLUC2ttbOB9qGYD+hV7QJWSyN"
			+"yHhQpWu3afjXNkdGlQJBAO4KEIxKWQtIiG1ZyhCQzjh+NZB4zzdrs1btoctchpFB"
			+"0hkyEauAJvdVw6jfHGDbRRwOQ5OWUQGBO0dR7DtzfXMCQQDqaEjUcsgvQAfXE2YZ"
			+"xA+Vnru6MKqex7sbglWnw7ZXeP3jGLO8ar0ZDVNpK5AU9fPuFqMdZGK14xTxUnAu"
			+"VprjAkEAjIfumxRtm2a3/G7tB3il9t4z+1YHZ/2yhBiI04mNfIYyZmOT4P8ogg7l"
			+"E3acx2XMWDLnnGM68bt85q8r2zFklwJAfMjXPIF+H+kuQ0iIad79jODbftq5eZ1J"
			+"9fMENjLxts8qGOLli8Amuldlt+8A3KeyG2ThoJv4EGu6kl0YaxKfkQJAMDYTqPOc"
			+"C5seAg1X+oM6Dd//5LdSzbwrVa1neBQITh5niih4Jc2y9p7uZ6AAHrFgfXWoBsY7"
			+"OiHnQrkfvhTGAg==";
		
			
	// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
//	public static String alipay_public_key  ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	public static String alipay_public_key  ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://vcommuntiy.com/AmRes/com.am.frame.payment.impl.webApi.AlipayPaymenNotifyWebApi.do";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://vcommuntiy.com/AmRes/com.am.frame.payment.impl.webApi.AlipayPaymenNotifyWebApi.do";
	
	//手机网页支付异步通知
	public static String wapNotify_url="http://vcommuntiy.com/AmRes/com.am.frame.payment.impl.webApi.WapAlipayPaymentNotifyWebApi.do";
	//手机网页支付同步通知  这个是返回页面，同步显示个用户看的
	public static String wapReturn_url="http://vcommuntiy.com/alipay/wap/call_back_show.jsp";
	
	// 签名方式
	public static String sign_type = "RSA";
	
	// 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
	public static String log_path = "D:\\";
		
	// 字符编码格式 目前支持utf-8
	public static String input_charset = "utf-8";
		
	// 支付类型 ，无需修改
	public static String payment_type = "1";
		
	// 调用的接口名，无需修改
	public static String service ="alipay.wap.create.direct.pay.by.user";
	
	//web端，即使到账service设置
	public static String WebSercie="create_direct_pay_by_user";


}

