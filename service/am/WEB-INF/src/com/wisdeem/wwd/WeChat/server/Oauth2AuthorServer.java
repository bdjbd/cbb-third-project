package com.wisdeem.wwd.WeChat.server;

import java.io.IOException;
import java.util.Formatter;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.tools.DatabaseAccess;
import com.wisdeem.wwd.Constant;
import com.wisdeem.wwd.WeChat.Utils;

/**
 *   说明:
 *   Oauth2.0认证业务处理
 *   <li>第一步：用户同意授权，获取code</li>
 *   <li>第二步：通过code换取网页授权access_token</li>
 *  <hr/>
 *  <h3>业务流向:</h3>
 *  认证后的服务好采访访问URL类似下面:</br>
 *  会员卡：http://xxx/author_oauth2.do?menu=member&token=token</br>
 *  商城：   http://xxx/author_oauth2.do?menu=weshop&token=token</br>
 *  在访问以上任意一个地址的时候，如果在session中没有保存用户信息，则弹出需要用户验证页面。
 *  在用户同意后将从微信获取的用户信息(JSONObject)保存到session中。以便下次查询。<br>
 *  获取用户关注企业，并查询用户关注企业的公众帐号类型，数据库中<code>account_belong</code>字段确认是否为运营商提供 公众帐号:<br/>
 *  <li>如果是企业提供公众帐号，则直接跳转到对应的商城。或者会员卡。</li>
 *  <li>如果是运营商提供公众帐号</li>
 *  	<ul>
 *  	<ol>1:首先查询session中是否有用户当前操作企业，如果没有，则查询此用户关注的企业，如果有，或者此用户只关注了一个企业，直接 重定向用户到该企业的商城。</ol>
 *  	<ol>2:如果此用户关注了多个企业，则先从Session中查询用户当前选择的企业，如果没有选择的企业，则列出此用户关注的企业，以便用户选择。
 *  		当用户选择后，将数据保存到Session中，用来记忆用户选择，方便用户。</ol>
 *  	</ul>
 *  </li>
 *   @creator	岳斌
 *   @create 	Jan 23, 2014 
 *   @version	$Id
 */
public class Oauth2AuthorServer {
	
	private DB db;
	
	private static Oauth2AuthorServer oauth2;
	
	/**微商店部署主机地址**/
	public static String hostUrl;
	public static Properties properties;
	/**公众帐号需要OAuth2.0认证的域名**/
	public static String OAUTH2_URL="";
	/**
	 * 用户同意授权，获取code URL
	 * 第一个参数 公众帐号的APPID，第二个参数 使用OAuth2认证返回结果处理地址，第三个参数 公众帐号在我们系统中的token
	 **/
	private static String OAuth2MenuStr="https://open.weixin.qq.com/connect/oauth2/authorize?" +
			"appid=%s&redirect_uri=%s" +
			"&response_type=code&scope=snsapi_userinfo" +
			"&state=%s#wechat_redirect";
	

	/**
	 * 获取Oauth2.0认证业务处理 服务
	 * @return
	 */
	public static Oauth2AuthorServer getInstance(){
		
		if(oauth2==null){
			oauth2=new Oauth2AuthorServer();
		}
		
		return oauth2;
	}
	
	
	/***
	 * Oauth2.0认证业务处理 服务私有构造器
	 */
	 private Oauth2AuthorServer(){
		 
		try {
		
			db = DBFactory.getDB();
			properties=new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream("host.properties"));
			
			hostUrl=properties.getProperty("host", "http://123.57.42.36/");
			OAUTH2_URL=properties.getProperty("oauth2_url", "123.57.42.36/");
			
			WeShopServer.getInstance();
			
		} catch (Exception e) {
			System.out.println("新建Oauth2AuthorServer出错！");
			e.printStackTrace();
		}
	}
	
	/**
	 * 身份认证
	 * @param request
	 * @param response
	 * @throws org.json.JSONException 
	 */
	public void oauthorAuthor(HttpServletRequest request,HttpServletResponse response)throws IOException,JDBCException,org.json.JSONException{
		
	 //JSONObject userInfo=(JSONObject)request.getSession().getAttribute(Constant.SESSION_USER_INFO_JSON);
		
		//菜单  找服务、商城、会员卡 menu=server,menu=weshop,menu=member
		
		String menuValue=request.getParameter("menu");
		
		
		String token=request.getParameter("token");//返回TOKEN
		
		
		request.getSession().setAttribute("token", token);
		
			//如果session没有保存用户信息，则先让用户进行认证，在重定向到指定的页面即可
			//认证
			MapList map=getPublicInfoByToken(token);
			
			Formatter f=new Formatter();
			
			if(!Checker.isEmpty(map)){
				String appid=map.getRow(0).get("app_id");
				
				String authroResultURL=hostUrl+"/weshop/"+Constant.WE_CAHT_OAUTH2_RESULT+".do?menu="+menuValue;
				
				f.format(OAuth2MenuStr,appid,authroResultURL,token);
				
				response.sendRedirect(f.toString());
			}
			
	}
	
	/**
	 * OAuthr2.0 认证结果处理
	 * @param request
	 * @param response
	 * @throws JDBCException 
	 * @throws org.json.JSONException 
	 * @throws IOException 
	 */
	public void oauthorResult(HttpServletRequest request,
			HttpServletResponse response) throws JDBCException, IOException, org.json.JSONException {
		
		String token=request.getParameter("state");
		String code=request.getParameter("code");
		
		MapList map=getPublicInfoByToken(token);
		
		JSONObject openidInfoAndAcctoken=Utils.getWebAccessToken(map.getRow(0).get("app_id"),map.getRow(0).get("app_secret"),code);
		
		//将用户信息保存到session中。
		request.getSession().setAttribute(Constant.SESSION_USER_INFO_JSON,openidInfoAndAcctoken);
		
		//menu 的值为MenuCode
		String menu=request.getParameter("menu");
		
		//拉取用户信息
		JSONObject userInfo=Utils.loadUserInFoOAuthor2(
				openidInfoAndAcctoken.getString("access_token"), 
				openidInfoAndAcctoken.getString("openid"));
		
		/**
		//注册用户
		if(userInfo!=null&&!userInfo.toString().contains("errcode")){
			wsServer.updateUserNickname(token,userInfo.getString("openid"),userInfo.getString("nickname"),false);
		}
		**/
		
		String getOrgidSQL="SELECT * FROM ws_member WHERE openid='"+openidInfoAndAcctoken.getString("openid")+"'";
		request.getSession().setAttribute(Constant.CURRENT_MEMBER_INFO,DatabaseAccess.query(getOrgidSQL));
		
		
		
		String menuValue=menu;
		
		String orgid=Utils.getOrgId(token);
		
		//访问相对路径URL
		String url="/am_mobile/index.html?menu="+menuValue
				+"&openid="+userInfo.getString("openid")
				+"&token="+token
				+"&orgid="+orgid
				+"&nickname="+userInfo.getString("nickname")
				+"&headimgurl="+userInfo.getString("headimgurl");
		
		response.sendRedirect(url);
	}
	
	
	private MapList getPublicInfoByToken(String token) throws JDBCException{
		
		String getAppidSQL="SELECT app_id,app_secret,account_belong FROM ws_public_accounts " +
				" WHERE app_id IS NOT NULL " +
				" AND is_valid=1 " +
				" AND token=?";
		
		MapList map = db.query(getAppidSQL,new String[]{token},new int[]{Type.VARCHAR});
		
		return map;
	}
	
	
}
