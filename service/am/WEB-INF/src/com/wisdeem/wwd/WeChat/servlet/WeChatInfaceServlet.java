package com.wisdeem.wwd.WeChat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wisdeem.wwd.Constant;
import com.wisdeem.wwd.WeChat.server.Oauth2AuthorServer;
import com.wisdeem.wwd.WeChat.server.WeChatInfaceServer;
import com.wisdeem.wwd.WeChat.server.WeShopServer;

/**
 *   说明:
 * 		微信接口Servlet
 *   @creator	岳斌
 *   @create 	Nov 15, 2013 
 *   @version	$Id
 */
public class WeChatInfaceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String tag="WeChatInfaceServlet";
	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String action=getAction(request.getRequestURI());
		
		WeShopServer shopServer=WeShopServer.getInstance();
		Oauth2AuthorServer author=Oauth2AuthorServer.getInstance();
		
		if(Constant.WE_CAHT_INFACE_URL.equals(action)){//微信公众平台开发接口推送Action
			logger.info("微信接入");
			
			WeChatInfaceServer server=WeChatInfaceServer.getInstance();
			server.weChatServer(request,response,action);
			
			logger.info("接入完成");
			return;
		}else if(Constant.WE_CAHT_AUTHOR_OAUTH2.equalsIgnoreCase(action)){//认证公众帐号Oauth2.0
			
			try {author.oauthorAuthor(request, response);} catch (Exception e) {e.printStackTrace();}
		
		}else if(Constant.WE_CAHT_OAUTH2_RESULT.equalsIgnoreCase(action)){//OAuth2.0认证结果处理
			
			try {author.oauthorResult(request, response);} catch (Exception e) {e.printStackTrace();}
		
		}else {
			
			response.setHeader("Content-Type","text/html;charset=GB2312" );//Ajax 返回汉字
			request.setCharacterEncoding("utf-8");//接受中文乱码问题
			shopServer.shopServer(request,response,action);
		} 
	}
	
	private String getAction(String uri){
		
		String action=null;
		
		if(uri==null)return uri;
		
		if(!uri.contains(".do"))return uri;
		
		if(uri!=null&&uri.length()>Constant.WE_SHOP_DOMAIN_NAME.length()+2){
			action=uri.substring(Constant.WE_SHOP_DOMAIN_NAME.length()+2,uri.indexOf(".do"));
		}
		
		return action;
	}
}
