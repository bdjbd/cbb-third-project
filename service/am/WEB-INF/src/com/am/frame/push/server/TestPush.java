package com.am.frame.push.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.p2p.service.IWebApiService;

public class TestPush implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		
		String tUserID=request.getParameter("UserID");
		Long tChannelID=Long.parseLong(request.getParameter("ChannelID"));
		String tTitle=request.getParameter("title") + ",测试内容";
		String tDescription=request.getParameter("description");
		//String tCustomContent=request.getParameter("customContent");
		
		JSONObject tJsonObj=new JSONObject();
		try 
		{
			//tJsonObj.put("key1", "测试值1");
			//tJsonObj.put("key2", "我是值2");
			tJsonObj.put("ID", "c0cb0a48-93ee-4a94-bf4a-7a4ebe37d47b");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PushNotification pn=new PushNotification(tUserID,tChannelID);
		Boolean rValue;
		
		if(tUserID!=null)
			rValue=pn.send(tTitle, tDescription, tJsonObj, 3);
		else
			rValue=pn.sendAll(tTitle, tDescription, tJsonObj, 3);
		
		// TODO Auto-generated method stub
		return rValue.toString();
	}

}
