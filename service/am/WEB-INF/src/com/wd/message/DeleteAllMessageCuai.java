package com.wd.message;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;
import com.wd.message.server.MessageOpertion;

public class DeleteAllMessageCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJs = new JSONArray();
		try {
			if (jsonArray != null && jsonArray.length() > 0) {
				MessageOpertion mo = new MessageOpertion();
				String result = mo.deleteAllMessage(jsonArray.getString(0));
				if (result != null) {
					returnJs.put(result);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnJs;
	}
}
