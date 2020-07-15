package com.fastunit.sendmessage;

import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.User;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.FlowMessenger;

public class SendMessageTogoCom implements FlowMessenger {

	
	@Override
	public void send(Map<String, String> arg0, User arg1) {
		// TODO Auto-generated method stub
		String Sender=arg0.get("caller");//发起人
		String Receiver=arg1.getId();//接收人
		String getUserSql="select username from auser where userid='"+Sender+"'";
		String username="";
		DB db;
		MapList list=null;
		
		try {
			db = DBFactory.getDB();
			list= db.query(getUserSql);
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(list!=null && list.size()>0)
		{
			Row row=list.getRow(0);
			username=row.get("username");
		}
		String BContent="由【"+username+"】发起的【"+arg0.get("flowName")+"】已经运行到【"+arg0.get("taskName")+"】环节，需要您及时进行处理！";//内容
		String Title="流程待办通知";//标题
		String Expand="<iq MsgFrom='综合业务系统'  MsgType='"+arg0.get("flowName")+"待办通知'> </iq>";//扩展
		SendOAMessage(Sender,Receiver,BContent,Title,Expand);
	}
	
	/**
	 * Sender String 发起人
	 * Receiver String 接收人 
	 * BContent String 内容
	 * Title 标题
	 * Expand String 扩展参数,预留 
	 * */
	private String SendOAMessage (String Sender,String Receiver,String BContent,String Title,String Expand)
	{
		String resultStr="";		
		String methodName="SendOAMessage"; 
		String[] ArrValues=new String[5];
		ArrValues[0]=Sender;//用户名
		ArrValues[1]=Receiver;//
		ArrValues[2]=BContent;
		ArrValues[3]=Title;
		ArrValues[4]=Expand;
		SoapObject so=WebService.ConnectWebService(methodName, ArrValues);
		if (so != null) {
			resultStr=so.getProperty("SendOAMessageReturn").toString();
		}	  
		return resultStr;
	}

}
