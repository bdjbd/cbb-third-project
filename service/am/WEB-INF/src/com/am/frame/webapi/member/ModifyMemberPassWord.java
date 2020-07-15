package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class ModifyMemberPassWord implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		String rCode="0";
		String rMsg="修改密码成功";
		
		//old_login_password，旧密码，检查用户旧密码是否正确；
		String OldLoginPassword=request.getParameter("old_login_password");
		//new_login_password，新密码，密码进行md5加密后更新密码；
		String NewLoginPassword=request.getParameter("new_login_password");
		//密码安全等级
		String pwd_security_grade=request.getParameter("pwd_security_grade");
		//member_id，会员ID
		String MemberID=request.getParameter("member_id");
		
		String tSql="";
		DB db=null;
		
		try 
		{
			db = DBFactory.getDB();
			
			tSql="select * from am_member where id='" + MemberID + "' and loginpassword='" + OldLoginPassword + "'";
			MapList map=db.query(tSql);
			
			//旧密码正确
			if(!Checker.isEmpty(map))
			{
				tSql="update am_member set loginpassword='" + NewLoginPassword + "',pwd_security_grade = '"+pwd_security_grade+"' where id='" + MemberID + "'"; 
				int count=db.execute(tSql);
				if(count<=0)
				{
					rCode="2";
					rMsg="该用户不存在，修改密码失败！";
				}
			}
			else
			{
				rCode="1";
				rMsg="旧密码输入错误！";
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
		
		//return：{CODE:”0”,MSG:”密码修改成功”}
		return "{\"CODE\":\"" + rCode + "\",\"MSG\":\"" + rMsg + "\"}";
	}

}
