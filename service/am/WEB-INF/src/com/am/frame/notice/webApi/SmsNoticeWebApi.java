package com.am.frame.notice.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;
import com.p2p.tools.sms.impl.YunPianSendSmsMessage;

/**
 * @author YueBin
 * @create 2016年7月28日
 * @version 
 * 说明:<br />
 * 短信通知所有用户
 * 1,短息通知 发生内容为内容详情中的信息，短信内容为 abstract字段填写内容 .<br>
 * 
 */
public class SmsNoticeWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//内容ID
		String id=request.getParameter("content_id");
		
		//用户ID，如果发送所有用户，值为*
		String memberIds=request.getParameter("member_id");
		
		if("*".equals(memberIds)){
			memberIds="%%";
		}
		
		
		
		
		DBManager db=new DBManager();
		
		
			String smsContent=Var.get("APP_VER_org");
			
			//查询所短信的用户ID
			String queryMember="SELECT loginaccount FROM am_member WHERE phone IS NOT NULL AND id LIKE '"+memberIds+"'";
			
			MapList memberMap=db.query(queryMember);
			
			if(!Checker.isEmpty(memberMap)){
				
				YunPianSendSmsMessage ypSendSms=new YunPianSendSmsMessage();
				
				//云片网 模板ID
				long tpl_id=1493632;
				
				for(int i=0;i<memberMap.size();i++){
					
					String phone=memberMap.getRow(i).get("loginaccount");
					System.out.println(phone);
					ypSendSms.send(smsContent, phone, tpl_id);
				}
			}
		
		
		return null;
	}

}
