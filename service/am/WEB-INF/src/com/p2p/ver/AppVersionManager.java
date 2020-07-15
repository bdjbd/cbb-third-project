package com.p2p.ver;

import com.fastunit.Var;

public class AppVersionManager 
{
	
	
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
		
		String AppPath = "APP_PATH_" + appName;
		
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
		if(ThisVerNumber<NewVerNumber)
		{
			rValue=Var.get(AppPath);
		}
		
		return rValue;
	}
}
