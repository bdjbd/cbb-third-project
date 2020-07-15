package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class FindMemberIsExistence implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		//phone:手机号码，接口检查该号码是否是会员；
				String tPhone=request.getParameter("Phone");

				String rCode="0";
				
				String tSql="";
				String rMsg="";
				DB db=null;
				
				try 
				{
					db = DBFactory.newDB();
					
					tSql="select * from am_member where Phone='" + tPhone  + "'";
					MapList map=db.query(tSql);
					
					//检查手机是否存在
					if(Checker.isEmpty(map))
					{
						rCode="1";
						rMsg="未找到手机号码" + tPhone + "的用户！";
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
				
				return "{\"CODE\":\"" + rCode + "\",\"MSG\":\"" + rMsg + "\"}";
	}

}
