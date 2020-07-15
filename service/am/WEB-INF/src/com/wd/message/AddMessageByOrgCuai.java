package com.wd.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wd.ICuai;
import com.wd.message.server.MessageOpertion;

/**
 * 新增消息，并发送通知
 * */
public class AddMessageByOrgCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJs = new JSONArray();
		try {
			if (jsonArray != null && jsonArray.length() > 0) {
				JSONObject jo = jsonArray.getJSONObject(0);
				String content = jo.getString("content");
				String senderuser = jo.getString("senderuser");
				String orgid = jo.getString("orgid");
				String msgType = jo.getString("messagrtype");
				String pcUrl = jo.getString("pcurl");
				String androidUrl = jo.getString("androidurl");
				MessageOpertion mo = new MessageOpertion();
				String result = mo.addMessageByOrg(content, senderuser, orgid, msgType, pcUrl, androidUrl);
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
