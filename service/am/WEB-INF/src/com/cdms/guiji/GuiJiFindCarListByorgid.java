package com.cdms.guiji;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

public class GuiJiFindCarListByorgid implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgid=request.getParameter("orgid");
		String sql="select cvb.id car_id,device_sn_number,cvb.license_plate_number,a.orgname from cdms_VehicleBasicInformation cvb,aorg a where a.orgid=cvb.orgcode ";
		if (orgid!=null) {
			sql+= " and orgid='"+orgid+"'";
		}
		DBManager db=new DBManager();
		JSONArray ja=db.queryToJSON(sql);
		return ja.toString();
	}

}
