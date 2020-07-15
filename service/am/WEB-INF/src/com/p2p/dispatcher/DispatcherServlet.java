package com.p2p.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.p2p.service.DataService;
//import com.sun.media.jfxmedia.logging.Logger;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * Author: Mike
 * 2014年7月15日
 * 说明：E快修servlet转发类
 *
 **/
public class DispatcherServlet extends HttpServlet 
{
	
	private static final long serialVersionUID = 1L;
	public static final String tag="DispatcherServlet";
	private static final String DATASERVER="P2P_DATASERVER";
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		//解决跨域问题
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");

		String action=getAction(request.getRequestURI());
		String encoding = System.getProperty("file.encoding");
		LoggerFactory.getLogger(getClass()).info(encoding);
		if(!"UTF-8".equals(encoding))
		{
			response.setHeader("Content-Type","text/html;charset=GB2312" );//Ajax 返回汉字
			request.setCharacterEncoding("utf-8");//接受中文乱码问题
		}
//        System.out.println("编码Encoding:" + encoding); 
		
		PrintWriter out=new PrintWriter(response.getOutputStream());
		DataService dataServer=(DataService)request.getSession().getAttribute(DATASERVER);
		
		if(dataServer==null)
		{
			dataServer=new DataService();
			request.getSession().setAttribute(DATASERVER, dataServer);
		}
		
		try 
		{
			Utils.Log(tag,"开始执行 | " + action + " | ");
			String result=dataServer.service(action, request, response);
			Utils.Log(tag,"执行 | " + action + " | 完成");
			
			out.print(result);
			out.flush();
			out.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String getAction(String uri)
	{
		String action=null;
		
		if(uri==null)
			return uri;
		
		if(!uri.contains(".do"))
			return uri;
		
		uri=uri.split("/")[uri.split("/").length-1];
		action=uri.substring(0, uri.length()-3);
		
		return action;
	}

}


