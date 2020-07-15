package com.cdms.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.utils.GetAddressByLatLng;
import com.am.utils.GpsCorrect;
import com.p2p.service.IWebApiService;

/**
 * 获取当前位置
 * @author guorenjie
 *
 */
public class GetLocation implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {

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
		GetAddressByLatLng gaAddress = new GetAddressByLatLng();
		String address = gaAddress.getAddress(lat1+"", lng1+"");
		
		return address;
	}

}
