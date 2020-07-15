package com.cdms.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class UpjieDan implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String userid = request.getParameter("userid");
		DBManager db = new DBManager();
		JSONObject rJsonObject = new JSONObject();
		String query = "select id from cdms_case where id='"+id+"' and (member_id is null or member_id='')";
		String sql1 = "update cdms_Case set "
				+ " member_id='"+userid+"',"
				+ " case_state='3',"
				+ " case_order_time='now()' where "
				+ " id='"+id+"'";
		String sql3="update am_member set work_state='2' where  id='"+userid+"'";
		String sql2="delete from  cdms_CaseOrderPersonnel  where "
				+ "member_id='"+userid+"' "
				+ "and case_id='"+id+"'";
		MapList mapList = db.query(query);
		if(!Checker.isEmpty(mapList)||mapList.size()>0){
			db.execute(sql1);
			db.execute(sql2);
			db.execute(sql3);
			rJsonObject.put("CODE", "0");
			rJsonObject.put("msg", "接单成功");
		}else {
			rJsonObject.put("CODE", "1");
			rJsonObject.put("msg", "接单失败，已有人接单");
		}
		logger.debug("接单结果==="+rJsonObject.toString());
		return rJsonObject.toString();
	}

}
