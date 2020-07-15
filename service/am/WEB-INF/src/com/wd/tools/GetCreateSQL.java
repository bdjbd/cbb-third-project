package com.wd.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.ICuai;
import com.wd.init.permission.FindPermissionByUserID;

/**
 * 获取当前登录人需要同步到终端的建表语句
 * @author 丁照祥
 * */
public class GetCreateSQL implements ICuai {

	private DB db=null;
	/**
	 * 获取当前登录人需要同步到终端的建表语句，版本号，当前系统时间
	 * 数据集合中，第一行是版本号和当前系统时间，从第二行开始是建表语句
	 * */
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		JSONArray rs=new JSONArray();
		String lastDownloadDatetime="";//最后一次同步数据时间
		if(jsonArray==null)
		{
			return rs;
		}
		try {
			//user=UserFactory.getUser(jsonArray.getString(0).toString());
			lastDownloadDatetime=jsonArray.getString(1).toString();
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
		FindPermissionByUserID findPer=new FindPermissionByUserID();
		//获取当前登录人模块权限集合
		JSONArray js=findPer.doAction(jsonArray);
		if(js!=null)
		{
			String mid="";
			try {
				for (int i = 0; i < js.length(); i++) {
					JSONObject jo = js.getJSONObject(i);
					JSONArray childMenuList = jo.getJSONArray("childMenuList");
					for (int j = 0; j < childMenuList.length(); j++) {
						JSONObject jon=childMenuList.getJSONObject(j);
						mid+="'"+jon.getString("ID")+"',";
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(!mid.equalsIgnoreCase("") && mid.endsWith(","))
			{
				mid=mid.substring(0,mid.length()-1);
			}
			
			String sql="select distinct tablename,selectsql,createsql,datatype,pkeyname,filecontrolname,signcontrolname " +
					" from abdp_offlinetable a left join abdp_offlinemoduel b on a.tid=b.tid " +
					" where b.mid in ("+mid+") or datatype=1 order by tablename";
			MapList list=null;
			try {
				if(mid!=null && !mid.trim().equalsIgnoreCase(""))
				{
					list=db.query(sql);
				}
			} catch (JDBCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!Checker.isEmpty(list) && list.size()>0)
			{
				String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				JSONArray m_js=new JSONArray();
				JSONObject jo=new JSONObject();
				try {
					jo.put("version",getVersion());//版本号
					jo.put("lastdownload",now);//当前系统时间
					m_js.put(jo);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rs.put(m_js);
				getData(rs,list,lastDownloadDatetime);//数据
			}
		}
		return rs;
	}
	
	/**
	 * 获取本次需要下载的建表语句
	 * @param list sql语句集合，用于查询将要下载的数据
	 * @param lastDownloadDatetime 最后一次下载数据的时间，格式：yyyy-mm-dd hh:mm:ss
	 * */
	private JSONArray getData(JSONArray js,MapList list,String lastDownloadDatetime)
	{
		if(!Checker.isEmpty(list))
		{
			for(int i=0;i<list.size();i++)
			{
				JSONArray row_data=new JSONArray();
				Row row=list.getRow(i);
				row_data.put(row.get("tablename"));//0表名
				row_data.put(row.get("createsql"));//1建表语句
				row_data.put(row.get("datatype"));//2数据类型
				//row_data.put(row.get("pkeyname"));//3主键字段名称
				js.put(row_data);
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
		String qryVersion="select max(version) as version from abdp_OfflineTable";
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

}
