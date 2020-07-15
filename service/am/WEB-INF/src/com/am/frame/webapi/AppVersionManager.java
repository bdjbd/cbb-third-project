package com.am.frame.webapi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.Var;
import com.p2p.service.IWebApiService;

public class AppVersionManager implements IWebApiService
{
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		String result = "";
		
		// http://127.0.0.1:8080/AmRes/com.am.frame.webapi.AppVersionManager.do?AppName=org&AppThisVer=1.0.1
		//应用名称
		String AppName = request.getParameter("AppName");
		
		/*
		 应用版本号，版本号规则如下：
		 a.b.c
		 a，主版本号，程序功能大范围升级，取值范围 1-9
		 b，二级版本号，功能升级版本号，取值范围0-999
		 c,三级版本号,Bug修正版本号，取值范围0-999
		 例子： 1.0.3
		 */
		String AppThisVer = request.getParameter("AppThisVer");
		
		result=AppVersionManager.checkAppVerGetUrl(AppName, AppThisVer);
		
		return result;
	}
	
	//将版本号转化为数字
	private static long getVerNumber(String ver)
	{
		ver=ver.replace('.', ' ');
		String[] verList=ver.split(" ");
		String tStr="0";
		
		for(String stemp:verList)
		{  
			tStr += getStrNumber(stemp);
		} 
		
		return Long.parseLong(tStr);
	}

	//为字符串前面补0，凑齐版本号
	private static String getStrNumber(String str)
	{
		String rValue=str;
		
		if(str.length()==1)
			rValue="00" + str;
		else if(str.length()==2)
			rValue="0" + str;
		else if(str.length()==3)
			rValue="" + str;
		
		return rValue;
	}
	
	//检查版本号，获得下载url，如已经是最新则返回空
	public static String checkAppVerGetUrl(String appName,String appVer)
	{
		//应用名称
		String AppName = "APP_VER_" + appName;
		
		String AppAndroidPath = "APP_ANDROID_PATH_" + appName;
		
		String AppIosPath="APP_IOS_PATH_" + appName;
		
		String AppContent = "APP_CONTENT_"+ appName;
		
		String Appforce_update = "APP_force_update_"+appName;
		
		/*
		 应用版本号，版本号规则如下：
		 a.b.c
		 a，主版本号，程序功能大范围升级，取值范围 1-9
		 b，二级版本号，功能升级版本号，取值范围0-999
		 c,三级版本号,Bug修正版本号，取值范围0-999
		 例子： 1.0.3
		 */
		String AppThisVer = appVer;
		

		long ThisVerNumber=getVerNumber(AppThisVer);
		long NewVerNumber=getVerNumber(Var.get(AppName));

		//response.getWriter().write("ThisVerNumber=" + ThisVerNumber + "<br>");
		//response.getWriter().write("NewVerNumber=" + NewVerNumber + "<br>");
		
		String rValue="";
		String rIosUrl="";
		String rAndroidUrl="";
		String rContent = "";
		String force_update = "";
		if(ThisVerNumber<NewVerNumber)
		{
			rAndroidUrl=Var.get(AppAndroidPath);
			rIosUrl=Var.get(AppIosPath);
			
			rContent = Var.get(AppContent);
			
			force_update = Var.get(Appforce_update);
			
			String app_name = rAndroidUrl.substring(rAndroidUrl.lastIndexOf("/"),rAndroidUrl.length());
			
			rValue="{\"android_path\" : \"" + rAndroidUrl + "\" , \"ios_path\" : \"" + rIosUrl + "\", \"app_content\" : \"" + rContent + "\",\"app_nv\" : \"" + Var.get(AppName) + "\",\"app_tv\" : \"" + AppThisVer + "\",\"app_name\" : \""+app_name+"\",\"force_update\" : \""+force_update+"\"}";
		}
		
		
		return rValue;
	}


}
