package com.am.cro.car;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 *@author 张少飞
 *@create 2017/7/20
 *@version
 *说明：修改当前用户默认车辆 将选择ID之外的其他车辆全部设为非默认状态
 */
public class updateOtherCarDefaultIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
	
		//已选择的默认车辆id
		String Id = request.getParameter("id");
		//当前登录会员ID
		String memberid = request.getParameter("memberid");
		
		String updateSql="UPDATE cro_CarManager SET isDefaultCar='0' WHERE id <>'"+Id+"' and memberid='"+memberid+"'";
		System.err.println("执行修改操作》》》"+updateSql);
		DB db=null;
		try {
			db = DBFactory.newDB();
			db.execute(updateSql);
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
