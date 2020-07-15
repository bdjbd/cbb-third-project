package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

public class UpdateFindPwd implements IWebApiService {

		@Override
		public String execute(HttpServletRequest request,
				HttpServletResponse response) 
		{
				String rCode="0";
				
		String rMsg="修改密码成功";
		
		//new_login_password，新密码，密码进行md5加密后更新密码；
		String NewLoginPassword=request.getParameter("new_login_password");
		//account，会员账号
		String MemberID=request.getParameter("login_account");
		
		String tSql="";
		DB db=null;
		
		try 
		{
			db = DBFactory.newDB();
			
			tSql="update am_member set loginpassword='" + NewLoginPassword + "' where loginaccount='" + MemberID + "'"; 
			int count=db.execute(tSql);
			if(count<=0)
			{
				rCode="2";
				rMsg="修改失败,请退出重新修改！";
			}
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
		
		//return：{CODE:”0”,MSG:”密码修改成功”}
		return "{\"CODE\":\"" + rCode + "\",\"MSG\":\"" + rMsg + "\"}";
	}
}
