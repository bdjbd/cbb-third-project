package com.cdms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class UpdateCarIcon implements IWebApiService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String cph = request.getParameter("cph");// tree上的车牌号
		String i = request.getParameter("i");// 更新第i个图标
		JSONArray array = null;
		DBManager db = new DBManager();
		// 所选车牌不为空
		if (!Checker.isEmpty(cph)) {
			String sql = "select "+i+" as i,"
					+ "(case when vehicle_state='1' then '../img/zx.png'  "
					+ "when vehicle_state='2' then '../img/weixiu.png' "
					+ "when vehicle_state='3' then '../img/zhongduanwx.png'  "
					+ "when vehicle_state='4' then '../img/broken.png' "
					+ "when vehicle_state='5' then '../img/tc.png' "
					+ "when vehicle_state='6' then '../img/ds.png' "
					+ "when vehicle_state='7' then '../img/bj.png' "
					+ "when vehicle_state='8' then '../img/lx.png' end ) as icon "
					+ "from cdms_vehiclebasicinformation where "
					+ "license_plate_number='" + cph +"'";
			array = db.queryToJSON(sql);
			logger.debug("array.toString()="+array.toString());
		}
		
		return array.toString();
	}
}
