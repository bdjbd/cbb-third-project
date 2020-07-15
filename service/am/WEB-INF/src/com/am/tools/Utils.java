package com.am.tools;
/**
 * @author Mike
 * @create 2015年1月28日
 * @version 
 * 说明:<br />
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.am.base.MakeCertPirc;
import com.baidu.yun.channel.auth.ChannelKeyPair;
import com.baidu.yun.channel.client.BaiduChannelClient;
import com.baidu.yun.channel.exception.ChannelClientException;
import com.baidu.yun.channel.exception.ChannelServerException;
import com.baidu.yun.channel.model.PushBroadcastMessageRequest;
import com.baidu.yun.channel.model.PushBroadcastMessageResponse;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.weChat.AccessTokenCache;
import com.weChat.Constant;
import com.weChat.beans.Message.MessageTemplet;
import com.weChat.exception.WeChatInfaceException;
/**
 * 
 * @creator Mike
 * @create Nov 15, 2013
 * @version $Id
 */
public class Utils {

	/** 调试日志输出，true输出，false不输出，在开发期间设置为true **/
	private static boolean DEBUG = true;
	private static String tag = "Utils";
	private static AccessTokenCache cache;
	
	
	
	/**
	 * 将map对象转换成 org.json.JSONObject对象
	 * @param map
	 * @return
	 */
	public static JSONObject mapToJSON(Map<String, String> map){
		JSONObject json = new JSONObject();
		
		Set<String> keySet = map.keySet();
		for(String key:keySet)
		{
		    try {
				json.put(key, map.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	

	/**
	 * 获取一个有数字组成的指定长度的字符串
	 * 
	 * @param length
	 *            字符串长度
	 * @return 字符串
	 */
	public static String getRandomStr(int length) {
		String str = new Random().nextLong() + "";
		if (str.length() > length + 1) {
			str = str.substring(1, length);
		}
		return str;
	}

	/**
	 * 根据公众帐号ID获取对应的所属机构ID
	 * 
	 * @param token
	 *            公众帐号ID
	 * @return
	 */
	public static String getOrgId(String token) {
		String orgid = "org";
		
		DB db=null;
		
		try {
			db = DBFactory.newDB();
			
			String sql = "SELECT orgid FROM ws_public_accounts WHERE token='"
					+ token + "' ";
			MapList map = db.query(sql);
			if (!Checker.isEmpty(map)) {
				orgid = map.getRow(0).get(0);
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			
		}
		return orgid;
	}

	/**
	 * 根据相对与平台部署路径获取文件名
	 * 
	 * @param path
	 *            已“/”开头
	 * @return
	 */
	public static String getFileNamef(String path) {
		String root = Path.getRootPath();
		File file = new File(root + path);
		if (!file.exists()) {
			return "";
		} else {
			String[] fileNames = file.list();
			if (fileNames != null && fileNames.length > 0) {
				return fileNames[0];
			}
			return "";
		}
	}

	/**
	 * 根据绝对路径获取文件名
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileNamer(String filaPath) {
		File file = new File(filaPath);
		if (!file.exists()) {
			return "";
		} else {
			String[] fileNames = file.list();
			if (fileNames != null && fileNames.length > 0) {
				return fileNames[0];
			}
			return "";
		}
	}

	/**
	 * 4位机构编号+8位年月日+4位流水号
	 * 
	 * @param orgid
	 * @return
	 */
	public static String getOrderCode(String orgid) {
		String orderCode = "";
		
		DB db=null;
		
		try {
			String data = new SimpleDateFormat("yyyyMMdd").format(
					new Date(System.currentTimeMillis())).toString();
			String sql = "SELECT count(*) FROM ws_order ";
			
			db = DBFactory.newDB();
			
			MapList map = db.query(sql);
			long number = 0;
			if (Long.parseLong(map.getRow(0).get(0)) == 0) {
				number = 0;
			} else {
				number = Long.parseLong(map.getRow(0).get(0));
			}
			if (!Checker.isEmpty(map)) {
				orderCode = orgid + data + Utils.format0Right(number + 1, 4);
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}finally{
			
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			
		}
		return orderCode;
	}

	/**
	 * 格式化输出 整数 [*]右对齐,左补0
	 * 
	 * @param num
	 * @param min_length
	 *            : 最小输出长度
	 * @return
	 */
	public static String format0Right(long num, int min_length) {
		String format = "%0" + (min_length < 1 ? 1 : min_length) + "d";
		return String.format(format, num);
	}

//	/**
//	 * 获取accessstoken，和公众帐号管理token不同 获取accesstoken
//	 * 
//	 * @return 公众帐号Token
//	 */
//	public static String getAccessToken(String token, String appid,
//			String appSecret) throws Exception {
//		String accToken = null;
//
//		cache = AccessTokenCache.getInstAccessTokenCache();
//
//		if (cache.getAccessToken(token) != null
//				&& cache.getAccessToken(token).getEnable()) {
//			return cache.getAccessToken(token).getAccessToken();
//		}
//		cache.removeTonken(token);
//		CloseableHttpResponse response = null;
//		JSONObject result = null;
//		try {
//			System.out.println("获取AccessToken中。。。。");
//			Formatter f = new Formatter();
//			f.format(Constant.GET_ACCTOKEN_URL, appid, appSecret);
//			CloseableHttpClient httpClient = HttpClients.createDefault();
//			RequestConfig reqConfig = RequestConfig.custom()
//					.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
//					.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
//					.build();
//			Utils.Log(tag, f);
//			HttpGet httpGet = new HttpGet(f.toString());
//			httpGet.setConfig(reqConfig);
//			response = httpClient.execute(httpGet);
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				long len = entity.getContentLength();
//				if (len != -1) {
//					result = new JSONObject(EntityUtils.toString(entity));
//				}
//			}
//			accToken = result.getString("access_token");
//			if (accToken == null) {
//				response.close();
//				return null;
//			}
//			response.close();
//			System.out.println("获取AccessToken成功：" + accToken);
//			cache.cacheAccessToken(new AccessToken(accToken, token));
//			return accToken;
//		} catch (Exception e) {
//			try {
//				if (response != null) {
//					response.close();
//				}
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			throw new Exception(e.getMessage()
//					+ "\n获取access_token出错");
//		}
//	}
//
//	/**
//	 * 发送消息根据消息ID
//	 * 
//	 * @param openidStr
//	 * @param msid
//	 *            消息ID
//	 * @return 发送图片File
//	 */
//	public static String sendMessage(Map<String, String> msgMap,
//			ArrayList<String> openidStr, String imgurl)
//			throws WeChatInfaceException {
//		String result = "1";
//		try {
//			DB db = DBFactory.getDB();
//			Formatter f = new Formatter();
//			String msgType = msgMap.get("msgType");
//			// 获取消息对于公众帐号token，appid,appSecret
//			String getMsgAccInfoSQL = "SELECT public_id,app_id,app_secret,token,orgid "
//					+ " FROM ws_public_accounts WHERE public_id="
//					+ msgMap.get("public_id");
//			MapList publAccMap = db.query(getMsgAccInfoSQL);
//			if (Checker.isEmpty(publAccMap))
//				return "2";
//			String accessToken = getAccessToken(
//					publAccMap.getRow(0).get("token"), publAccMap.getRow(0)
//							.get("app_id"),
//					publAccMap.getRow(0).get("app_secret"));
//			f.format(Constant.SEND_MSG_URL, accessToken);
//			String url = f.toString();
//			String msgData = "";
//			for (int i = 0; i < openidStr.size(); i++) {
//				// 获取消息类型
//				if ("1".equals(msgType)) {// 文本
//					String textMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
//					f = new Formatter();
//					f.format(textMsg, openidStr.get(i), msgMap.get("content"));
//					msgData = f.toString();
//					int unUsedLen = MessStatistics(msgMap.get("public_id"));
//					if (unUsedLen >= 1) {
//						JSONObject reString = sendPostData(url, msgData);
//						if (reString == null) {// 发送失败
//							result = "2";
//						} else {// 发送成功
//							result = "1";
//							Utils.SaveMsg(msgMap);
//						}
//					}
//				} else if ("6".equals(msgType)) {// 图文
//					String imgtextMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\":[{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}]}}";
//					f = new Formatter();
//					f.format(imgtextMsg, openidStr.get(i), msgMap.get("title"),
//							msgMap.get("description"), msgMap.get("url"),
//							imgurl.replace("\\", "/"));
//					msgData = f.toString();
//					int unUsedLen = MessStatistics(msgMap.get("public_id"));
//					// String moreMsg=builderMoreNews(msgMap);
//					// msgData.replace("@", moreMsg);
//					if (unUsedLen >= 1) {
//						JSONObject reString = sendPostData(url, msgData);
//						if (reString == null) {
//							result = "2";
//						} else {
//							result = "1";
//							Utils.SaveMsg(msgMap);
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = "2";
//			throw new WeChatInfaceException(e.getMessage());
//		}
//
//		return result;
//	}

	/**
	 * 构造其图文消息
	 * 
	 * @param msgMap
	 */
	private static String builderMoreNews(Map<String, String> msgMap) {
		String str = ",{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}";
		String result = " ";
		int i = 2;
		for (; i < 11; i++) {
			String title = msgMap.get("news" + i);
			String description = title;
			String picurl = msgMap.get("fimage" + i);
			String url = msgMap.get("news" + i + "url");
			if (title != null && title.length() > 0) {
				Formatter f = new Formatter();
				f.format(str, title, title, url, picurl.replace("\\", "/"));
				result += f.toString();
			}
		}
		return result;
	}

	/**
	 * 将发送成功的消息存入统计表
	 * 
	 * @throws JDBCException
	 * @throws JDBCException
	 */

	public static void SaveMsg(Map<String, String> msgMap) throws JDBCException {
		DB db = DBFactory.getDB();
		Table table = new Table("wwd", "WS_STATISTICS");
		TableRow tableRow = table.addInsertRow();
		tableRow.setValue("public_id", msgMap.get("public_id"));
		tableRow.setValue("orgid", msgMap.get("orgid"));
		tableRow.setValue("scene_id", msgMap.get("scene_id"));
		tableRow.setValue("msgtype", msgMap.get("msgType"));
		tableRow.setValue("msg_id", msgMap.get("fsend_id"));
		db.save(table);

	}

	/**
	 * 统计当前公众帐号今日剩余可发消息数
	 * 
	 * @throws JDBCException
	 * @return unUsedLen
	 * */

	public static int MessStatistics(String public_id) throws JDBCException {
		int unUsedLen = 0;
		DB db = DBFactory.getDB();
		String qSQL = "select c.public_id,c.orgid,a.max_textmsg as maxmsg from ws_enterprise_month_tariff  a left join "
				+ "ws_org_baseinfo b on a.monthly_fee_id=b.monthly_fee_id left join ws_public_accounts c "
				+ "on b.orgid=c.orgid where c.public_id='" + public_id + "' ";
		MapList list = db.query(qSQL);
		if (list.size() > 0) {
			// 此公众帐号最大可发消息数
			String maxMsgLen = list.getRow(0).get("maxmsg");
			// 获取系统当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = df.format(new Date());
			String countSQL = "SELECT count(*) as count from WS_STATISTICS where public_id='"
					+ public_id
					+ "' and to_char(callinter_time,'yyyy-mm-dd')='"
					+ nowDate
					+ "'";
			MapList mapList = db.query(countSQL);
			// 此公众帐号今日已发消息数
			String count = mapList.getRow(0).get("count");
			// 此公众帐号今日剩余可发消息数
			unUsedLen = Integer.parseInt(maxMsgLen) - Integer.parseInt(count);
		}
		return unUsedLen;
	}

	/**
	 * 向url提交数据data
	 * 
	 * @param url
	 * @param data
	 * @return 执行结果
	 */
	public static JSONObject sendPostData(String url, String data)
			throws WeChatInfaceException {
		try {
			JSONObject result = null;
			CloseableHttpResponse response = null;
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig reqConfig = RequestConfig.custom()
					.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
					.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
					.build();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(reqConfig);
			HttpEntity entity = EntityBuilder.create()
					.setContentEncoding("GBK")
					.setBinary(data.getBytes("UTF-8")).build();
			httpPost.setEntity(entity);
			// 设置访问超时时间
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
			response = httpClient.execute(httpPost);
			entity = response.getEntity();

			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1) {
					result = new JSONObject(EntityUtils.toString(entity));
				}
			}
			response.close();
			return result;
		} catch (Exception e) {
			throw new WeChatInfaceException(e.getMessage());
		}
	}

	/**
	 * 构造一条消息
	 * 
	 * @param msgMap
	 *            消息Map
	 * @param recvMap
	 *            消息接收人Map
	 * @return 1 文本消息;2 图片消息;3 语音消息;4 视频消息;5 音乐消息;6 图文消息 1，2，6
	 */
	public static String builderMsg(MapList msgMap, Row recvRow) {
		String msgType = msgMap.getRow(0).get("msg_type");
		Formatter f = new Formatter();
		// TODO
		if ("1".equals(msgType)) {// 文本
			f.format(MessageTemplet.TEXT_MSG, "");
		} else if ("2".equals(msgType)) {// 图片
			f.format(MessageTemplet.IMAGE_MSG, "");
		} else if ("6".equals(msgType)) {// 图文
			f.format(MessageTemplet.NEWS_MSG, "");
		}
		return f.toString();
	}

	/**
	 * 同步用户信息
	 * 
	 * @param accessToken
	 *            accessToken
	 * @param openid
	 */
	public static JSONObject getUserInfo(String accessToken, String openid) {
		JSONObject object = null;
		try {
			String getUserInfoUr = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="
					+ accessToken + "&openid=" + openid + "";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig reqConfig = RequestConfig.custom()
					.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
					.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
					.build();
			Formatter f = new Formatter();
			f.format(getUserInfoUr);
			HttpGet httpGet = new HttpGet(f.toString());
			httpGet.setConfig(reqConfig);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1) {
					object = new JSONObject(EntityUtils.toString(entity,
							Charset.forName("UTF-8")));
				}
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;

	}

	/**
	 * 获取关注者列表
	 * 
	 * @return JSONArray
	 * */
	public static JSONArray getUserList(String acctoken) {
		JSONArray array = new JSONArray();
		JSONObject result = null;
		try {
			String getUserList = "https://api.weixin.qq.com/cgi-bin/user/get?access_token="
					+ acctoken + "&next_openid=";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig reqConfig = RequestConfig.custom()
					.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
					.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
					.build();
			Formatter f = new Formatter();
			f.format(getUserList, "");
			HttpGet httpGet = new HttpGet(f.toString());
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
			JSONObject object = result;
			if (object.has("errcode")) {
				String errcode = object.getString("errcode");
				if ("48001".equals(errcode) || "50001".equals(errcode)) {
					JSONObject robj = new JSONObject();
					robj.put("RESULT", "ERROR");
					array.put(0, robj);
					return array;
				}
			} else {
				System.out.println(object);
				if (object.getInt("total") > 0) {
					JSONObject responseResult = object.getJSONObject("data");
					array = responseResult.getJSONArray("openid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}

	/**
	 * 发送图片消息，返回media_id,提交文件到url
	 * 
	 * @param file
	 *            提交文件
	 * @param url
	 *            文件上传路径
	 * @return MEDI_ID
	 * @throws JSONException
	 */
	public static String postFile(String accessToken, String url, File file)
			throws JSONException {
		String media_id = "";
		JSONObject result = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			RequestConfig reqConfig = RequestConfig.custom()
					.setSocketTimeout(20 * 1000).setConnectTimeout(10 * 1000)
					.build();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(reqConfig);
			// 创建待处理的文件
			FileBody fileb = new FileBody(file);
			// 对请求的表单域进行填充
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("file", fileb);
			// 设置请求
			httpPost.setEntity(reqEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
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
		media_id = result.getString("media_id");
		return media_id;
	}

	/***
	 * 获取webAccess_token 和消息接口访问是的access_token不同
	 * 
	 * @param appid
	 * @param appsecret
	 * @param code
	 * @return 返回值格式：
	 *         {"access_token":"ACCESS_TOKEN","expires_in":7200,"refresh_token"
	 *         :"REFRESH_TOKEN","openid":"OPENID","scope":"SCOPE"}
	 */
	public static JSONObject getWebAccessToken(String appid, String appsecret,
			String code) {
		String getWebAccessTokenURL = "https://api.weixin.qq.com/sns/oauth2/access_token?"
				+ "appid=%s&secret=%s&code=%s&grant_type=authorization_code";
		Formatter f = new Formatter();
		f.format(getWebAccessTokenURL, appid, appsecret, code);
		return getHttpURL(f.toString());
	}

	/**
	 * 根据openid和access_token拉取用户信息
	 * 
	 * @param access_token
	 *            OAuth2的access_token
	 * @param openid
	 *            用户唯一标识符
	 * @return 返回值格式: {"openid":" OPENID"," nickname":
	 *         NICKNAME,"sex":"1","province":"PROVINCE","city":"CITY",
	 *         "country":"COUNTRY","headimgurl":
	 *         "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46"
	 *         , "privilege":["PRIVILEGE1" "PRIVILEGE2"]}
	 */
	public static JSONObject loadUserInFoOAuthor2(String access_token,
			String openid) {
		/**
		 * 拉取用户信息URL 第一个参数：网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
		 * 第二个参数：用户的唯一标识
		 */
		String GETUSERINFO_OAuth2 = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&"
				+ "openid=%s&lang=zh_CN";
		Formatter f = new Formatter();
		f.format(GETUSERINFO_OAuth2, access_token, openid);
		return getHttpURL(f.toString());
	}

	/**
	 * 获取HTTPGET请求结果
	 * 
	 * @param url
	 * @return
	 */
	public static JSONObject getHttpURL(String url) {
		JSONObject result = null;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
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
		Log(tag, result);
		return result;
	}

	/***
	 * 调试阶段日志数输出
	 * 
	 * @param tag
	 * @param obj
	 */
	public static void Log(String tag, Object obj) {
		if (Utils.DEBUG) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
			System.out.println(tag + ":" + sdf.format(new java.util.Date())
					+ " >>>" + obj);
			LoggerFactory.getLogger(tag).error(tag+"  "+obj!=null?obj.toString():null);
		}
		
	}

	/**
	 * 获取永久二维码
	 * 
	 * @param accessToken
	 *            access_toke
	 * @param sceneId
	 *            场景ID
	 * @param path
	 *            文件保存路径
	 * @param fileName
	 *            文件名称
	 * @throws JDBCException
	 */
	public static void getLimitQrcode(String accessToken, int sceneId,
			String path, String fileName) throws JDBCException {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
		// String
		// data="{\"expire_seconds\": 1800,\"action_name\": \"QR_SCENE\",\"action_info\": {\"scene\":{\"scene_id\":%s}}}";
		String data = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %s}}}";
		Formatter furl = new Formatter();
		Formatter fdata = new Formatter();
		furl.format(url, accessToken);
		fdata.format(data, sceneId);
		try {
			String ticket = sendPostData(furl.toString(), fdata.toString())
					.getString("ticket");
			String dowloadUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
			Formatter f = new Formatter();
			f.format(dowloadUrl, ticket);
			System.out.println("获取很ticket成功！");
			downloadFileByPath(f.toString(), path, fileName);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (WeChatInfaceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载文档到指定的目录
	 * 
	 * @param url
	 * @param saveFilePath
	 */
	public static void downloadFileByPath(String url, String saveFilePath,
			String filaName) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		RequestConfig reqConfig = RequestConfig.custom()
				.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
				.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT).build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(reqConfig);
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				byte[] buffer = new byte[1024];
				java.io.InputStream in = entity.getContent();
				File file = new File(saveFilePath);
				if (!file.exists()) {
					System.out.println("文件不存在，新建文件！");
					file.mkdirs();
				}

				OutputStream out = new FileOutputStream(new File(file.getPath()
						+ "/" + filaName));
				int length = 0;
				while ((length = in.read(buffer)) > 0) {
					out.write(buffer, 0, length);
				}
				in.close();
				out.close();
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析XML数据
	 * 
	 * @param req
	 * @return 解析后的消息MAP
	 */
	public static Map<String, String> parseXMLMsg(HttpServletRequest req)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		InputStream is = req.getInputStream();
		SAXReader reader = new SAXReader();
		Document document = reader.read(is);
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}
		is.close();
		return map;
	}

	/**
	 * 查询方法(返回JsonObject集合)
	 * 
	 * @param java
	 *            .lang.String sql语句
	 * @return org.json.JSONArray json数组
	 */
	public static JSONArray query(String sql) {
		JSONArray returnJsonArray = new JSONArray();
		
		DB db=null;
		
		try {
			db= DBFactory.newDB();
			
			MapList list =db.query(sql);
			// list=DBFactory.getDB().query(arg0, arg1, arg2);
			int row_count = list.size();
			for (int i = 0; i < row_count; i++) {
				Row row = list.getRow(i);
				int column_count = row.size();
				JSONObject jo = new JSONObject();
				for (int j = 0; j < column_count; j++) {
					String currentValue = row.get(j);
					if (currentValue == null
							|| currentValue.trim().equalsIgnoreCase("null")) {
						jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						jo.put(row.getKey(j).toUpperCase(), currentValue);
					}
				}
				returnJsonArray.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return returnJsonArray;
	}

	/**
	 * 验证token对应的公众帐号是否认证
	 * 
	 * @param token
	 *            token
	 * @param appid
	 *            appid
	 * @param appSecret
	 *            appsecret
	 * @return 认证返回true，否则返回false
	 */
	public static boolean checkVaildate(String accessString) {
		boolean result = false;
		JSONArray resultAr = getUserList(accessString);
		if (resultAr != null && !resultAr.toString().contains("ERROR")) {
			result = true;
		}
		return result;
	}

	public void testUitls() throws JDBCException {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=t8WYyYcQgR__0V_ezqquM_BBFkuzk_kG0ztmAhJ9MrRJSPZQTDvmheO9GllDPI3Tr-NRS8R4CjZGb2J8gQ5UR2c6NysAqgM79P2OqeYSzqfelHiw2Wff65UxyTUrrcPCHJYOziDdjn9Mlv4BBvc8uw";
		String data = "{\"expire_seconds\": 1800,\"action_name\": \"QR_SCENE\",\"action_info\": {\"scene\":{\"scene_id\": 123}}}";
		try {
			JSONObject result = sendPostData(url, data);
			Log("@TEST", result.toString());
		} catch (WeChatInfaceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件
	 * 
	 * @param table
	 *            表面
	 * @param pk
	 *            主键
	 * @param isHome
	 *            是否在home目录下
	 * @return List<String>
	 */
	public static List<String> getImageByTable(String table, String pk,
			boolean isHome) {
		List<String> imgs = new ArrayList<String>();
		if (table == null || pk == null) {
			return null;
		}
		String rootPaht = isHome == true ? Path.getHomePath() : Path
				.getRootPath();
		// String rootPaht="D:\\WeiStore\\soft\\ws";
		String tableFilePath = rootPaht + File.separator + "files"
				+ File.separator + table + File.separator + pk;
		Utils.Log(table, tableFilePath);
		;
		File file = new File(tableFilePath);
		if (!file.exists()) {
			Utils.Log("file.exists", "file.exists");
			return null;
		}
		File[] files = file.listFiles();
		if (files == null) {
			Utils.Log("files", "files");
			return null;
		}
		for (File f : files) {
			System.out.println("/files/" + table + "/" + pk + "/" + f.getName()
					+ "/" + f.list()[0]);
			imgs.add("/files/" + table + "/" + pk + "/" + f.getName() + "/"
					+ f.list()[0]);
		}
		return imgs;
	}

	public void fileTest() {
		try {
			String str1 = "[{\"NEWS\":\"{\"type\":\"view\",\"name\":\"党政时要\",\"url\":\"hosturl/news.jsp?orgid=org&cid=4\"}\"},{\"NEWS\":\"{\"type\":\"view\",\"name\":\"全球经济\",\"url\":\"hosturl/news.jsp?orgid=org&cid=5\"}\"},{\"NEWS\":\"{\"type\":\"view\",\"name\":\"游戏天地\",\"url\":\"hosturl/news.jsp?orgid=org&cid=6\"}\"},{\"NEWS\":\"{\"type\":\"view\",\"name\":\"明星娱乐\",\"url\":\"hosturl/news.jsp?orgid=org&cid=11\"}\"}]";
			JSONArray jar = new JSONArray(str1);
			System.out.println(jar);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String resultStr = "{\"errcode\":%s, \"errmsg\": \"%s\"}";
	private static int port = 456;
	public final static double PI = 3.14159265358979323; // 圆周率
	public final static double R = 6371229; // 地球的半径

	public static final JsonObject ResultSetToJsonObject(ResultSet rs) {
		JsonObject element = null;
		JsonArray ja = new JsonArray();
		JsonObject jo = new JsonObject();
		ResultSetMetaData rsmd = null;
		String columnName, columnValue = null;
		try {
			rsmd = rs.getMetaData();
			while (rs.next()) {
				element = new JsonObject();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					columnName = rsmd.getColumnName(i + 1);
					columnValue = rs.getString(columnName);
					element.addProperty(columnName, columnValue);
				}
				ja.add(element);
			}
			jo.add("result", ja);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jo;
	}

	/**
	 * 输出执行结构
	 * 
	 * @param ercode
	 *            错误代码
	 * @param ermsg
	 *            错误信息
	 * @return {"errcode":错误代码, "errmsg": "错误信息"}
	 */
	public static String executeResult(int ercode, String ermsg) {
		@SuppressWarnings("resource")
		Formatter f = new Formatter();
		f.format(resultStr, ercode, ermsg);
		System.out.println(f.toString());
		return f.toString();
	}

	/**
	 * 获取一个随机的字符串
	 * 
	 * @param length
	 *            随机字符串长度
	 * @return 获取的随机字符串
	 */
	public static String getRandomStrs(int length) {
		if (length < 1)
			return null;
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(MakeCertPirc.mapTable[rand
					.nextInt(MakeCertPirc.mapTable.length)]);
		}

		return sb.toString();
	}



	public static String getFastUnitFilePath(String table, String columName,
			String id) {
		String result = "";
		String fileSpar = "/";
		String relativeFilePath = fileSpar + "files" + fileSpar + table
				+ fileSpar + id + fileSpar + columName;
		String fielPath = Path.getRootPath() + relativeFilePath;
		File file = new File(fielPath);
		if (!file.exists()) {
			return result;
		}
		for (String f : file.list()) {
			result += relativeFilePath + fileSpar + f + ",";
		}
		return result;
	}
	
	
	/**
	 *保存附件文件到表中，此方法用来保存主键为int类型的附件
	 * @param db  DB
	 * @param tableName 表面
	 * @param field  字段名，数据库中字段名，平台对于附件名称为 bdp+field
	 * @param primaryKey  主键名
	 * @param primaryValue  主键值  
	 * @throws JDBCException
	 */
	public static void saveFilesPathInt(DB db,String tableName,String field,String primaryKey,String primaryValue) throws JDBCException{
		
		String fileName=Utils.getFastUnitFilePath(tableName, "bdp"+field, primaryKey);
		
		String updateSql=" ";
		
		if(fileName!=null&&fileName.length()>1){
			fileName=fileName.substring(0, fileName.length()-1);
			updateSql="UPDATE "+tableName+"  SET "+field+"='"
					+fileName+"'  WHERE primaryKey="+primaryValue+" ";
			db.execute(updateSql);
		}
	}
	
	/**
	 *保存附件文件到表中，此方法用来保存主键为String类型的附件
	 * @param db  DB
	 * @param tableName 表面
	 * @param field  字段名，数据库中字段名，平台对于附件名称为 bdp+field
	 * @param primaryKey  主键名
	 * @param primaryValue  主键值  
	 * @throws JDBCException
	 */
	public static void saveFilesPathString(DB db,String tableName,String field,String primaryKey,String primaryValue) throws JDBCException{
		
		String fileName=Utils.getFastUnitFilePath(tableName, "bdp"+field, primaryValue);
		
		String updateSql=" ";
		
		if(fileName!=null&&fileName.length()>1){
			fileName=fileName.substring(0, fileName.length()-1);
			updateSql="UPDATE "+tableName+"  SET "+field+"='"
					+fileName+"'  WHERE "+primaryKey+"='"+primaryValue+"' ";
			db.execute(updateSql);
		}
	}

	/**
	 * 百度推送
	 * 
	 * @param member
	 * @param msg
	 */
	public static void sendMsgToMember(String member, String msg) {

		/*
		 * @brief 推送广播消息(消息类型为透传，由开发方应用自己来解析消息内容) message_type = 0 (默认为0)
		 */

		// 1. 设置developer平台的ApiKey/SecretKey
		String apiKey = "M6VBk7BLei5ySc1Ni7pA4NQZ";
		String secretKey = "K1cQkh0VWodVmevVe64pRFHMRCOops1z";
		ChannelKeyPair pair = new ChannelKeyPair(apiKey, secretKey);

		// 2. 创建BaiduChannelClient对象实例
		BaiduChannelClient channelClient = new BaiduChannelClient(pair);

		// 3. 若要了解交互细节，请注册YunLogHandler类
		channelClient.setChannelLogHandler(new YunLogHandler() {
			@Override
			public void onHandle(YunLogEvent event) {
				System.out.println(event.getMessage());
			}
		});

		try {

			// 4. 创建请求类对象
			PushBroadcastMessageRequest request = new PushBroadcastMessageRequest();
			request.setDeviceType(3); // device_type => 1: web 2: pc 3:android
										// 4:ios 5:wp

			// request.setMessage("Hello Channel");
			// 若要通知，
			request.setMessageType(1);
			request.setMessage(msg);
			// 5. 调用pushMessage接口
			PushBroadcastMessageResponse response = channelClient
					.pushBroadcastMessage(request);

			// 6. 认证推送成功
			System.out.println("push amount : " + response.getSuccessAmount());

		} catch (ChannelClientException e) {
			// 处理客户端错误异常
			e.printStackTrace();
		} catch (ChannelServerException e) {
			// 处理服务端错误异常
			System.out.println(String.format(
					"request_id: %d, error_code: %d, error_message: %s",
					e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
		}
	}

	/**
	 * 计算两坐标之间的距离
	 * 
	 * @param longt1
	 * @param lat1
	 * @param longt2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double longt1, double lat1, double longt2,
			double lat2) {
		double x, y, distance;
		x = (longt2 - longt1) * PI * R
				* Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		y = (lat2 - lat1) * PI * R / 180;
		distance = Math.hypot(x, y);
		return distance;
	}

	public void testFilePath() {
		String fileName = "bacdedf,";
		System.out.println(fileName.substring(0, fileName.length() - 1));
	}

	
	public void testSendMsg() {
		sendMsgToMember("",
				"{\"title\":\"您有新的订单\",\"description\":\"您有新的订单，订单编号是：org20140805009\"}");
	}

	
	@Test
	public void testDesic(){
		double x, y, distance;
		//116.4219,39.93986
		double longt2=108.885554;
		double longt1=108.9388;
		double lat1=34.2214;
		double lat2=34.194095;
//		x = (longt2 - longt1) * PI * R
//				* Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
//		y = (lat2 - lat1) * PI * R / 180;
//		distance = Math.hypot(x, y);
		distance=getDistance(longt1, lat1, longt2, lat2);
		System.out.println(distance);
		distance=gps2m(lat1,longt1,lat2,longt2);
		System.out.println(distance);
	}
	
	
	 private final double EARTH_RADIUS = 6378137.0;  
	 
	 private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
	        double radLat1 = (lat_a * Math.PI / 180.0);
	        double radLat2 = (lat_b * Math.PI / 180.0);
	        double a = radLat1 - radLat2;
	        double b = (lng_a - lng_b) * Math.PI / 180.0;
	        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	               + Math.cos(radLat1) * Math.cos(radLat2)
	               * Math.pow(Math.sin(b / 2), 2)));
	        s = s * EARTH_RADIUS;
	        s = Math.round(s * 10000) / 10000;
	        return s;
	    }
	 
	 
	 
	 	/**   
	     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额  
	     *   
	     * @param amount  
	     * @return  
	     */    
	    public static Long changeY2F(String amount){    
	        String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额    
	        int index = currency.indexOf(".");    
	        int length = currency.length();    
	        Long amLong = 0l;    
	        if(index == -1){    
	            amLong = Long.valueOf(currency+"00");    
	        }else if(length - index >= 3){    
	            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));    
	        }else if(length - index == 2){    
	            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);    
	        }else{    
	            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");    
	        }    
	        return amLong;    
	    }
	    
	    /**
	     *  保存附件文件到表中，此方法用来保存主键为String类型的附件,并且修改时会覆盖掉原来的附件
	     * @param db  DB
	     * @param tableName 表面
	     * @param field  字段名，数据库中字段名，平台对于附件名称为 bdp+field
	     * @param primaryKey  主键名
	     * @param primaryValue  主键值  
	     * @param uuid 文件名
	     * @throws JDBCException
	     */
	    public static void saveFilesPath(DB db,String tableName,String field,String primaryKey,String primaryValue,String uuid) throws JDBCException{
	    	if(Checker.isEmpty(uuid)){
	    		uuid = UUID.randomUUID().toString();
	    	}
	    	
	    	String fileName=Utils.getFastUnitFilePath(tableName, "bdp"+field, primaryValue);
	    	String path = "";
	    	String suffix = "";
	    	String updateSql=" ";
	    	if(!Checker.isEmpty(fileName)){
	    		path = Path.getRootPath()+fileName.substring(0, fileName.length()-1);
	    		File   file=new   File(path);   //指定文件名及路径  
	    		String   filename=file.getAbsolutePath();     
	    		filename =file.getName();
	    		 
	    		suffix = filename.substring(filename.lastIndexOf(".")+1,filename.length());
	    		file.renameTo(new   File(path.substring(0,path.lastIndexOf("/")+1)+uuid+"."+suffix));   //改名 
	    		fileName=Utils.getFastUnitFilePath(tableName, "bdp"+field, primaryValue);
	    		
	    		fileName = path.substring(0,path.lastIndexOf("/")+1)+uuid+"."+suffix;
	    		
	    		if(fileName!=null&&fileName.length()>1){
	    			fileName=fileName.substring(Path.getRootPath().length(),fileName.length());
	    			updateSql="UPDATE "+tableName+"  SET "+field+"='"
	    					+fileName+"'  WHERE "+primaryKey+"='"+primaryValue+"' ";
	    			db.execute(updateSql);
	    		}
	    	}
	    }
	    
}