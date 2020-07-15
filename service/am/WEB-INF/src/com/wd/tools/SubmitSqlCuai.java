package com.wd.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Transaction;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wd.ICuai;
import com.wd.database.DataBaseFactory;

/**
 * 执行离线模块上传的sql语句
 * 
 * @author dzx
 * @time 2012-10-10
 */
public class SubmitSqlCuai implements ICuai {
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray==null || jsonArray.length()<=0)
		{
			return null;
		}
		JSONArray returnJSONArray = new JSONArray();
		DB db=null;
		Transaction tran=null;
		try 
		{
			String userId = jsonArray.getString(1).toString();
			db=DBFactory.getDB();
			tran= db.beginTransaction();
			JSONArray sqls=jsonArray.getJSONArray(0);
			String ids=jsonArray.getString(2);
			String qrySql="select id from abdp_serveroperation where id in ("+ids+")";
			MapList list=db.query(qrySql);
			String currentSql="";
			String insertSql="";
			if(!Checker.isEmpty(list))
			{
				for(int i=0;i<sqls.length();i++)
				{
					JSONObject jo=sqls.getJSONObject(i);
					String id=jo.getString("id");
					int index=list.findRowIndex("id",id);
					if(index==-1)
					{
						//如果不存在，则执行
						currentSql=jo.getString("operationSql");
						db.execute(currentSql);
						CommonUtil.addBusinesslog(currentSql, userId);
						insertSql="insert into abdp_serveroperation(id,createtime)" +
							" values ('"+id+"',"+DataBaseFactory.getDataBase().getSysdateStr()+")";
						db.execute(insertSql);
					}
				}
			}
			else
			{
				//全部执行
				for(int i=0;i<sqls.length();i++)
				{
					JSONObject jo=sqls.getJSONObject(i);
					String id=jo.getString("id");
					//如果不存在，则执行
					currentSql=jo.getString("operationSql");
					db.execute(currentSql);
					CommonUtil.addBusinesslog(currentSql, userId);
					insertSql="insert into abdp_serveroperation(id,createtime)" +
						" values ('"+id+"',"+DataBaseFactory.getDataBase().getSysdateStr()+")";
					db.execute(insertSql);
				}
			}
			returnJSONArray.put(1);
			tran.commit();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			returnJSONArray.put(-1);
			try {
				tran.rollback();
			} catch (JDBCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			e1.printStackTrace();
		}
		return returnJSONArray;
	}
	
	/**
	 * 获取系统时间：yyyy-MM-dd hh:mm:ss
	 * */
	private String getSystemDateTime()
	{
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strTime = sf.format(date);
		return strTime;
	}
}
