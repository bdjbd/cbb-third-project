package com.am.cro.bespeak;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.p2p.service.IWebApiService;

/**
 * 获取预约时间
 * 
 * @author guorenjie
 *
 */
public class BespeakCarInfo implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DBManager db = new DBManager();

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) 
	{
		// 会员ID
		String memberid = request.getParameter("memberid");
		// 所属机构
		String orgcode = request.getParameter("orgcode");
		logger.info("memberid======="+memberid);
		
		// 查询当前机构会员的所属车辆
		String sql = "select * from cro_carmanager where orgcode='" + orgcode
				+ "' and memberid='" + memberid + "' and car_state='1'";
		MapList mapList = db.query(sql);
		
		return db.mapListToJSon(mapList).toString();
	}
}
