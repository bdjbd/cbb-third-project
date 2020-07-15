package com.wd.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;

import com.wd.ICuai;

/**
 * 获得服务器时间，精确到年月日时分秒
 * */
public class GetServerTime implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		return new JSONArray().put(dateStr);
	}
}
