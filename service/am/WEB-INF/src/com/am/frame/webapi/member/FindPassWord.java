package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;
import com.p2p.tools.sms.SMSIdentifyingCode;

public class FindPassWord implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		//phone:手机号码，接口检查该号码是否是会员；
		String tPhone=request.getParameter("Phone");
		String sTemplateName = request.getParameter("TemplateName");

		String tContent = Var.get(sTemplateName);

		
		
		String rCode="0";
		String rMsg="密码重置成功，密码已发送至号码为" + tPhone + "的手机。";
		
		String tSql="";
		DB db=null;
		
		try 
		{
			db = DBFactory.newDB();
			
			tSql="select * from am_member where Phone='" + tPhone  + "'";
			MapList map=db.query(tSql);
			
			//检查手机是否存在
			if(!Checker.isEmpty(map))
			{
				String tID=map.getRow(0).get("ID");
				
				//构建对象，生成并发送密码
				SMSIdentifyingCode sic = new SMSIdentifyingCode("");
				String tCode = sic.getCode(tContent, tPhone);
				
				//短信发送成功
				if(sic.srm.getValue("result").equalsIgnoreCase("0"))
				{
					tSql="update am_member set loginpassword='" + com.p2p.Utils.getMD5Str(tCode) + "' wehre id='" + tID + "'"; 
					int count=db.execute(tSql);
					if(count<=0)
					{
						rCode="3";
						rMsg="该用户不存在，找回密码失败！";
					}
				}
				else
				{
					rCode=sic.srm.getValue("result");
					rMsg=sic.srm.getValue("description");
				}
			}
			else
			{
				rCode="1";
				rMsg="未找到手机号码" + tPhone + "的用户！";
			}
			
			//db.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		//return: {CODE:”0”,MSG:”密码重置成功，重置后的密码发送至xxx号码手机。”}
		//CODE=0,重置成功；
		//CODE=1，未找到手机号码xxx的用户；
		//CODE=2，手机号码无效/同一号码信息发送过多/…来自短信平台的错误信息
		return "{\"CODE\":\"" + rCode + "\",\"MSG\":\"" + rMsg + "\"}";
	}

}
