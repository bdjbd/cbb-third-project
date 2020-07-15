package com.cdms.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

public class PepoleCarRelationshipAppAction implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String useraccount = request.getParameter("useraccount");
		String sql = "select cvb.id "
				+ " from cdms_VehicleBasicInformation cvb,am_member am"
				+ " where am.id=cvb.member_id and am.loginaccount='" + useraccount + "'";
		DBManager db = new DBManager();
		boolean b=db.queryToJSON(sql).length()!=0;
		if(b){
			return b+"";
		}
		String sql1="select cp.id  from cdms_pepolecarrelationship cp,am_member am where am.id=cp.member_id and am.loginaccount='" + useraccount + "'";
		b=db.queryToJSON(sql1).length()==0;
		return b+"";
	}

}
