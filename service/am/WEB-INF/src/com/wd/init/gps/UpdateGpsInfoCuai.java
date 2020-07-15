package com.wd.init.gps;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.database.DataBaseFactory;
import com.wd.tools.DatabaseAccess;

/**
 * 更新GPS信息
 * 
 * @author llm
 * @time 2012-5-12
 */
public class UpdateGpsInfoCuai implements ICuai {
	/**
	 * 更新GPS信息
	 * 
	 * @param gps信息
	 */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray retJsonArray = new JSONArray();
		try {
			String deviceId = jsonArray.getString(0);
			String latitude = jsonArray.getString(1);
			if (latitude == null || latitude.equals("null")
					|| latitude.length() == 0) {
				latitude = "";
			}
			String longitude = jsonArray.getString(2);
			if (longitude == null || longitude.equals("null")
					|| longitude.length() == 0) {
				longitude = "";
			}
			String userId = jsonArray.getString(3);
			String phoneNo = jsonArray.getString(4);
			if (phoneNo == null || phoneNo.equals("null")
					|| phoneNo.length() == 0) {
				phoneNo = "";
			}
			String sql = "update abdp_terminalmanage set longitude='"
					+ longitude + "', latitude='" + latitude + "', heartbeat="
					+ DataBaseFactory.getDataBase().getSysdateStr()
					+ " , phoneno='" + phoneNo + "' , staffid='" + userId
					+ "' where imei='" + deviceId + "'";
			retJsonArray = DatabaseAccess.execute(sql);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return retJsonArray;
	}

}
