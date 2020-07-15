package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONException;

import com.wd.ICuai;

/**
 * 支持跨库查询
 * @author dzx
 * */
public class ExecutionQueryForDbCuai implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray==null)
		{
			return null;
		}
		JSONArray js=new JSONArray();
		String sql = null;
		String db = null;
		try {
			sql = jsonArray.getString(0);
			db = jsonArray.getString(1);
			
			if(sql!=null && !sql.equalsIgnoreCase(""))
			{
				if(db!=null && !db.equalsIgnoreCase(""))
				{
					js=DatabaseAccess.query(sql,db);
				}
				else
				{
					js=DatabaseAccess.query(sql);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return js;
	}

}
