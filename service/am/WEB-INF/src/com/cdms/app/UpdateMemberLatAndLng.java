package com.cdms.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.webapi.db.DBManager;
import com.am.utils.GpsCorrect;
import com.p2p.service.IWebApiService;

/**
 * 获取驾驶员经纬度
 * @author 02
 *
 */
public class UpdateMemberLatAndLng implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {

		// 会员ID
		String memberid = request.getParameter("memberid");
		// 经度
		String lng = request.getParameter("lng");
		// 纬度
		String lat = request.getParameter("lat");

		double lat1 = Double.parseDouble(lat);
		double lng1 = Double.parseDouble(lng);

		double []corr = new double[2];
		corr[0] = lat1;
		corr[1] = lng1;

		GpsCorrect.transform(lat1, lng1, corr);

		lat1 = corr[0];
		lng1 = corr[1];
		DBManager db = new DBManager();
		String sql = "update am_member set lng='"+lng1+""+"',lat='"+lat1+""+"' where id='"+memberid+"'";
		db.execute(sql);
		
		return "{\"CODE\":\"0\",\"MSG\":\"更新id=" + memberid + "的用户坐标为lng=" + lng1 + " | lat=" + lat1 + "\"}";
	}

}
