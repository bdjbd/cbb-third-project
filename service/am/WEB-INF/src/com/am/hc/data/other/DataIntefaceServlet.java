package com.am.hc.data.other;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.p2p.service.IWebApiService;

/**
 * 接口Servlet类
 * @author guorenjie 
 * 2017-06-03
 */

public class DataIntefaceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory
			.getLogger(DataIntefaceServlet.class);

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 解决跨域问题
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST,OPTIONS,PUT,DELETE");
		
		
		String action = getAction(request.getRequestURI());
		
		String encoding = System.getProperty("file.encoding");
		
		LoggerFactory.getLogger(getClass()).info(encoding);

		if (!"UTF-8".equals(encoding)) {
			// windows系统下需要设置为 GB2312 Ajax 返回汉字
			response.setHeader("Content-Type", "text/html;charset=GB2312");
			request.setCharacterEncoding("utf-8");// 接受中文乱码问题
		} else {
			// linux系统下设置为utf8 Ajax 返回汉字
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
		}

		
		String result="";
		
		CheckOut co=new CheckOut();
		
		//检查白名单是否合法及校验码是否合法
		if (co.check(request)) 
		{
			result = CallInterface(action, request, response);

		} else {
			result = "{\"CODE\":\"40006\",\"MSG\":\"无效的参数\"}";

		}
		
		PrintWriter out = new PrintWriter(response.getOutputStream());
		
		try 
		{
			out.print(result);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 截取uri工具类
	 * 
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
	 * 
	 * @param action
	 * @param request
	 * @param response
	 * @return
	 */
	public String CallInterface(String action, HttpServletRequest request,
			HttpServletResponse response) {

		IWebApiService ias = classNameToObject(action);
		String results = "";
		if (ias != null) {
			results = ias.execute(request, response);
		} else {
			results = "接口调用失败:" + action + "不存在！";
			log.info("接口调用失败:" + action + "不存在！;");
		}
		return results;
	}

	/**
	 * 依据类名反射出对象
	 * 
	 * @param className
	 * @return
	 */
	private IWebApiService classNameToObject(String className) {
		IWebApiService result = null;

		try {
			result = (IWebApiService) Class.forName(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
