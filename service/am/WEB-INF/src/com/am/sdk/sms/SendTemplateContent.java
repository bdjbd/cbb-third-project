package com.am.sdk.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;
import com.p2p.tools.sms.SMSIdentifyingCode;

public class SendTemplateContent 
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private String TemplateContent="";
	
	 //构造函数，templateCode模板存放的变量code
    //变量中需要替换的字段应写为：{字段名称大写}
    public SendTemplateContent(String templateCode)
    {
    	//取设置变量中的变量
    	Var var = new Var();
    	TemplateContent = Var.get(templateCode, "");
    	System.err.println("输出TemplateContent" + TemplateContent);
    	
    }
    
    //发送短信,phonelist:目标手机号码集合，用“;”号隔开；mp：用来替换模板内容的数据集合，只使用第一行数据进行替换；
    //返回发送短信成功的条数，如均未发送成功则返回0；
    public int send(String phonelist,MapList mp)
    {
    	System.err.println("----------进入SendTemplateContent类send方法----------");
    	int rValue=0;
    	String tFiledName="";
    	String tValue="";
    	String tContent=TemplateContent;
    	for(int i=0;i<mp.getRow(0).size();i++)
    	{
    		tFiledName=mp.getRow(0).getKey(i);
    		tValue=mp.getRow(0).get(i);
    		if(!Checker.isEmpty(tValue)){
    			tContent = tContent.replace("[" + tFiledName.toUpperCase() + "]", tValue);
    		}
    	}
    	System.err.println("输出短信tContent" + tContent);
    	
    	SMSIdentifyingCode sic = new SMSIdentifyingCode("");
    	
    	String [] tPhoneArray=phonelist.split(";");
    	String tPhone="";
    	for(int i=0;i<tPhoneArray.length;i++)
    	{
    		tPhone=tPhoneArray[i];
    		sic.getCode(tContent, tPhone);
    		
    		rValue++;
    	}
    	
    	return rValue;
    }

}
