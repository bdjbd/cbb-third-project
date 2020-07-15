package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.sdk.sms.SendTemplateContent;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;
import com.p2p.tools.sms.RandomUtil;

public class SmsVcode implements IWebApiService {
	private Logger logger=LoggerFactory.getLogger(getClass());
//	@Override
//	public String execute(HttpServletRequest request,
//			HttpServletResponse response) 
//	{		
//		
//		//phone:手机号码，接口检查该号码是否是会员
//		String tPhone = request.getParameter("Phone");
//            
//		String sTemplateName = request.getParameter("TemplateName");
//
//		String tContent = Var.get(sTemplateName);
//
//		String tCode = "";
//		
//		
//		String rCode="0";
//		String rMsg="验证码已发送";
//		logger.info("ceshi"+tPhone);
//		logger.info("ceshi"+ tContent);
//		//构建对象，生成并发送密码
//		SMSIdentifyingCode sic = new SMSIdentifyingCode("");
//		tCode = sic.getCode(tContent, tPhone);
//
//		return "{\"CODE\":\"" + rCode + "\",\"MSG\":\"" + rMsg + "\",\"tCode\":\""+tCode+"\"}";
//	}
//	@Override
//	public String execute(HttpServletRequest request,
//			HttpServletResponse response) 
//	{		
//		
//		//phone:手机号码，接口检查该号码是否是会员
//		String tPhone = request.getParameter("Phone");
//            
//		String sTemplateName = request.getParameter("TemplateName");
//
//		String tContent = Var.get(sTemplateName);
//
//		String message ="";
//		//构建对象，生成并发送密码
//		SMSIdentifyingCode sic = new SMSIdentifyingCode("");
//		message = sic.getCode(tContent, tPhone);
//		
//		
//		
//		return message;
//	}
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response
			) {
		System.err.println("------------------------进入短信发送类SmsVcode----------------------");
		String rValue = "";
		String content = request.getParameter("TemplateName");			//模板变量
		System.err.println("输出变量模板TemplateName:" + content);
		String phone = request.getParameter("Phone");					//手机号
		System.err.println("输出手机号phone：" + phone);
		// 2、调用短信类，发送短信
		// {'code':'SMS_80840010',content:{'CODE':'[CODE]'}}
		SendTemplateContent stc = new SendTemplateContent(content);
		MapList mp = new MapList();
		// 获得6位的随机字符串，字符和数字
		String sNumber = RandomUtil.generateNumber(6);
		mp.addRow(0).put("code",sNumber);
		// 成功短信发送条数(手机号、map集合)
		int count = stc.send(phone, mp);
		System.err.println("输出count:" + count);
		if (count > 0) {
			rValue = "{\"CODE\":\"" + "0" + "\",\"MSG\":\"" + "验证码发送成功"
					+ "\",\"tCode\":\"" + sNumber + "\"}";
		} else {
			rValue = "{\"CODE\":\"" + "1" + "\",\"MSG\":\"" + "系统异常，请稍后再试"
					+ "\",\"tCode\":\"" + "" + "\"}";

		}

		return rValue;
	}

}
