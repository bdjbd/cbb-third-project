package com.cdms.guiji;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

public class GuiJiFindCarGroup implements IWebApiService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String rValue = "";
		JSONArray ja=new JSONArray();
		String orgid=request.getParameter("orgid");
		rValue = getOrgList(orgid, ja);
		return rValue;
	}
	/**
	 * 获取当前以及下级机构列表
	 * @param current_orgid	当前机构ID
	 * @param ja	结果集
	 * @return
	 */
	public String getOrgList(String current_orgid,JSONArray ja){
		DBManager db=new DBManager();
		String orgname = "";
		String orgid = "";
		String sql="select orgname,orgid from aorg where orgid = '"+current_orgid+"'";
		
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			orgname = mapList.getRow(0).get("orgname");
			orgid = mapList.getRow(0).get("orgid");
		}
		JSONObject jo = new JSONObject();
		jo.put("ORGNAME", orgname);
		jo.put("ORGID", orgid);
		ja.put(jo);
		getOrg(db, "", orgid,ja);
		return ja.toString();
		
	}
	
	/**
	 * 递归查询下级机构
	 * @param db
	 * @param prefix	机构名称前拼的前缀
	 * @param parentid	父机构
	 * @param ja	结果集
	 */
	public void getOrg(DBManager db,String prefix,String parentid,JSONArray ja) {
		
		prefix+="&nbsp;&nbsp;";
		String sql = "select orgid,orgname,parentid from aorg where parentid='"+parentid+"'";
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			for (int i = 0; i < mapList.size(); i++) {
				String orgname = prefix+mapList.getRow(i).get("orgname");
				String orgid = mapList.getRow(i).get("orgid");
				JSONObject jo = new JSONObject();
				jo.put("ORGNAME", orgname);
				jo.put("ORGID", orgid);
				ja.put(jo);
				getOrg(db,prefix,orgid,ja);
			}
			
		}	
	}

}
