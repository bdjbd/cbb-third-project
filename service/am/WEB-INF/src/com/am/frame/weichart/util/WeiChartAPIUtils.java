package com.am.frame.weichart.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.weichart.AccessToken;
import com.am.frame.weichart.AccessTokenCache;
import com.am.frame.weichart.Constant;
import com.am.frame.weichart.JSSDKCache;
import com.am.frame.weichart.beans.BizOrder;
import com.am.frame.weichart.beans.EntPaymentInfo;
import com.am.frame.weichart.beans.JsAPITicket;
import com.am.frame.weichart.beans.PayInfo;
import com.am.frame.weichart.conf.WeiChartConfig;
import com.fastunit.Var;



/**
 * @author YueBin
 * @create 2016年7月7日
 * @version 
 * 说明:<br />
 * 微信工具类
 * 
 */
public class WeiChartAPIUtils {
	
	
	private static Logger logger=LoggerFactory.getLogger("com.am.frame.weichart.util.WeiChartAPIUtil");
	
	/** 调试日志输出，true输出，false不输出，在开发期间设置为true **/
	private static String tag = "Utils";
	private static AccessTokenCache cache;
	private static JSSDKCache jsCache;
	

	
	
	/**
	 * 此方法调用了getJSApiTicket方法 
	 * 
	 * 获取调用微信JS的config 返回值格式为：
	 * <br>
	 * {<br>
	 * debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，
	 * 可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。<br>
	 * appId: '', // 必填，公众号的唯一标识<br>
	 * timestamp: , // 必填，生成签名的时间戳<br>
	 * nonceStr: '', // 必填，生成签名的随机串<br>
	 * signature: '',// 必填，签名，见附录1<br>
	 * jsApiList: [] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2<br>
	 * }<br>
	 * @param url JS-SDK调用URL
	 * @param token  token
	 * @param appid appid
	 * @param appSecret appSecret
	 * @return 
	 */
	public static JSONObject getJSApiConfig(String url,JSONArray apiList,
			String token, String appid,String appSecret){
		JSONObject result=new JSONObject();
		
		//签名生成规则如下：参与签名的字段包括noncestr（随机字符串）, 
		//有效的jsapi_ticket, timestamp（时间戳）,
		//url（当前网页的URL，不包含#及其后面部分） 。
		//对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）后，
		//使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1。这里需要注意的是所有参数名均为
		//小写字符。对string1作sha1加密，字段名和字段值都采用原始值，不进行URL 转义。
		try{
			String nonceStr=getRandomStr(8);
			String jsapi_ticket=getJSApiTicket(url,token, appid, appSecret).getTicket();
			String timestamp=createTimestamp();
			
			String string1="jsapi_ticket="+jsapi_ticket+
					"&noncestr="+nonceStr+
					"&timestamp="+timestamp+
					"&url="+url;
			
			logger.info("string1:"+string1);
			
			String signature=SHA1(string1);
			logger.info("signature:"+signature);
			
			result.put("debug",Boolean.parseBoolean(Var.get("MpWeiChartJSDebug"))); // 开启调试模式,调用的所有api的返回值会在客户端alert出来，
			//若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			result.put("appId",appid); //appId:"" 必填，公众号的唯一标识
			result.put("timestamp",timestamp); //timestamp: , // 必填，生成签名的时间戳
			result.put("nonceStr",nonceStr); //    nonceStr: '', // 必填，生成签名的随机串
			result.put("signature",signature); //    signature: '',// 必填，签名，见附录1
			result.put("jsApiList",apiList); //    jsApiList: [] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
			    		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	public static String getRandomStr(int length){
		String str = "0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,g,h,i,j,k,l,m,"
				+ " n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,"
				+ "U,V,W,X,Y,Z";
		
        String str2[] = str.split(",");//将字符串以,分割  
        
        Random rand = new Random();//创建Random类的对象rand
        int index = 0;  
        String randStr = "";//创建内容为空字符串对象randStr  
        for (int i=0; i<length; ++i)  
        {  
            index = rand.nextInt(str2.length-1);//在0到str2.length-1生成一个伪随机数赋值给index  
            randStr += str2[index];//将对应索引的数组与randStr的变量值相连接  
        }
		return randStr;
	}
	
	
	/**
	 * 获取JSAPI
	 * @params url JS-SDK调用URL
	 * @param token token
	 * @param appid  appid
	 * @param appSecret appSecret
	 * @return
	 */
	public static JsAPITicket getJSApiTicket(String url,String token, String appid,String appSecret){
		JsAPITicket result=null;//jsCache.getJSApiTicket(token);
		CloseableHttpResponse response = null;
		
			logger.info("获取JSApiTicket中。。。。");
			
			jsCache=JSSDKCache.getInstance();
			if(jsCache!=null&&jsCache.getJSApiTicket(url)!=null
					&&jsCache.getJSApiTicket(url).isEnable()){
				result=jsCache.getJSApiTicket(url);
			}else{
				try {
					String accessToken=getAccessToken(token, appid, appSecret);
					
					Formatter f = new Formatter();
					f.format(Constant.GET_JSAPI_URL,accessToken);
					logger.info(f.toString().length()+"");
					logger.info("getJSApiTicket URL:\n"+f);
					
					CloseableHttpClient httpClient = SSLClient.createSSLClientDefault();
					HttpGet get = new HttpGet();
					get.setURI(new URI(f.toString()));
					
					response =httpClient.execute(get);// httpClient.execute(httpGet);
					
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						long len = entity.getContentLength();
						if (len != -1) {
							result=new JsAPITicket(url, new JSONObject(EntityUtils.toString(entity)));
						}
					}
					response.close();
					logger.info("获取JSAPITirect成功：" + result);
					jsCache.cacheJSApiTicket(result);
				
				} catch (Exception e){
					e.printStackTrace();
				}
			}
			return result;
	}
	
	
	 /**
		 * 获取签名
		 * @param payInfo
		 * @return
		 * @throws Exception
		 */
		 public String getSign(PayInfo payInfo){
			 
			 String signTemp = "appid="+payInfo.getAppid()
					 +"&attach="+payInfo.getAttach()
					 +"&body="+payInfo.getBody()
					 +"&device_info="+payInfo.getDevice_info()
					 +"&mch_id="+payInfo.getMch_id()
					 +"&nonce_str="+payInfo.getNonce_str()
					 +"&notify_url="+payInfo.getNotify_url()
					 +"&openid="+payInfo.getOpenid()
					 +"&out_trade_no="+payInfo.getOut_trade_no()
					 +"&spbill_create_ip="+payInfo.getSpbill_create_ip()
					 +"&total_fee="+payInfo.getTotal_fee()
					 +"&trade_type="+payInfo.getTrade_type()
					 +"&key="+Var.get("MpWeiPayAPIKey");
			 
			 logger.info("signTemp:"+signTemp);
			 String sign=DigestUtils.md5Hex(signTemp).toUpperCase();
			 
			 logger.info("sign:"+sign);
			 
			 return sign;
		 }
	
	
		 	/**
			 * 获取accessstoken，和公众帐号管理token不同 获取accesstoken
			 * 
			 * @return 公众帐号Token
			 */
			public static String getAccessToken(String token, String appid,String appSecret) throws Exception {
				String accToken = null;

				cache = AccessTokenCache.getInstAccessTokenCache();

				if (cache.getAccessToken(token) != null
						&& cache.getAccessToken(token).getEnable()) {
					accToken=cache.getAccessToken(token).getAccessToken();
				}else{
					cache.removeTonken(token);
					CloseableHttpResponse response = null;
					JSONObject result = null;
					try {
						logger.info("获取AccessToken中。。。。");
						
						Formatter f = new Formatter();
						f.format(Constant.GET_ACCTOKEN_URL, appid, appSecret);
						logger.info(f.toString().length()+"");
						logger.info("getAccess URL:\n"+f);
						
						CloseableHttpClient httpClient = SSLClient.createSSLClientDefault();
						HttpGet get = new HttpGet();
						get.setURI(new URI(f.toString()));
						
						response =httpClient.execute(get);// httpClient.execute(httpGet);
						
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							long len = entity.getContentLength();
							if (len != -1) {
								result = new JSONObject(EntityUtils.toString(entity));
							}
						}
						accToken = result.getString("access_token");
						if (accToken == null) {
							response.close();
							accToken= null;
						}
						response.close();
						logger.info("获取AccessToken成功：" + accToken);
						
						cache.cacheAccessToken(new AccessToken(accToken, token));
					} catch (Exception e){
						e.printStackTrace();
						try {
							if (response != null) {
								response.close();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				return accToken;
			}

		 
		 
	
	/**
	 * 微信获取时间戳，微信时间戳需要除以1000，微信坑爹的地方在这儿
	 * @return
	 */
	public static String createTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
	
	
	public static String SHA1(String decript) {
		return DigestUtils.sha1Hex(decript);
	}
	
	
	
	/**
	 * 创建统一下单的xml的java对象
	 * @param bizOrder 系统中的业务单号
	 * @param ip 用户的ip地址
	 * @param openId 用户的openId
	 * @return
	 */
	 public String createPayInfo(BizOrder bizOrder,String ip,String openId) {
		  PayInfo payInfo = new PayInfo();
		  payInfo.setAppid(WeiChartConfig.getInstance().getAppId());
		  payInfo.setDevice_info("WEB");
		  payInfo.setMch_id(WeiChartConfig.getInstance().getValue("mch_id"));
		  payInfo.setNonce_str(Utils.getRandomStr(12));
		  payInfo.setBody(bizOrder.getDescript());
		  payInfo.setAttach(bizOrder.getAttch());
		  payInfo.setOut_trade_no(bizOrder.getId());
		  payInfo.setTotal_fee(bizOrder.getTotalFee());
		  payInfo.setSpbill_create_ip(ip);
		  payInfo.setNotify_url(Var.get("weiChartNotifyUrl"));
		  payInfo.setTrade_type("JSAPI");
		  payInfo.setOpenid(openId);
	  
		  //获取支付签名
		  String sign=getSign(payInfo);
		  
		  Map<String,String> signMap=new HashMap<String,String>();
			 signMap.put("appid", payInfo.getAppid());
			 signMap.put("attach", payInfo.getAttach());
			 logger.info("attach:"+payInfo.getAttach());
			 signMap.put("body", payInfo.getBody());
			 signMap.put("device_info", payInfo.getDevice_info());
			 signMap.put("mch_id", payInfo.getMch_id());
			 signMap.put("notify_url", payInfo.getNotify_url());
			 signMap.put("nonce_str", payInfo.getNonce_str());
			 signMap.put("openid", payInfo.getOpenid());
			 signMap.put("out_trade_no", payInfo.getOut_trade_no());
			 signMap.put("spbill_create_ip", payInfo.getSpbill_create_ip());
			 signMap.put("total_fee", payInfo.getTotal_fee()+"");
			 signMap.put("trade_type", payInfo.getTrade_type());
			// signMap.put("key",WeiChartConfig.getInstance().getValue("api_key"));
			 signMap.put("sign", sign);

			 StringBuffer sb=new StringBuffer();
//			 
//			 mapToXMLTest2(signMap,sb);
//			 sb.append("</xml>");	
			 
			 sb.append("<xml>");
			 sb.append("<appid>"+payInfo.getAppid()+"</appid>");
			 sb.append("<attach>"+payInfo.getAttach()+"</attach>");
			 sb.append("<body>"+payInfo.getBody()+"</body>");
			 sb.append("<device_info>WEB</device_info>");
			 sb.append("<mch_id>"+Var.get("mch_id")+"</mch_id>");
			 sb.append("<nonce_str>"+payInfo.getNonce_str()+"</nonce_str>");
			 sb.append("<notify_url>"+payInfo.getNotify_url()+"</notify_url>");
			 sb.append("<openid>"+payInfo.getOpenid()+"</openid>");
			 sb.append("<out_trade_no>"+payInfo.getOut_trade_no()+"</out_trade_no>");
			 sb.append("<spbill_create_ip>"+payInfo.getSpbill_create_ip()+"</spbill_create_ip>");
			 sb.append("<total_fee>"+payInfo.getTotal_fee()+"</total_fee>");
			 sb.append("<trade_type>JSAPI</trade_type>");
			 sb.append("<sign>"+sign+"</sign>");
			 sb.append("</xml>");
			 
			 
			 return sb.toString();
	 }
	
	

		public static Map<String,String> xmlStrToMap(String xmlStr){
			 Map<String,String> result=new HashMap<String, String>();
			 
			SAXReader reader = new SAXReader();
			
			try {
				Document document=reader.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
				Element root = document.getRootElement();
				List<Element> elementList = root.elements();
				for (Element e : elementList) {
					result.put(e.getName(), e.getText());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}

		/**
		 * 统一下单接口
		 * 
		 * @return
		 */
		public static Map<String, String> getUnifiedOrder(String xml){
			Map<String, String> map = new HashMap<String, String>();
			
			logger.info("调用统一下单接口....");
			
			CloseableHttpClient httpClient = null;  
	        try{  
	            httpClient =SSLClient.createSSLClientDefault();
	            HttpsURLConnection urlCon = null;  
	            try {  
	                urlCon = (HttpsURLConnection) (new URL(Constant.UNIFIED_ORDER)).openConnection();  
	                urlCon.setDoInput(true);  
	                urlCon.setDoOutput(true);  
	                urlCon.setRequestMethod("POST");  
	                urlCon.setRequestProperty("Content-Length",  
	                        String.valueOf(xml.getBytes().length));  
	                urlCon.setUseCaches(false);  
	                //设置为gbk可以解决服务器接收时读取的数据中文乱码问题  
	                
	                urlCon.getOutputStream().write(xml.getBytes("utf-8"));  
	                urlCon.getOutputStream().flush();  
	                urlCon.getOutputStream().close();  
	                BufferedReader in = new BufferedReader(new InputStreamReader(  
	                        urlCon.getInputStream(),"utf-8"));
	                
	        		SAXReader reader = new SAXReader();
	        		Document document = reader.read(in);
	        		Element root = document.getRootElement();
	        		List<Element> elementList = root.elements();
	        		
	        		logger.info("统一下单接口返回参数:");
	        		for (Element e : elementList) {
	        			logger.info("KEY:"+e.getName()+"\tvalue:"+e.getText());
	        			map.put(e.getName(), e.getText());
	        		}
	        		
//	                String line;
//	                while ((line = in.readLine()) != null) {  
//	                    System.out.println(line);
//	                    result+=line;
//	                }  
	            } catch (MalformedURLException e) {  
	                e.printStackTrace();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }
	        }catch(Exception ex){  
	            ex.printStackTrace();  
	        }  
			return map;
		}
		
		
		/**
		  * 通过静默方式获取openid等信息，格式为<br>
		  * {
		  * "access_token":"ACCESS_TOKEN",网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同<br>
		  * "expires_in":7200,access_token接口调用凭证超时时间，单位（秒）<br>
		  * "refresh_token":"REFRESH_TOKEN",用户刷新access_token<br>
		  * "openid":"OPENID",用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID<br>
		  * "scope":"SCOPE",用户授权的作用域，使用逗号（,）分隔<br>
		  * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段.<br>
		  * }
		  * @param code
		  * @return  
		  */
		public static JSONObject getSnsapiBaseInfoByCode(String code){
			JSONObject result=new JSONObject();
			String url="https://api.weixin.qq.com/sns/oauth2/access_token?"+
					"appid="+WeiChartConfig.getInstance().getAppId()+ 
					"&secret="+WeiChartConfig.getInstance().getAppSecret()+
					"&code="+code+
					"&grant_type=authorization_code";
			result=getHttpsURL(url);
			
			return result;
		}
	
		
		/**
		 * 获取HTTPGET请求结果
		 * 
		 * @param url
		 * @return
		 */
		public static JSONObject getHttpsURL(String url) {
			JSONObject result =new JSONObject();
			try {
				CloseableHttpClient httpClient =SSLClient.createSSLClientDefault();
				RequestConfig reqConfig = RequestConfig.custom()
						.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
						.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
						.build();
				HttpGet httpGet = new HttpGet(url);
				httpGet.setConfig(reqConfig);
				CloseableHttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					long len = entity.getContentLength();
					if (len != -1) {
						result = new JSONObject(EntityUtils.toString(entity,
								Charset.forName("UTF-8")));
					}
				}
				response.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info(tag+result);
			
			return result;
		}
		
		
		/**
		 * 获取企业支付签名信息
		 * @param entPaymentInfo
		 * @return
		 */
		public String signEntPaymentInfo(EntPaymentInfo pInfo){
			
			String stringA="amount="+pInfo.getAmount()+
					"&check_name="+pInfo.getCheck_name()+
					"&desc="+pInfo.getDesc()+
					"&mch_appid="+pInfo.getMch_appid()+
					"&mchid="+pInfo.getMchid()+
					"&nonce_str="+pInfo.getNonce_str()+
					"&openid="+pInfo.getOpenid()+
					"&partner_trade_no="+pInfo.getPartner_trade_no()+
					"&re_user_name="+pInfo.getRe_user_name()+
					"&spbill_create_ip="+pInfo.getSpbill_create_ip();
			
			String signTemp=stringA+"&key="+pInfo.getKey();
			
			String sign=DigestUtils.md5Hex(signTemp).toUpperCase();
			
			return sign;
		}
		
		
		/**
		 * 创建统微信支付给用户的xml
		 * @param EntPaymentInfo 系统微信企业支付信息
		 * @return
		 */
		 public String createEntPaymentInfo(EntPaymentInfo entPaymentInfo) {
		  
			//获取支付签名
			String sign=signEntPaymentInfo(entPaymentInfo);
				 
			String querXML="<xml>"+
				"<mch_appid>"+entPaymentInfo.getMch_appid()+"</mch_appid>"+//这个与微信公众账号的AppId相同
				"<mchid>"+entPaymentInfo.getMchid()+"</mchid>"+//这个与微信公众账号的mchid相同
				"<nonce_str>"+entPaymentInfo.getNonce_str()+"</nonce_str>"+//随机数，不可超过32位
				"<partner_trade_no>"+entPaymentInfo.getPartner_trade_no()+"</partner_trade_no>"+//商户订单号 提现ID
				"<openid>"+entPaymentInfo.getOpenid()+"</openid>"+//提现OPENID
				"<check_name>"+entPaymentInfo.getCheck_name()+"</check_name>"+//NO_CHECK：不校验真实姓名 
														//FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账） 
														//OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
				"<re_user_name>"+entPaymentInfo.getRe_user_name()+"</re_user_name>"+
				"<amount>"+entPaymentInfo.getAmount()+"</amount>"+
				"<desc>"+entPaymentInfo.getDesc()+"</desc>"+//企业付款金额，单位为分
				"<spbill_create_ip>"+entPaymentInfo.getSpbill_create_ip()+"</spbill_create_ip>"+
				"<sign>"+sign+"</sign>"+
				"</xml>";
				 
				 
				 return querXML;
		 }
		
}
