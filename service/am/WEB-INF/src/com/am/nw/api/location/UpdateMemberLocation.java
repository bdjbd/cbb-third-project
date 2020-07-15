package com.am.nw.api.location;

import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.am.nw.api.location.util.LocationApiUtil;
import com.fastunit.util.Checker;

public class UpdateMemberLocation {
	
	/**
	 * 如果车辆绑定了人员，更新人员位置
	 * @param db
	 * @param terminalId	终端ID
	 * @param location	
	 * @return
	 */
	public static boolean updateMemberLnglat(String driver,String carId,LocationApiUtil util,DBManager db,
			JSONObject location,JSONObject status) {
		boolean rValue = false;
		
		if(util.isNotLocate(status)){//数据是否有效
			if(!Checker.isEmpty(driver)){
				// 如果实时位置数据包不为空
				if (location.length() > 0) {
					double lng=location.getDouble("longitude");// 经度
					double lat=location.getDouble("latitude"); // 纬度
					String updatelnglatsql = "update am_member set lng='"+lng+""+"',lat='"+lat+""+"' where id='"+driver+"'";
					int count = db.execute(updatelnglatsql);
					if(count>0){
						rValue=true;
					}
				}
			}
			
		}
		
		return rValue;
	}
}
