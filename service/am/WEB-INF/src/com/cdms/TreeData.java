package com.cdms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.util.Checker;

public class TreeData{
	Logger logger = LoggerFactory.getLogger(getClass());

	public String getTreeData(String orgid) {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		logger.debug("开始执行时间=" + sFormat.format(new Date()));
		String rValue = "";
		DBManager db = new DBManager();
		JSONArray jArray = new JSONArray();
		String sql ="select orgid,orgname as name"
				+" from aorg where orgid='"+orgid+"'";
		logger.info("当前登录机构="+sql);
		MapList mapList = db.query(sql);
		if(!Checker.isEmpty(mapList)){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ORGID", mapList.getRow(0).get("orgid"));
			jsonObject.put("name", mapList.getRow(0).get("name"));
			jsonObject.put("open", "true");
			jsonObject.put("iconOpen", "../img/cz.png");
			jsonObject.put("iconClose", "../img/czh.png");
			String carSql = "select id,license_plate_number as name,(case when vehicle_state='1' then '../img/zx.png'  when vehicle_state='2' then '../img/weixiu.png' when vehicle_state='3' then '../img/zhongduanwx.png'  when vehicle_state='4' then '../img/broken.png' when vehicle_state='5' then '../img/tc.png' when vehicle_state='6' then '../img/ds.png' when vehicle_state='7' then '../img/bj.png' when vehicle_state='8' then '../img/lx.png' end ) as icon from "
					+ "cdms_vehiclebasicinformation where orgcode='"+orgid+"' order by license_plate_number";
			logger.info("当前登录机构车辆="+carSql);
			//子节点为当前机构所属车辆
			JSONArray childrenArray = db.queryToJSON(carSql);
			childrenArray = new JSONArray(childrenArray.toString().replaceAll("NAME", "name").replaceAll("ICON", "icon"));
			//子节点中添加下级机构
			getCarAndOrg(db, orgid, childrenArray);
			//将子节点放入当前机构中
			jsonObject.put("children", childrenArray);
			jArray.put(jsonObject);
		}
		rValue = jArray.toString();
		logger.debug("rValue=" + rValue);
		logger.debug("执行完成时间=" + sFormat.format(new Date()));
		return rValue;
	}
	/**
	 * 获取当前机构的车辆以及下级机构
	 * @param db
	 * @param orgid
	 * @param carArray
	 */
	public void getCarAndOrg(DBManager db,String orgid,JSONArray childrenArray){
		String sql = "select orgid,orgname as name from aorg where parentid='"+orgid+"'";
		logger.info("递归查询下级机构="+sql);
		MapList mapList = db.query(sql);
		//如果下级机构不为空
		if(!Checker.isEmpty(mapList)){
			for (int i = 0; i < mapList.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ORGID", mapList.getRow(i).get("orgid"));
				jsonObject.put("name", mapList.getRow(i).get("name"));
				jsonObject.put("open", "true");
				jsonObject.put("iconOpen", "../img/cz.png");
				jsonObject.put("iconClose", "../img/czh.png");
				String carSql = "select id,license_plate_number as name,(case when vehicle_state='1' then '../img/zx.png'  when vehicle_state='2' then '../img/weixiu.png' when vehicle_state='3' then '../img/zhongduanwx.png'  when vehicle_state='4' then '../img/broken.png' when vehicle_state='5' then '../img/tc.png' when vehicle_state='6' then '../img/ds.png' when vehicle_state='7' then '../img/bj.png' when vehicle_state='8' then '../img/lx.png' end ) as icon from "
						+ "cdms_vehiclebasicinformation where orgcode='"+mapList.getRow(i).get("orgid")+"' order by license_plate_number";
				//子节点为当前机构所属车辆
				logger.info("递归查询下级机构的车辆="+carSql);

				JSONArray childrenArray2 = db.queryToJSON(carSql);
				childrenArray2 = new JSONArray(childrenArray2.toString().replaceAll("NAME", "name").replaceAll("ICON", "icon"));
				//子节点中添加下级机构
				getCarAndOrg(db, mapList.getRow(i).get("orgid"), childrenArray2);
				//将子节点放入当前机构中
				jsonObject.put("children", childrenArray2);
				childrenArray.put(jsonObject);
			}
		}
	}
}
