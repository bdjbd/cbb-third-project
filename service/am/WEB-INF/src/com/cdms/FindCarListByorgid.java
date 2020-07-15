package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class FindCarListByorgid implements IWebApiService {
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String orgid=request.getParameter("orgid");//当前登录机构
		String orgcode=request.getParameter("orgcode");//当前选择机构
		String wlid=request.getParameter("wlid");
		String cph=request.getParameter("cph");
		String sql="select cvb.id car_id,device_sn_number,cvb.license_plate_number,a.orgname from cdms_VehicleBasicInformation cvb,aorg a where a.orgid=cvb.orgcode ";
		if (!Checker.isEmpty(orgcode)) {
			sql+= " and orgid = '"+orgcode+"'";
		}else{
			sql+= " and orgid like '"+orgid+"%'";
		}
		if (wlid!=null) {
			sql+= " and cvb.id not in (select car_id from cdms_fencecars where fence_id='"+wlid+"')";
		}
		if (cph!=null) {
			sql+= " and cvb.license_plate_number like '%"+cph+"%'";
		}
		sql+=" order by orgid,license_plate_number";
		DBManager db=new DBManager();
		JSONArray ja=db.queryToJSON(sql);
		return ja.toString();
	}
}
