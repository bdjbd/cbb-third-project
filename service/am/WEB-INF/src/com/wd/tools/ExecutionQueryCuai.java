package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wd.ICuai;

public class ExecutionQueryCuai implements ICuai {

	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray==null)
		{
			return null;
		}
		JSONArray js=new JSONArray();
		try {
			for(int i=0;i<jsonArray.length();i++)
			{
				JSONObject job=new JSONObject();
				JSONObject jo=jsonArray.getJSONObject(i);
				String sql=jo.getString("sql");
				String db=jo.getString("db");
				if(sql!=null && !sql.equalsIgnoreCase(""))
				{
					if(db!=null && !db.equalsIgnoreCase(""))
					{
						job.put("d",DatabaseAccess.query(sql,db));
					}
					else
					{
						job.put("d",DatabaseAccess.query(sql));
					}
					job.put("name",jo.getString("name"));
					js.put(job);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return js;
	}

}
