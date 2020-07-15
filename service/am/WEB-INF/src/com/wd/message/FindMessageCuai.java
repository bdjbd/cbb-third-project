package com.wd.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wd.ICuai;
import com.wd.message.server.MessageOpertion;

/**
 * 查询消息
 * */
public class FindMessageCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJs = new JSONArray();
		try {
			if (jsonArray != null && jsonArray.length() > 0) {
				JSONObject jo = jsonArray.getJSONObject(0);
				String receiveUserID = jo.getString("receiveUserID");
				MessageOpertion mo = new MessageOpertion();
				returnJs = mo.selectMessage(receiveUserID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnJs;
	}
}
