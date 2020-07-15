package com.wd.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.User;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.user.UserFactory;
import com.fastunit.util.Checker;
import com.wd.ICuai;

/**
 * 获取当前登录人需要缓存的数据
 * @author 丁照祥
 * */
public class GetUserCacheData implements ICuai {

	private DB db=null;
	private User user=null;
	/**
	 * 获取当前登录人需要同步到终端的缓存数据
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		JSONArray rs=new JSONArray();
		if(jsonArray==null)
		{
			return rs;
		}
		try {
			user=UserFactory.getUser(jsonArray.getString(0).toString());
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			db=DBFactory.getDB();
		} catch (JDBCException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
			String sql="select tablename,selectsql,createsql,delsql from abdp_offlinetable where datatype=1 order by tid desc";
			MapList list=null;
			try {
					list=db.query(sql);
			} catch (JDBCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!Checker.isEmpty(list) && list.size()>0)
			{
				rs=getData(list);
				rs.put(getVersion());
			}
			System.out.println(rs.toString());
		return rs;
	}
	
	private JSONArray getData(MapList list)
	{
		JSONArray js=new JSONArray();
		if(!Checker.isEmpty(list))
		{
			for(int i=0;i<list.size();i++)
			{
				JSONArray row_data=new JSONArray();
				Row row=list.getRow(i);
				row_data.put(row.get("tablename"));
				row_data.put(row.get("createsql"));
				String sql=row.get("selectsql");
				if(!sql.equalsIgnoreCase("-1"))
				{
					sql=ReplaceStr(sql);
					JSONArray data_js=DatabaseAccess.query(sql);
					row_data.put(data_js);
					js.put(row_data);
				}
				else
				{
					JSONArray data_js=getAllroles();
					row_data.put(data_js);
					js.put(row_data);
				}
				String delSql=row.get("delsql");
				delSql=ReplaceStr(delSql);//替换参数
				row_data.put(delSql);
			}
		}
		return js;
	}
	
	/**
	 * 获取需要更新的版本号
	 * */
	private String getVersion()
	{
		String version="1";
		String qryVersion="select max(version) as version from abdp_OfflineTable where datatype=1";
		MapList list=null;
		try {
			list=db.query(qryVersion);
			if(!Checker.isEmpty(list) && list.size()>0)
			{
				Row row=list.getRow(0);
				version=row.get(0,"1");
			}
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	//替换全局变量的值
	private String ReplaceStr(String sql)
	{
		GlobalParamList paramList=new GlobalParamList(user.getId());
		if (sql.indexOf("$G") == -1) {
			return sql;
		}
		// 替换全局变量
		for (int i = 0; i < paramList.length(); i++) {
			String str = "$G{" +paramList.getKey(i) + "}";
			sql = sql.replace(str, paramList.getValue(i));
		}
		return sql;
	}
	
	private JSONArray getAllroles()
	{
		JSONArray js=new JSONArray();
		String userid=user.getId();
		String []roles=user.getAllRoles();
		for (String roleID : roles) 
		{
			JSONObject jo = new JSONObject();
			try {
				jo.put("userid".toUpperCase(),userid);
				jo.put("roleid".toUpperCase(),roleID);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			js.put(jo);
		}
		return js;
	}

}
