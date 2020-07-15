
<%!
//将版本号转化为数字
public long getVerNumber(String ver)
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
public String getStrNumber(String str)
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
%>

<%@ page import="java.util.HashMap" %>



<%
	//下载URL
	String rootUrl="http://182.92.99.124";

	//用户端 版本号、下载地址定义
	String[] exk={"1.0.3","/apk/ekx.apk"};
	//维修端版本号、下载地址定义
	String[] exk_wxd={"1.0.3","/apk/ekx.apk"};
	
	//应用程序名称
	HashMap<String, String[]> map = new HashMap<String, String[]>();
	map.put("ekx", exk);
	map.put("ekx_wxd", exk_wxd);

	//应用名称
	String AppName = request.getParameter("AppName").toString();
	
	/*
	 应用版本号，版本号规则如下：
	 a.b.c
	 a，主版本号，程序功能大范围升级，取值范围 1-9
	 b，二级版本号，功能升级版本号，取值范围0-999
	 c,三级版本号,Bug修正版本号，取值范围0-999
	 例子： 1.0.3
	 */
	String AppThisVer = request.getParameter("AppThisVer").toString();
	
	//response.getWriter().write("AppThisVer=" + AppThisVer + "<br>");
	//response.getWriter().write("AppName=" + AppName + "<br>");
	
	
	long ThisVerNumber=getVerNumber(AppThisVer);
	long NewVerNumber=getVerNumber(map.get(AppName)[0]);

	//response.getWriter().write("ThisVerNumber=" + ThisVerNumber + "<br>");
	//response.getWriter().write("NewVerNumber=" + NewVerNumber + "<br>");
	
	if(ThisVerNumber<NewVerNumber)
	{
		response.getWriter().write(rootUrl + map.get(AppName)[1]);
	}
%>