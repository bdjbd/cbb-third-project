package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONException;

import com.fastunit.Var;
import com.wd.ICuai;

/**
 * 通过变量名称获得系统变量
 * */
public class GetSystemVarCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		JSONArray returnJSONArray = new JSONArray();
		try {
			returnJSONArray.put(Var.get(jsonArray.getString(0), ""));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return returnJSONArray;
	}
}
