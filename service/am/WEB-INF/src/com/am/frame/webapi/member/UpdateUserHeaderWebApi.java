package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月2日
 * @version 
 * 说明:<br />
 * 更新用户头像
 */
public class UpdateUserHeaderWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String memberId=request.getParameter("memberId");
		String weixheader=request.getParameter("wxheadimg");
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			if(!Checker.isEmpty(weixheader)){
				weixheader=weixheader.replaceAll("\\\\", "/");
			}
			
			db.execute("UPDATE am_member SET wxheadimg=? WHERE ID=?", 
					new String[]{weixheader,memberId},
					new int[]{Type.VARCHAR,Type.VARCHAR});
			
		}catch(Exception e){
			
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return "";
	}

}
