package com.am.frame.weichart;
/**
 *   说明:
 * 		微信商店常量类
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */
public class Constant {
	/**微商店访问域名**/
	public static final String WE_SHOP_DOMAIN_NAME="frServlet/mobile";
	/**P2P E快修访问域名**/
	public static final String P2P_DOMAIN_NAME="p2p";
	/**微信公众平台接口URL**///http://www.wisdeem.cn/weshop/wechatinface.do
	public static final String WE_CAHT_INFACE_URL="wechatinface";
	/**认证公众帐号Oauth2.0认证 会员卡请求**/
	public static final String WE_CAHT_AUTHOR_OAUTH2="author_oauth2";
	/**认证公众帐号Oauth2.0认证返回结果处理**/
	public static final String WE_CAHT_OAUTH2_RESULT="author_oauth2_result";
	/**微信接口调用socket连接超时**/
	public static final int WE_CHAT_SOCKET_TIMEOUT=3000;
	/**微信接口调用http协议连接超时时间**/
	public static final int WE_CHAT_CONNECT_TIMEOUT=3000;
	/**统一下单URL**/
	public static final String UNIFIED_ORDER="https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	/**获取Access_tokenURL**/
	public static final String GET_ACCTOKEN_URL=
			"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	public static final String GET_JSAPI_URL=
			"https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
	
	
	
	/**发送客服消息URL**/
	public static final String SEND_MSG_URL="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	
	/**Session 当前机构**/
	public static final String CURRENT_ORG="CURRENT_ORG";
	/**Session 当前会员的会员编号**/
	public static final String CURRENT_MEMBER_INFO="CURRENT_MEMBER_INFO";
	/**用户session保存信息  
	 * 格式:
	 * {"access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token":"REFRESH_TOKEN","openid":"OPENID","scope":"SCOPE"}
	 **/
	public static final String SESSION_USER_INFO_JSON="JSON_USER_INFO";
	/**方瑞到家token**/
	public static final String TOKEN = "FANGRUIDAOJIA_WEICHART_TOKEN";
	
}
