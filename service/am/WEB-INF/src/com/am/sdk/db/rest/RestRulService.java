package com.am.sdk.db.rest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * servlet服务类
 * 2015-11-4 15:40:58
 * @author wz
 *
 */
public class RestRulService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String tag = "DispatcherServlet";
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 解决跨域问题
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		String action = getAction(request.getRequestURI());
		
		String encoding = System.getProperty("file.encoding");
		LoggerFactory.getLogger(getClass()).info(encoding);
		//windows系统下需要设置为 GB2312
		if(!"UTF-8".equals(encoding))
		{
			response.setHeader("Content-Type","text/html;charset=GB2312" );//Ajax 返回汉字
			request.setCharacterEncoding("utf-8");//接受中文乱码问题
		}else{
			//linux系统下设置为utf8
			response.setHeader("Content-Type","text/html;charset=UTF-8" );//Ajax 返回汉字
		}
	
		
		logger.info("网络访问，访问地址"+request.getRemoteAddr());
		
		PrintWriter out = new PrintWriter(response.getOutputStream());
		
		RestRulService rs = new RestRulService();
		try {
			//执行接口方法
			String result = rs.CallInterface(action, request, response);
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 截取uri工具类
	 * @param uri
	 * @return
	 */
	private String getAction(String uri) {
		String action = null;

		if (uri == null)
			return uri;

		if (!uri.contains(".do"))
			return uri;

		uri = uri.split("/")[uri.split("/").length - 1];
		action = uri.substring(0, uri.length() - 3);

		return action;
	}
	/**
	 * 调用接口方法
	 * @param action
	 * @param request
	 * @param response
	 * @return
	 */
	public String CallInterface(String action,HttpServletRequest request,
			HttpServletResponse response){
		
		IRestAction ias=classNameToObject(action);
		String results = "";
		if(ias!=null)
		{
			results=ias.execute(request, response);
		}
		else
		{
			results="接口调用失败:" + action + "不存在！";
			logger.info("接口调用失败:" + action + "不存在！;");
		}
		return results;
	}
	/**
	 * 依据类名反射出对象
	 * @param className
	 * @return
	 */
	private IRestAction classNameToObject(String className)
	{
		IRestAction result=null;

		try 
		{
			result=(IRestAction)Class.forName(className).newInstance();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
