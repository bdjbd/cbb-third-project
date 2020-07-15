package com.wisdeem.wwd.WeChat.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Path;
import com.fastunit.Row;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.tools.DatabaseAccess;
import com.wisdeem.wwd.Constant;
import com.wisdeem.wwd.WeChat.Utils;
import com.wisdeem.wwd.WeChat.beans.Interviewer;
import com.wisdeem.wwd.WeChat.beans.Message;
import com.wisdeem.wwd.WeChat.beans.PublicAccount;
import com.wisdeem.wwd.WeChat.exception.WeChatInfaceException;

/**
 * 说明: 微信公众平台业务处理类
 * 
 * @creator 岳斌
 * @create Nov 18, 2013
 * @version $Id
 */
public class WeChatInfaceServer {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/** 用户缓存信息 **/
	public static HashMap<String, PublicAccount> USERINFO_CATCH = new HashMap<String, PublicAccount>();
	private final String tag = "WeChatInfaceServer01";
	private final String SESSION_INTERVIEWER = "SESSION_INTERVIEWER";
	/** 访问者信息 **/
	private Interviewer interviewer;
	private final String SESSION_ACCOUNT = "SESSION_ACCOUNT";
	/** 当前公众帐号 **/
	private PublicAccount currentAccount;
	/** 消息类型 **/
	private String msgType;
	/** 微网店接入token **/
	private String token = "";

	private Properties properties;
	/** 微商店部署主机地址 **/
	private static String hostUrl;
	/** 公众帐号需要OAuth2.0认证的域名 **/
	public static String OAUTH2_URL = "";
	
	
	//找服务、商城、会员卡
	private static String meunStr = "{\"button\":["
			+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"},"
			+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"},"
			+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"}]}";
	
	/** 设置菜单URL **/
	private static final String setMenuTokenURL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
	private DB db;

	private static WeChatInfaceServer instance;
	
	public static WeChatInfaceServer getInstance(){
		if(instance==null){
			instance=new WeChatInfaceServer();
		}
		return instance;
	}
	
	private  WeChatInfaceServer() {
		try {
			db = DBFactory.getDB();
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"host.properties"));
			hostUrl = properties.getProperty("host", "http://115.28.234.15");
			OAUTH2_URL = properties.getProperty("oauth2_url", "115.28.234.15");
			
			//判断是否为科技DIY
			if(hostUrl!=null&&hostUrl.contains("ckbox")){
				
				meunStr = "{\"button\":["
						+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"},"
						+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"},"
						+ "{\"type\":\"view\",\"name\":\"%s\",\"url\":\"%s\"}]}";
				
			}
			
		} catch (Exception e) {
			System.out.println("新建WeChatInfaceServer出错！");
			e.printStackTrace();
		}
	}

	/**
	 * 接入验证，如果接入验证成功，原样返回echostr，否则返回null
	 * 
	 * @return 如果验证成功返回 echostr
	 */
	public String valid(HttpServletRequest req) {
		String token=Var.get("weiChartToken");
		String echostr = req.getParameter("echostr");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String signature = req.getParameter("signature");
		
		// 获得加密后的字符串可与signature对比，标识该请求来源于微信
		if (signature.equals(checkSignature(token,timestamp, nonce))) {
			return echostr;
		}else{
			return null;
		}
	}

	/**
	 * 加密/校验流程如下： 1. 将token、timestamp、nonce三个参数进行字典序排序 2.
	 * 将三个参数字符串拼接成一个字符串进行sha1加密
	 * 
	 * @param req
	 * @return 返回token,timestamp,nonce的sha1密文
	 */
	public String checkSignature(String token,String timestamp, String nonce) {
		String[] sortArray = new String[] {token,timestamp, nonce };
		Arrays.sort(sortArray);
		String tempStr = sortArray[0] + sortArray[1]+ sortArray[2];
		return DigestUtils.sha1Hex(tempStr);
	}

	/**
	 * 验证token是否属于平台管理用户
	 * 
	 * @param token
	 *            公众帐号token
	 * @return 如果属于平台企业用户，返回true，否则返回false
	 */
	public boolean validToken(String token) {
		if (token == null || token.equals("")) {
			return false;
		}
		try {
			String vaildSQL = "SELECT token FROM ws_public_accounts WHERE token='"
					+ token + "'";
			MapList map = db.query(vaildSQL);
			if (!Checker.isEmpty(map)) {
				return true;
			} else {
				System.out.println(token + "不存在。");
				return false;
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getOrgNameByToken(String token) {
		
		String sql = "SELECT org.orgname FROM ws_public_accounts AS wpa "
				+ "	LEFT JOIN aorg AS org ON wpa.orgid=org.orgid "
				+ "   WHERE wpa.token='" + token + "'";
		
		String orgname = "";
		
		try {
			MapList map = db.query(sql);
			if (!Checker.isEmpty(map)) {
				orgname = map.getRow(0).get("orgname");
			}
		
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return orgname;
	}

	/**
	 * 微信接口业务处理 微信推送消息接入接口
	 * 
	 * @param request
	 *            HttpRequest对象
	 * @param response
	 *            HttpResponse对象
	 */
	public void weChatServer(HttpServletRequest req, HttpServletResponse rep,
			String action) throws ServletException, IOException {
		// GET请求处理，GET请求一般为微信工作平台开发者模式接入验证
		if ("GET".equalsIgnoreCase(req.getMethod())) {
			PrintWriter out = new PrintWriter(rep.getOutputStream());
			out.print((valid(req)));
			out.flush();
			out.close();
			return;
		}
		// 权限验证
		try {
			Map<String, String> msg = Utils.parseXMLMsg(req);
			parseAccountInfo(msg, token, req);
			interviewer = (Interviewer) req.getSession().getAttribute(
					SESSION_INTERVIEWER);
			if (interviewer == null) {
				interviewer = new Interviewer();
			}
			interviewer.setOpenid(msg.get("FromUserName"));
			req.getSession().setAttribute(SESSION_INTERVIEWER, interviewer);

			// 获取消息类型
			msgType = msg.get("MsgType");
			if ("event".equalsIgnoreCase(msgType)) {// 事件
				processEvent(msg, req, rep);// 事件处理
			} else if ("text".equalsIgnoreCase(msgType)) {// 文本
				// 文本消息处理
				processTextMsg(msg, req, rep);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据Token解析帐号信息并将帐号信息保存到Session中
	 * 
	 * @param token
	 */
	private void parseAccountInfo(Map<String, String> msg, String token,
			HttpServletRequest req) {
		String sql = "SELECT wpa.app_id,wpa.app_secret,wpa.is_valid,wpa.orgid,       "
				+ "  org.orgname,wpa.account_belong                                  "
				+ "	FROM ws_public_accounts AS wpa LEFT JOIN aorg AS org           "
				+ "	ON wpa.orgid=org.orgid WHERE wpa.token='"
				+ token
				+ "'         "
				+ "	AND wpa.app_id IS NOT NULL                                     ";
		try {
			String openid = msg.get("FromUserName");
			currentAccount = USERINFO_CATCH.get(openid);
			if (currentAccount == null) {
				Utils.Log("parseAccuoutnInfo:", "curentAccount为空" + openid);
				currentAccount = new PublicAccount();
				MapList map = db.query(sql);
				if (!Checker.isEmpty(map)) {
					currentAccount.setAccountBelong(map.getRow(0).get(
							"account_belong"));
					currentAccount.setAppId(map.getRow(0).get("app_id"));
					currentAccount
							.setAppSecret(map.getRow(0).get("app_secret"));
					currentAccount.setIsValid(map.getRow(0).get("is_valid"));
					if (!map.getRow(0).get("account_belong").equals("1")) {// 1，运营商提供公众帐号。
						currentAccount.setOrgind(map.getRow(0).get("orgid"));
						currentAccount.setOrgname(map.getRow(0).get("orgname"));
					}
					if (req.getSession().getAttribute("orgid") != null) {
						currentAccount.setOrgind((String) req.getSession()
								.getAttribute("orgid"));
					}
					USERINFO_CATCH.put(openid, currentAccount);
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		Utils.Log(tag, "帐号类型为：" + currentAccount.getAccountBelong());
	}

	/**
	 * 文本消息处理
	 * 
	 * @param msg
	 *            解析后的消息对象
	 * @param req
	 *            HttpRequest对象
	 * @param rep
	 *            HttpResponse对象
	 */
	private void processTextMsg(Map<String, String> msg,HttpServletRequest req, HttpServletResponse rep) {
		
		try {
			
			String toUserName = msg.get("FromUserName");
			String fromUserName = msg.get("ToUserName");
			String content = msg.get("Content");
			
			// 记录接受的消息
			String msgFilterSQL = "SELECT ruleName,regstr FROM wmsgfilter WHERE orgid='"+currentAccount.getOrgind()+"' AND data_state=2";
			
			MapList regMap = db.query(msgFilterSQL);
			String ruleName = "";
			String regStr = "";
		
			for (int i = 0; i < regMap.size(); i++) {
				ruleName = regMap.getRow(i).get("rulename");
				regStr = regMap.getRow(i).get("regstr");
				if (ruleName != null && content != null&& content.matches(regStr)) {
					String mstInt="1";
					
					String inserRecvMsg = "INSERT INTO ws_recv_msg(msg_id,createtime,msg_type, to_user_name,from_user_name, \"content\",orgid,ruleName)"
							+ "VALUES('"
							+ msg.get("MsgId")
							+ "',CURRENT_TIMESTAMP(0) :: TIMESTAMP WITHOUT TIME ZONE ,"
							+ mstInt
							+ ",'"
							+ toUserName
							+ "','"
							+ fromUserName
							+ "','"
							+ content
							+ "','"
							+ currentAccount.getOrgind()
							+ "','" + ruleName + "')";
					db.execute(inserRecvMsg);
				}
			}

			if (currentAccount.getAccountBelong().equals("1")) {
				// 运营商提供公众帐号
				if (content.equalsIgnoreCase("ls")
						|| (currentAccount.getOrgind() == null && currentAccount
								.getReplayStatus() == 0)) {
					// 设置选择当前企业
					currentAccount.setReplayStatus(1);
					currentAccount.setOrgind(null);
					// 无用户缓存信息
					String sql = "SELECT orgname,orgid FROM aorg WHERE orgid in ("
							+ " SELECT detail.care_orgid FROM ws_member AS member "
							+ " RIGHT JOIN ws_mbdeatil AS detail "
							+ " ON member.member_code=detail.member_code "
							+ " WHERE member.openid='"
							+ toUserName
							+ "'"
							+ " AND detail.data_status!=4 )";
					MapList map = db.query(sql);
					if (!Checker.isEmpty(map)) {
						String contents = "";
						contents = "请选择您所关注的企业:\n";
						String url = hostUrl
								+ "/domain/wwd/weshop/member.jsp?openid="
								+ toUserName + "&token=" + token
								+ "&regist=true";
						for (int i = 0; i < map.size(); i++) {
							contents += (i + 1) + ":"
									+ map.getRow(i).get("orgname")
									+ "<a href='" + url + "&orgidp="
									+ map.getRow(i).get("orgid") + "'>登录</a>"
									+ "\n";
							currentAccount.getMenuId().put((i + 1) + "",
									map.getRow(i).get("orgid"));
						}
						contents += "\n输入ls列出您关注的所有企业。";
						Formatter f = new Formatter();
						f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
								fromUserName, System.currentTimeMillis(),
								"text", contents);
						Utils.Log(tag, f.toString());
						rep.getOutputStream().write(
								f.toString().getBytes("UTF-8"));
						rep.getOutputStream().flush();
						rep.getOutputStream().close();
						return;
					}
				} else if (content.matches("\\d+")
						&& currentAccount.getOrgind() == null
						&& currentAccount.getReplayStatus() == 1) {
					
					String orgid = currentAccount.getMenuId().get(content);
					
					currentAccount.setOrgind(orgid);
					
					String contents = getOrgnameByOrgid(orgid);
					
					Formatter f = new Formatter();
					
					if (contents != null && !"".equals(contents.trim())
							&& !"null".equalsIgnoreCase(contents)) {
						currentAccount.setReplayStatus(0);
						f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
								fromUserName, System.currentTimeMillis(),
								"text", "你当前选择的企业是:\n" + contents
										+ "\n输入hlep查询所有.");
					} else {
						currentAccount.setReplayStatus(1);
						f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
								fromUserName, System.currentTimeMillis(),
								"text", "输入有误，请选择正确的企业编号。");
					}
					
					rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
					
					return;
				
				} else if ((!content.matches("\\d+"))
						&& currentAccount.getOrgind() == null
						&& currentAccount.getReplayStatus() == 1) {
				
					String contents = "请输入正确的选项。";
					
					Formatter f = new Formatter();
					f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
							fromUserName, System.currentTimeMillis(), "text",
							contents);
					
					rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
					return;
				} else {
					// 有用户缓存数据
					if ("zzs".equalsIgnoreCase(content)) {// 发送制造商信息
						sendZZSMsg(msg, req, rep);
					} else if ("help".equalsIgnoreCase(content)) {// 发送帮助文件
						sendHelpMsg(msg, req, rep);
					} else {// 机器人自动回复
						autoReplay(msg, req, rep);
					}
				}

			} else {
				// 企业私有公众帐号  有用户缓存数据
				if ("zzs".equalsIgnoreCase(content)) {// 发送制造商信息
					sendZZSMsg(msg, req, rep);
					
				} else if ("help".equalsIgnoreCase(content)) {// 发送帮助文件
					sendHelpMsg(msg, req, rep);
				} else {// 机器人自动回复
					autoReplay(msg, req, rep);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 事件处理
	 * 
	 * @param msg
	 *            解析后的消息对象
	 * @param req
	 *            HttpRequest对象
	 * @param rep
	 *            HttpResponse对象
	 */
	private void processEvent(Map<String, String> msg, HttpServletRequest req,
			HttpServletResponse rep) {
		try {
			// 1,检查设置菜单
			if (!checkMenuSet(token)
					&& msg.get("Event").equalsIgnoreCase("subscribe")) {
				String acctoken = Utils.getAccessToken(token,
						currentAccount.getAppId(),
						currentAccount.getAppSecret());
				setMenuByToken(acctoken, token, currentAccount.getAppId(),
						currentAccount.getIsValid().equals("1"));
				String saveSQL = "UPDATE ws_public_accounts SET dev_account='"
						+ msg.get("FromUserName") + "' WHERE token='" + token
						+ "' ";
				
				db.execute(saveSQL);
				
				//会员同步取消此功能
				//syncMember();
			}
			// 2,判读是否有场景ID
			String sceneid = "-1";
			// if (msg.get("EventKey") != null||
			// msg.get("EventKey").indexOf("qrscene_") > -1) {//
			// 2014-03-12日，接口变化前
			if (msg.get("EventKey") != null
					&& !"VIEW".equalsIgnoreCase(msg.get("EventKey"))
					&& msg.get("EventKey").indexOf("qrscene_") > -1) {
				// EventKey 事件KEY值，qrscene_为前缀，后面为二维码的参数值
				sceneid = msg.get("EventKey").substring(
						msg.get("EventKey").indexOf("_") + 1,
						msg.get("EventKey").length());
				Utils.Log(tag, msg.get("EventKey"));
				if (sceneid != null && !"".equals(sceneid.trim())) {
					String orgid = getOrgidBySceneId(sceneid, token);
					if (currentAccount != null) {
						currentAccount.setOrgind(orgid);
					}
				}
			}
			// 3,判读事件类型，关注，扫描
			String event = msg.get("Event");
			if (event.equalsIgnoreCase("subscribe")) {// 关注
				// 1，运营商提供公众帐号，0或者空为企业私有公众帐号
				if (currentAccount.getAccountBelong().equals("1")) {
					// 运营商提供工作帐号关注事件
					if (sceneid != null && !sceneid.trim().equals("")) {
						
						//registerUser(token, msg.get("FromUserName"), sceneid);
						String replayMsg = sendWelcome(msg.get("FromUserName"),msg.get("ToUserName"), token, true);
						
						rep.getOutputStream().write(replayMsg.getBytes("UTF-8"));
						rep.getOutputStream().flush();
						rep.getOutputStream().close();
					}
					
				} else {
					// 企业私有公众帐号关注事件
					registerUser(token, msg.get("FromUserName"));
					String replayMsg = sendWelcome(msg.get("FromUserName"),
							msg.get("ToUserName"), token, false);
					rep.getOutputStream().write(replayMsg.getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
				}

				// 获取用户数据。
				// 验证公众帐号是否为认证公众帐号

				if (currentAccount.getIsValid().equals("1")) {
					String accessToken = Utils.getAccessToken(token,
							currentAccount.getAppId(),
							currentAccount.getAppSecret());
					String openid = msg.get("FromUserName");
					JSONObject userInfo = Utils
							.getUserInfo(accessToken, openid);
					String checkMemberSQL = "SELECT nickname FROM ws_member  WHERE openid='"
							+ openid + "' ";
					ResultSet rset = db.getResultSet(checkMemberSQL);
					if (rset.next()) {
						// update
						String gender = "0";
						if (userInfo.toString().contains("sex")) {
							gender = userInfo.getString("sex");
						}
						String updateMemberSQL = "UPDATE ws_member SET "
								+ "city='"
								+ userInfo.getString("city")
								+ "',"
								+ "gender='"
								+ gender
								+ "',"
								+ "country='"
								+ userInfo.getString("country")
								+ "',"
								+ "provice='"
								+ userInfo.getString("province")
								+ "',"
								+ "language='"
								+ userInfo.getString("language")
								+ "',"
								+ "headimgurl='"
								+ userInfo.getString("headimgurl")
								+ "',"
								+ "subscribe_time='"
								+ userInfo.getString("subscribe_time")
								+ "' "
								+ "WHERE openid='" + openid + "' ";
						db.execute(updateMemberSQL);
					}
					System.out.println(userInfo.toString());
				}

				return;
			}
			//
			if (event.equalsIgnoreCase("SCAN")) {// 扫描
				// 1，运营商提供公众帐号，0或者空为企业私有公众帐号
				if (currentAccount.getAccountBelong().equals("1")) {
					// 运营商提供工作帐号扫描事件
					//registerUser(token, msg.get("FromUserName"), sceneid);
					String replayMsg = sendWelcome(msg.get("FromUserName"),
							msg.get("ToUserName"), token, true);
					rep.getOutputStream().write(replayMsg.getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
					return;
				} else {
					// 企业私有公众帐号扫描事件
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (WeChatInfaceException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判读是否设置的了菜单
	 * 
	 * @param token
	 * @return true，设置了菜单，false没有设置菜单
	 */
	private boolean checkMenuSet(String token) {
		boolean result = false;
		String checkDevAcc = "SELECT dev_account FROM ws_public_accounts  WHERE token='"
				+ token + "' AND dev_account IS NOT NULL ";
		try {
			MapList map = db.query(checkDevAcc);
			if (!Checker.isEmpty(map))
				result = true;
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 同步会员
	 */
	private void syncMember() {
		/*
		new Thread() {
			@Override
			public void run() {
				
				String acctoken = "";
				;
				try {
					acctoken = Utils.getAccessToken(setMenuTokenURL,
							currentAccount.getAppId(),
							currentAccount.getAppSecret());
				} catch (WeChatInfaceException e1) {
					e1.printStackTrace();
				}
				// 获取关注者列表
				JSONArray array = Utils.getUserList(acctoken);
				// System.out.println("获取关注者列表：" + array);
				try {
					for (int i = 0; i < array.length(); i++) {
						String openid = array.getString(i);
						// 同步用户
						JSONObject object = Utils.getUserInfo(acctoken, openid);
						String querySQL = "select orgid,public_id from ws_public_accounts where token='"
								+ token + "'";
						MapList mapStr = db.query(querySQL);
						String orgid = mapStr.getRow(0).get("orgid");
						String public_id = mapStr.getRow(0).get("public_id");
						// subscribe：用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，
						// 拉取不到其余信息.(data_status取值：1 启用；2 冻结)。
						String data_status = object.get("subscribe").toString();
						String open_id = object.get("openid").toString();
						String nickname = object.get("nickname").toString();// 用户昵称
						if (nickname.indexOf("'") >= 0
								|| nickname.contains("\"")) {
							nickname = nickname.replace("'", "`");
							nickname = nickname.replaceAll("\"", "`");
						}
						String gender = object.get("sex").toString();// 用户性别

						// 获取当前系统时间
						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String nowdate = df.format(new Date());

						String sql = "insert into WS_MEMBER(orgid,nickname,gender,data_status,create_time,openid,public_id,group_id) values"
								+ "('"
								+ orgid
								+ "','"
								+ nickname
								+ "','"
								+ gender
								+ "','"
								+ data_status
								+ "','"
								+ nowdate
								+ "','"
								+ open_id
								+ "','"
								+ public_id
								+ "','1') ";
						// System.out.println("插入的SQL语句：" + sql);
						db.execute(sql);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();*/
	}

	/**
	 * 检查公众帐号类型
	 * 
	 * @param token
	 * @return
	 */
	private boolean checkAccountBelong(String token, String openid) {

		String sql = "SELECT org.orgname,wpa.* "
				+ " FROM ws_public_accounts AS wpa "
				+ " LEFT JOIN aorg AS org ON org.orgid=wpa.orgid WHERE token='"
				+ token + "' ";
		boolean result = false;
		try {
			MapList map = db.query(sql);
			if (Checker.isEmpty(map)) {
				result = false;
			} else if (currentAccount.getAccountBelong().equals("1")) {
				result = true;
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据机构id获取机构名称
	 * 
	 * @param orgid
	 * @return 机构id
	 */
	private String getOrgnameByOrgid(String orgid) {
		String orgname = "";
		try {
			String sql = "SELECT orgname FROM aorg WHERE orgid='" + orgid + "'";
			MapList map = db.query(sql);
			if (!Checker.isEmpty(map)) {
				orgname = map.getRow(0).get("orgname");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return orgname;
	}

	/**
	 * 根据场景ID和token获取机构名称id
	 * 
	 * @param secneid
	 * @return
	 */
	public String getOrgidBySceneId(String sceneid, String token) {
		String result = "";
		String sql = "SELECT wpa.orgid FROM ws_public_accounts AS wpa "
				+ "	LEFT JOIN ws_actdetal AS wa ON "
				+ "	wpa.public_id=wa.public_id WHERE wpa.token='" + token
				+ "' " + "   AND wa.scene_id=" + sceneid
				+ " AND wpa.app_id IS NOT NULL";
		try {
			MapList map = db.query(sql);
			if (!Checker.isEmpty(map)) {
				result = map.getRow(0).get("orgid");
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 公用公众帐号用户注册
	 * 
	 * @param token
	 *            token
	 * @param openId
	 *            openid
	 * @param sceneid
	 *            场景id
	 */
	/**
	public void registerUser(String token, String openId, String sceneid) {
		JSONArray jsar = registerUser(token, openId);
		try {
			String member_code = jsar.getJSONObject(0).getString("MEMBER_CODE");
			if (sceneid == null || "".equals(sceneid.trim())) {
				sceneid = "-1";
			}
			// 获取是否已经关注此企业
			String findCoreOrgSQL = "SELECT * FROM ws_mbdeatil WHERE member_code "
					+ " IS NOT NULL AND member_code="
					+ member_code
					+ " AND care_sceneid=" + sceneid;
			MapList map = db.query(findCoreOrgSQL);
			*//**
			 * 数据状态 1，启用； 2，冻结； 3，锁定；锁定帐号即为在超出公用公众帐号后的用户。
			 * 4,取消关注，取消关注后的企业将不在企业列表中展示。
			 *//*
			int dataStatus = 1;
			String orgid = getOrgidBySceneId(sceneid, token);
			*//** 检查是否超过最大会员数量，如果超过则状态为锁定状态 **//*
			if (checkMaxMember(orgid)) {
				dataStatus = 3;
			}
			if (Checker.isEmpty(map)) {// 当没有关注此企业是，为会员增加关注
				String isertCoreOrg = "INSERT INTO ws_mbdeatil "
						+ "(member_code,care_orgid,care_sceneid,data_status,firstsubscribe) "
						+ "VALUES('"
						+ member_code
						+ "','"
						+ orgid
						+ "',"
						+ sceneid + "," + dataStatus + ",1);";
				
				db.execute(isertCoreOrg);
				
			} else {
				// UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
				String isertCoreOrg = "UPDATE ws_mbdeatil SET data_status=1 WHERE member_code='"
						+ member_code + "'" + " AND care_sceneid=" + sceneid;
				db.execute(isertCoreOrg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
**/
	/**
	 * 根据机构ID查询是否操作最大管理会员数量
	 * 
	 * @param orgid
	 * @return 超过返回true，否则返回false
	 */
	private boolean checkMaxMember(String orgid) {
		boolean result = false;
		try {
			String sql = "SELECT max_members FROM ws_enterprise_month_tariff WHERE monthly_fee_id IN "
					+ " (SELECT  monthly_fee_id FROM ws_org_baseinfo WHERE orgid='"
					+ orgid + "')";
			MapList map = db.query(sql);
			int max = 0;
			if (!Checker.isEmpty(map)) {
				max = map.getRow(0).getInt("max_members", -1);
			}
			String curretnMemberSQL = "SELECT count(*) FROM ws_mbdeatil WHERE care_orgid='"
					+ orgid + "'";
			int currentMax = 0;
			map = db.query(curretnMemberSQL);
			if (!Checker.isEmpty(map)) {
				currentMax = map.getRow(0).getInt("count", -1);
			}
			result = currentMax > max ? true : false;
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 发送欢迎次
	 * <ul>
	 * <ol>
	 * 1,判断公众帐号类型:A，运营商提供公众帐号。B，客户提供公众帐号
	 * </ol>
	 * <ol>
	 * 2,如果为客户提供公众帐号，则回复内容不变。
	 * </ol>
	 * <ol>
	 * 3,如果是运营商提供公众帐号，则先让用户选择机构。
	 * </ol>
	 * </ul>
	 * 
	 * @param toUserName
	 * @param fromUserName
	 * @param token
	 * @param flage
	 */
	private String sendWelcome(String toUserName, String fromUserName,
			String token, boolean flage) {
		String getExplain = null;
		try {
			if (flage) {
				getExplain = "SELECT orgname FROM aorg WHERE orgid='"
						+ currentAccount.getOrgind() + "'";
				MapList map = db.query(getExplain);
				if (Checker.isEmpty(map))
					return null;
				String url = hostUrl + "/domain/wwd/weshop/member.jsp?openid="
						+ toUserName + "&token=" + token + "&regist=true";
				String content = "欢迎关注" + map.getRow(0).get(0)
						+ "请点击此处登录<a href='" + url + "&orgidp="
						+ currentAccount.getOrgind() + "'>登录</a>\n"
						+ "回复help查看帮助；回复zzs查看制作商；回复yz进行会员卡登录；"
						+ "回复ls查看该服务账号下，您所关注的所有企业列表。";// 欢迎词
				Formatter f = new Formatter();
				f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
						fromUserName, System.currentTimeMillis(), "text",
						content);
				Utils.Log(tag, f.toString());
				return f.toString();
			} else {
				getExplain = "SELECT * FROM ws_public_accounts WHERE token='"
						+ token + "'";
				MapList map = db.query(getExplain);
				if (Checker.isEmpty(map))
					return null;
				String content = map.getRow(0).get("welcomeword");// 欢迎词
				String url = hostUrl + "/domain/wwd/weshop/member.jsp?openid="
						+ toUserName + "&token=" + token + "&regist=true";
				/**
				content += currentAccount.getOrgname() + "请点击此处进行<a href=\""
						+ url + "&orgidp=" + map.getRow(0).get("orgid")
						+ "\">登录</a>验证。回复help查看帮助；回复zzs查看制作商；回复yz进行会员卡登录。";
				**/
				Formatter f = new Formatter();
				f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
						fromUserName, System.currentTimeMillis(), "text",
						content);
				Utils.Log(tag, f.toString());
				return f.toString();
			}

		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 机器人自动回复功能 机器人匹配规则，每天回复消息机器人只回复一条消息， 匹配规则为:先从全匹配去查找是否有对应消息，如果没有，
	 * 则转到包含中去查找相应的消息，如果没有，回复无匹配模式消息。</br> 匹配规则：
	 * <ul>
	 * <ol>
	 * 1，全匹配
	 * </ol>
	 * <ol>
	 * 2，包含
	 * </ol>
	 * <ol>
	 * 3，自定义
	 * </ol>
	 * </ul>
	 * 
	 * @param msg
	 *            消息集合MAP
	 * @param rep
	 *            HttpServletResponse
	 * @return 如果找到匹配规则，则自动回复返回true，否则返回false
	 */
	private boolean autoReplay(Map<String, String> msg, HttpServletRequest req,
			HttpServletResponse rep) throws JDBCException {
		// 1,获取文本消息类型
		String content = msg.get("Content");
		// System.out.println(content);
		// 2,查找匹配规则
		// 全匹配规则SQL
		String sqlEqu = "SELECT * FROM ws_auto_replay_rule AS ru                 "
				+ "	LEFT JOIN ws_send_msg AS msg ON ru.rule_id=msg.rule_id "
				+ "	LEFT JOIN ws_public_accounts AS pa ON pa.public_id=ru.public_id "
				+ "	WHERE match_moudle=1  AND upper(ru.match_key)=upper('"
				+ content
				+ "')  "
				+ "	AND pa.orgid='"
				+ currentAccount.getOrgind() + "' ORDER BY msg.content ";
		// 包含
		// String sqlLike =
		// "SELECT * FROM ws_auto_replay_rule AS ru                 "
		// + "	LEFT JOIN ws_send_msg AS msg ON ru.rule_id=msg.rule_id "
		// + "	LEFT JOIN ws_public_accounts AS pa ON pa.public_id=ru.public_id "
		// + "	WHERE match_moudle=2  AND upper('%'||ru.match_key||'%') "
		// + " SIMILAR TO  upper('%' ||'"+content+"'|| '%')"
		// + "	AND pa.orgid='"
		// + currentAccount.getOrgind()
		// + "' ORDER BY msg.content";
		String sqlLike = "SELECT * FROM ws_auto_replay_rule AS ru                 	        "
				+ "	LEFT JOIN ws_send_msg AS msg ON ru.rule_id=msg.rule_id 	        "
				+ "	LEFT JOIN ws_public_accounts AS pa ON pa.public_id=ru.public_id "
				+ "	WHERE match_moudle=2  AND (                                     "
				+ "	upper('%"
				+ content
				+ "%') LIKE (    "
				+ "	SELECT '%'||upper(ru.match_key)||'%' FROM ws_auto_replay_rule ru1      "
				+ "	WHERE ru1.rule_id=ru.rule_id))                                  "
				+ "	AND pa.orgid='"
				+ currentAccount.getOrgind()
				+ "' ORDER BY msg.content ";
		MapList map = db.query(sqlEqu);

		if (!Checker.isEmpty(map)) {// 全匹配模式回复
			// 3,处理发送消息
			// Utils.Log(tag,"全匹配模式回复");
			sendMsage(map, rep, msg.get("FromUserName"), msg.get("ToUserName"));
			return true;
		} else {
			map = db.query(sqlLike);
			// Utils.Log(tag," 包含模式回复:"+sqlLike);
			if (!Checker.isEmpty(map)) {// 包含匹配模式

				// 3,处理发送消息
				sendMsage(map, rep, msg.get("FromUserName"),
						msg.get("ToUserName"));
				return true;
			} else if ("yz".equalsIgnoreCase(content)) {// 验证消息
				sendVialidMsg(req, rep, msg);
			} else {// 无匹配模式
				String noMatchSQL = "SELECT ru.match_key,ru.rule_name  FROM ws_auto_replay_rule AS ru     			"
						+ "	LEFT JOIN ws_send_msg AS msg ON ru.rule_id=msg.rule_id          "
						+ "	LEFT JOIN ws_public_accounts AS pa ON pa.public_id=ru.public_id "
						+ "	WHERE  pa.orgid='"
						+ currentAccount.getOrgind()
						+ "'";
				// Utils.Log(tag,"无匹配模式:"+noMatchSQL);
				map = db.query(noMatchSQL);
				sendNoMatchMsg(map, rep, msg.get("FromUserName"),
						msg.get("ToUserName"));
			}
		}
		return false;
	}

	/**
	 * 发送制作商信息
	 * 
	 * @param msg
	 * @param req
	 * @param rep
	 */
	public void sendZZSMsg(Map<String, String> msg, HttpServletRequest req,
			HttpServletResponse rep) {
		String content = " ";
		Formatter f = new Formatter();
		f.format(Message.MessageTemplet.TEXT_MSG, msg.get("FromUserName"),
				msg.get("ToUserName"), System.currentTimeMillis(), "text",
				content);
		try {
			rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
			rep.getOutputStream().flush();
			rep.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送帮助消息 当前token下面对应的所有全匹配的消息
	 * 
	 * @param msg
	 * @param req
	 * @param rep
	 */
	public void sendHelpMsg(Map<String, String> msg, HttpServletRequest req,
			HttpServletResponse rep) {
		// 全匹配规则SQL
		String noMatchSQL = "SELECT ru.msg_type, ru.match_key,ru.rule_name  FROM ws_auto_replay_rule AS ru     			"
				+ "	LEFT JOIN ws_send_msg AS msg ON ru.rule_id=msg.rule_id          "
				+ "	LEFT JOIN ws_public_accounts AS pa ON pa.public_id=ru.public_id "
				+ "	WHERE  pa.orgid='" + currentAccount.getOrgind() + "'";
		MapList map = null;
		try {
			map = db.query(noMatchSQL);
			String content = "";
			if (!Checker.isEmpty(map)) {// 全匹配模式回复
				// 3,处理发送消息
				// sendMsage(map,rep,msg.get("FromUserName"),msg.get("ToUserName"));
				for (int i = 0; i < map.size(); i++) {
					Row row = map.getRow(i);
					// if ("1".equals(row.get("msg_type"))) {// 文本消息
					content += row.get("match_key") + "     "
							+ row.get("rule_name") + " \n";
					// }
				}
			} else {
				content = "";
			}
			Formatter f = new Formatter();
			f.format(Message.MessageTemplet.TEXT_MSG, msg.get("FromUserName"),
					msg.get("ToUserName"), System.currentTimeMillis(), "text",
					content);
			rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
			rep.getOutputStream().flush();
			rep.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送验证消息
	 * 
	 * @param req
	 * @param rep
	 */
	private void sendVialidMsg(HttpServletRequest req, HttpServletResponse rep,
			Map<String, String> msg) {
		JSONArray userInfo = registerUser(req.getParameter("token"),
				msg.get("FromUserName"));
		String toUserName = msg.get("FromUserName");// 回复消息的发送者便是接受消息的接收者
		String formUserName = msg.get("ToUserName");// 回复信息的接收者便是发送消息的发送者
		String url = hostUrl + "/domain/wwd/weshop/member.jsp?openid="
				+ toUserName + "&token=" + req.getParameter("token")
				+ "&regist=true";
		String orgid = currentAccount.getOrgind();
		if (orgid != null) {
			url = url + "&orgidp=" + orgid;
		}
		String content = "验证成功，请您点击此处<a href=\"" + url + "\">登录</a>";
		if (userInfo != null) {
			Formatter f = new Formatter();
			f.format(Message.MessageTemplet.TEXT_MSG, toUserName, formUserName,
					System.currentTimeMillis(), "text", content);
			// System.out.println("sendMsage:" + f.toString());
			try {
				rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
				rep.getOutputStream().flush();
				rep.getOutputStream().close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据OpenId和token注册新用户
	 * 
	 * @param openId
	 * @param token
	 * @return
	 */
	
	private JSONArray registerUser(String token, String openId) {
//		try {
			String orgid = Utils.getOrgId(token);
			
			//通过OpenID检查用户是否已存在
			String findViewSQL = "SELECT * FROM ws_member AS member         "
					+ "	LEFT JOIN ws_public_accounts AS acc    "
					+ "	ON acc.orgid=member.orgid              "
					+ "	WHERE  member.openid='" + openId + "'      "
					+ "	AND acc.token='" + token + "'          ";
			
			/*MapList map = db.query(findViewSQL);
			
			String getPublicIdSQL = "SELECT * FROM  ws_public_accounts WHERE token='"
					+ token + "' ";*/
			return DatabaseAccess.query(findViewSQL);
			/*if (Checker.isEmpty(map)) {//用户不存在
				String nickName = "";
				int sex = 0;
				
				MapList publicidMap = db.query(getPublicIdSQL);
				try {
					if (!Checker.isEmpty(publicidMap)
							&& publicidMap.getRow(0).getInt("is_valid", -1) == 1) {
						String accessToken = Utils.getAccessToken(token,
								publicidMap.getRow(0).get("app_id"),
								publicidMap.getRow(0).get("app_secret"));
						
						JSONObject userInfo = Utils.getUserInfo(accessToken,openId);
						Utils.Log(tag, userInfo);
						nickName = userInfo.getString("nickname");
						sex = Integer.parseInt(userInfo.getString("sex"));
					}
				} catch (WeChatInfaceException e) {
					e.printStackTrace();
				}
				String paswd = Utils.getRandomStr(5);
				Table table = new Table("wwd", "WS_MEMBER");
				TableRow tr = table.addInsertRow();
				tr.setValue("orgid", orgid);
				tr.setValue("wshop_name", openId);
				tr.setValue("openid", openId);
				tr.setValue("group_id", 1);
				tr.setValue("wshaop_password", DigestUtils.md5Hex(paswd));
				tr.setValue("data_status", 1);
				tr.setValue("public_id", publicidMap.getRow(0).get("public_id"));
				tr.setValue("nickname", nickName);
				tr.setValue("gender", sex);
				//首次关注
				tr.setValue("firstsubscribe", 1);
				db.save(table);
				
				String membercode=tr.getValue("member_code");
				//更新任务
				updateUserTask(db,membercode,orgid);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}

	/**
	 * 更新用户任务数据
	 * @param membercode 会员ID
	 * @param orgid  机构ID
	 */
	private void updateUserTask(DB db,String membercode, String orgid) {
		String getTaskSQL="SELECT * FROM p2p_enterpriseTask AS et "
				+ " LEFT JOIN p2p_taskTemplate AS tt "
				+ " ON et.tasktemplateid=tt.id "
				+ " WHERE et.orgid='"+orgid+"'  "
				+ " AND tt.templatestate='1'";
		
	}

	/**
	 * 发送消息
	 * 
	 * @param map
	 * @param out2
	 * @param 消息接收者
	 * @param 消息发送者
	 * @throws JSONException
	 * @throws WeChatInfaceException
	 */
	private void sendMsage(MapList map, HttpServletResponse rep,
			String toUserName, String formUserName) {
		// 1 文本消息;2 图片消息;3 语音消息;4 视频消息;5 音乐消息;6 图文消息 一期只做1，2，6
		if (!Checker.isEmpty(map)) {
			Row tr = map.getRow(0);
			String msgType = tr.get("msg_type");
			if ("1".equals(msgType)) {// 文本消息
				String content = tr.get("content");
				Formatter f = new Formatter();
				f.format(Message.MessageTemplet.TEXT_MSG, toUserName,
						formUserName, System.currentTimeMillis(), "text",
						content);
				// System.out.println("sendMsage:" + f.toString());
				try {
					rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if ("2".equals(msgType)) {// 图片消息
				try {
					String accessToken = Utils.getAccessToken(tr.get("token"),
							tr.get("app_id"), tr.get("app_secret"));
					String imgMsgurl = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token="
							+ accessToken + "&type=image";
					String filesName = "";
					String media_id = "";
					String rule_id = tr.get("rule_id");
					String filePath = Path.getRootPath() + File.separator
							+ "files" + File.separator + "WS_AUTO_REPLAY_RULE"
							+ File.separator + rule_id + File.separator
							+ "image";
					File file = new File(filePath);
					for (String fileName : file.list()) {
						filesName = fileName;
					}
					if (filesName != null && filesName.length() != 0) {
						filesName = filePath + File.separator + filesName;
					}
					if (filesName != null || filesName != "") {
						// 上传图片至微信服务器，返回media_id
						// TODO 增加缓存机制
						media_id = Utils.postFile(accessToken, imgMsgurl,
								new File(filesName));
					}
					Formatter f = new Formatter();
					f.format(Message.MessageTemplet.IMAGE_MSG, toUserName,
							formUserName, System.currentTimeMillis(), "image",
							media_id);
					rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (WeChatInfaceException e) {
					e.printStackTrace();
				}
			}
			if ("6".equals(msgType)) {
				try {
					String rule_id = tr.get("rule_id");
					String title = tr.get("title");
					String description = tr.get("description");
					String url = tr.get("url");
					// 图片连接地址
					String picUrl = File.separator + "files" + File.separator
							+ "WS_AUTO_REPLAY_RULE" + File.separator + rule_id
							+ File.separator + "imagecover";
					String filePath = Path.getRootPath() + picUrl;
					File file = new File(filePath);
					for (String fileName : file.list()) {
						picUrl = picUrl + File.separator + fileName;
					}
					if (picUrl != null || picUrl != "") {
						picUrl = hostUrl + picUrl;
					}
					// 发送图文消息
					Formatter f = new Formatter();
					f.format(Message.MessageTemplet.NEWS_MSG, toUserName,
							formUserName, System.currentTimeMillis(), "news",
							"1", title, description, picUrl.replace("\\", "/"),
							url);
					rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
					rep.getOutputStream().flush();
					rep.getOutputStream().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 发送无匹配模式信息
	 * 
	 * @param map
	 *            token对应的菜单数据集合
	 * @param rep
	 *            HttpServlerReponse
	 * @param toUserName
	 *            消息接收者
	 * @param formUserName
	 *            消息发送者
	 */
	public void sendNoMatchMsg(MapList map, HttpServletResponse rep,
			String toUserName, String formUserName) {
		// match_key rule_name
		String content = "";
		if (!Checker.isEmpty(map)) {
			for (int i = 0; i < map.size(); i++) {
				Row row = map.getRow(i);
				if ("1".equals(row.get("msg_type"))) {// 文本消息
					content += "" + row.get("match_key") + "    "
							+ row.get("rule_name") + " \n";
				}
			}
		}
		Formatter f = new Formatter();
		f.format(Message.MessageTemplet.TEXT_MSG, toUserName, formUserName,
				System.currentTimeMillis(), "text", content);
		// System.out.println("sendMsage:" + f.toString());
		try {
			rep.getOutputStream().write(f.toString().getBytes("UTF-8"));
			rep.getOutputStream().flush();
			rep.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存开发者帐号信息(可以当公众帐号使用)
	 * 
	 * @param token
	 *            系统接口token
	 * @param devAccount
	 */
	public void saveDevAccount(final String token, String devAccount)
			throws WeChatInfaceException {
		try {
			// System.out.println("saveDevAccount");
			String checkDevAcc = "SELECT dev_account FROM ws_public_accounts  WHERE token='"
					+ token + "' AND dev_account IS NOT NULL ";
			// System.out.println(checkDevAcc);
			MapList map = db.query(checkDevAcc);
			if (Checker.isEmpty(map)) {// 检测是否保存开发者帐号
				// 如果没有保存开发者帐号则保存开发者帐号，并设置菜单
				// 设置用户菜单 是否通过微信认证：1，是，2否
				String getAppIdSQL = "SELECT app_id,app_secret,is_valid,orgid FROM ws_public_accounts WHERE token='"
						+ token + "' AND app_id IS NOT NULL ";
				MapList maps = db.query(getAppIdSQL);
				if (!Checker.isEmpty(maps)) {
					final String acctoken = Utils.getAccessToken(token, maps
							.getRow(0).get("app_id"),
							maps.getRow(0).get("app_secret"));
					// System.out.println("获取AccessToken成功：" + acctoken);
					boolean author = false;
					if (maps.getRow(0).getInt("is_valid", 2) == 1) {
						author = true;
					}
					setMenuByToken(acctoken, token, maps.getRow(0)
							.get("app_id"), author);
					new Thread() {
						@Override
						public void run() {
							// 获取关注者列表
							JSONArray array = Utils.getUserList(acctoken);
							// System.out.println("获取关注者列表：" + array);
							try {
								for (int i = 0; i < array.length(); i++) {
									String openid = array.getString(i);
									// 同步用户
									JSONObject object = Utils.getUserInfo(
											acctoken, openid);
									String querySQL = "select orgid,public_id from ws_public_accounts where token='"
											+ token + "'";
									MapList mapStr = db.query(querySQL);
									String orgid = mapStr.getRow(0)
											.get("orgid");
									String public_id = mapStr.getRow(0).get(
											"public_id");
									// subscribe：用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，
									// 拉取不到其余信息.(data_status取值：1 启用；2 冻结)。
									String data_status = object
											.get("subscribe").toString();
									String open_id = object.get("openid")
											.toString();
									String nickname = object.get("nickname")
											.toString();// 用户昵称
									if (nickname.indexOf("'") >= 0
											|| nickname.contains("\"")) {
										nickname = nickname.replace("'", "`");
										nickname = nickname.replaceAll("\"",
												"`");
									}
									String gender = object.get("sex")
											.toString();// 用户性别

									// 获取当前系统时间
									SimpleDateFormat df = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									String nowdate = df.format(new Date());

									String sql = "insert into WS_MEMBER(orgid,nickname,gender,data_status,create_time,openid,public_id,group_id) values"
											+ "('"
											+ orgid
											+ "','"
											+ nickname
											+ "','"
											+ gender
											+ "','"
											+ data_status
											+ "','"
											+ nowdate
											+ "','"
											+ open_id
											+ "','"
											+ public_id + "','1') ";
									// System.out.println("插入的SQL语句：" + sql);
									db.execute(sql);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
				}
				// TODO 发送机器人规则
				String saveSQL = "UPDATE ws_public_accounts SET dev_account='"
						+ devAccount + "' WHERE token='" + token + "' ";
				if (db.execute(saveSQL) > 1)
					throw new JDBCException("保存开发者帐号失败！TOKEN：" + token);
			} else {
			}
		} catch (Exception e) {
			throw new WeChatInfaceException("保存开发者帐号出错");
		}
	}

	/**
	 * 获取菜单
	 * @return
	 */
	public static MapList getMenusMap(){
		
		MapList result=null;
		
		try{
			String sql="SELECT id,menuname,menucode FROM am_mobliemenu WHERE upid='1' ORDER BY menusort limit 3";
			
			DB db=DBFactory.getDB();
			
			result=db.query(sql);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 设置菜单选项
	 * 
	 * @param accToken
	 *            accessToken
	 * @param token
	 *            帐号对应token 在微网站系统必须唯一
	 * @param appid
	 *            帐号对应APPID
	 * @param isAuthor
	 *            否认证
	 * @param orgid
	 * @return
	 */
	public static boolean setMenuByToken(String acctoken, String token,String appid, boolean isAuthor) throws WeChatInfaceException {
		
		CloseableHttpResponse response = null;
		
		try {
		
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			RequestConfig reqConfig = RequestConfig.custom()
					.setSocketTimeout(Constant.WE_CHAT_SOCKET_TIMEOUT)
					.setConnectTimeout(Constant.WE_CHAT_CONNECT_TIMEOUT)
					.build();
			
			Formatter f = new Formatter();
			
			f.format(setMenuTokenURL, acctoken);
			
			HttpPost httpPost = new HttpPost(f.toString());
			
			httpPost.setConfig(reqConfig);

			//获取前3个菜单
			MapList menuMap=getMenusMap();
			
			if(Checker.isEmpty(menuMap)||menuMap.size()<3){
				throw new Exception("微信端的菜单应该为3个,请检查菜单。");
			}
			
			//找服务、商城、会员卡
			String serverURL = "";//找服务
			String weshopURL = "";//商城
			String memberURL="";//会员卡
			
//			String orgid = Utils.getOrgId(token);
			
			if(hostUrl==null||"null".equalsIgnoreCase(hostUrl)){
				
				WeChatInfaceServer.getInstance().initProperties();
			}
			
			// cinfoMenu=DatabaseAccess.query(cinfoMequSQL).toString();
			
			if (isAuthor) {// 认证公众号访问地址
				
				f = new Formatter();
				
				////找服务、商城、会员卡
				serverURL=hostUrl+"/weshop/"+
						Constant.WE_CAHT_AUTHOR_OAUTH2
						+".do?menu="+menuMap.getRow(0).get("MENUCODE")+"&token="+token;
				
				weshopURL = hostUrl + "/weshop/"
						+ Constant.WE_CAHT_AUTHOR_OAUTH2
						+ ".do?menu="+menuMap.getRow(1).get("MENUCODE")+"&token=" + token;
				
				memberURL = hostUrl + "/weshop/"
						+ Constant.WE_CAHT_AUTHOR_OAUTH2
						+ ".do?menu="+menuMap.getRow(2).get("MENUCODE")+"&token=" + token;
				
			} else {
				// 未认认证服务号地址
				serverURL=hostUrl+"/am_mobile/index.html?menu="+menuMap.getRow(0).get("MENUCODE");
				weshopURL=hostUrl+"/am_mobile/index.html?menu="+menuMap.getRow(1).get("MENUCODE");
				memberURL=hostUrl+"/am_mobile/index.html?menu="+menuMap.getRow(2).get("MENUCODE");
				}
			
			f = new Formatter();
			
			f.format(meunStr, 
					menuMap.getRow(0).get("MENUNAME"),serverURL,//第一个菜单名称，菜单menucode
					menuMap.getRow(1).get("MENUNAME"),weshopURL, //第二个菜单名称，菜单menucode
					menuMap.getRow(2).get("MENUNAME"),memberURL);//第三个菜单名称，菜单menucode
			
			Utils.Log("setMenuByToken", "设置菜单字符串:" + f.toString());
			
			HttpEntity entity = EntityBuilder.create()
					.setContentEncoding("GBK")
					.setBinary(f.toString().getBytes("UTF-8")).build();
			
			httpPost.setEntity(entity);
			
			response = httpClient.execute(httpPost);

			HttpEntity resEntity = response.getEntity();
			
			JSONObject result = new JSONObject();
			
			if (resEntity != null) {
				long len = resEntity.getContentLength();
				if (len != -1) {
					result = new JSONObject(EntityUtils.toString(resEntity));
				}
				if ("ok".equals(result.getString("errmsg"))) {
					return true;
				}
			}
		} catch (Exception e) {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return false;
	}

	
	public void initProperties(){
		try {
			db = DBFactory.getDB();
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"host.properties"));
			hostUrl = properties.getProperty("host", "http://182.92.99.124");
			OAUTH2_URL = properties.getProperty("oauth2_url", "182.92.99.124");
		} catch (Exception e) {
			System.out.println("新建WeChatInfaceServer出错！");
			e.printStackTrace();
		}
	}
}