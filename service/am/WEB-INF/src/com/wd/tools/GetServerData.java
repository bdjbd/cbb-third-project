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
import com.wd.init.permission.FindPermissionByUserID;

/**
 * 获取当前登录人需要同步到终端的数据
 * @author 丁照祥
 * */
public class GetServerData implements ICuai {

	private DB db=null;
	private User user=null;
	/**
	 * 获取当前登录人需要同步到终端的数据
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
			user=UserFactory.getUser(jsonArray.getString(0).toString());
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
				rs=getData(list,lastDownloadDatetime);//数据
			}
		}
		return rs;
	}
	
	/**
	 * 获取本次需要下载的数据集合
	 * @param list sql语句集合，用于查询将要下载的数据
	 * @param lastDownloadDatetime 最后一次下载数据的时间，格式：yyyy-mm-dd hh:mm:ss
	 * */
	private JSONArray getData(MapList list,String lastDownloadDatetime)
	{
		JSONArray data=new JSONArray();
		if(!Checker.isEmpty(list))
		{
			for(int i=0;i<list.size();i++)
			{
				Row row=list.getRow(i);
				String tablename=row.get("tablename");//表名
				String datatype=row.get("datatype");//数据类型
				String pkeyname=row.get("pkeyname");//主键字段名称
				String fileControlName=row.get("filecontrolname","-1");//文件元素编号
				if(fileControlName==null || fileControlName.equalsIgnoreCase(""))
					fileControlName="-1";
				String signcontrolname=row.get("signcontrolname","-1");//签名元素编号
				if(signcontrolname==null || signcontrolname.equalsIgnoreCase(""))
					signcontrolname="-1";
				String sql=row.get("selectsql");
				if(datatype.trim().equalsIgnoreCase("2"))
				{
					sql=Replace$T(sql,lastDownloadDatetime);
				}
				sql=ReplaceStr(sql);
				if(datatype!=null && !datatype.trim().equalsIgnoreCase("1"))
				{
					DatabaseAccess.queryForOffline(data,tablename,pkeyname,datatype,fileControlName,signcontrolname,sql);
				}
			}
		}
		return data;
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
	
	/**
	 * 替换$T表达式
	 * @param str 需要替换的内容
	 * @param lastDownloadDatetime 最后一次下载数据的时间
	 * */
	private String Replace$T(String str,String lastDownloadDatetime)
	{
		int startIndex=str.indexOf("$T{");
		int endIndex=str.indexOf("}",startIndex);
		if(startIndex>-1 && endIndex>-1)
		{
			String t=str.substring(startIndex, endIndex+1);
			String t1=str.substring(startIndex+3, endIndex);
			if(lastDownloadDatetime!=null && !lastDownloadDatetime.equalsIgnoreCase(""))
			{
				str=str.replace(t," "+t1+">='"+lastDownloadDatetime+"' ");
			}
			else
			{
				str=str.replace(t," 1=1 ");
			}
		}
		return str;
	}
}
