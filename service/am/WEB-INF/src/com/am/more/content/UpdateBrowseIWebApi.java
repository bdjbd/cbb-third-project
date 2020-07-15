package com.am.more.content;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月16日
 *@version
 *说明：内容浏览修改次数
 */
public class UpdateBrowseIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
	
		//id
		String Id = request.getParameter("id");
		
		String updateBrowseSql="UPDATE am_Content SET count=( COALESCE(count,0)+1) WHERE id='"+Id+"'";
		DB db=null;
		try {
			db = DBFactory.newDB();
			db.execute(updateBrowseSql);
		} catch (JDBCException e) {
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
		return null;
	}

}
