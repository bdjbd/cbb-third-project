package com.wd.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import com.wd.ICuai;

/**
 * 得到服务器日期
 * */
public class GetServerDate implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		return new JSONArray().put(dateStr);
	}
}