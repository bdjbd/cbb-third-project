package com.cdms.app;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

public class UpBackCar implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String car_id = request.getParameter("car_id");
		String sql1="update cdms_peoplecarshistory set status='2' ,end_time='"+new Date()+"' "
				+ "where id='"+id+"'";
		String sql = "update cdms_VehicleBasicInformation set member_id=null,"
				+ "member_datetime=null,car_binding_time=null where id='"+car_id+"'";
		DBManager db = new DBManager();
		db.execute(sql1);
		db.execute(sql);
		return null;
	}

}
