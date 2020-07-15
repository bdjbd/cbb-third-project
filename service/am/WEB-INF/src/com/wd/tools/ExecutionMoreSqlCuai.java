package com.wd.tools;

import org.json.JSONArray;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.wd.ICuai;

/**
 *   说明:
 * 		<br>执行多条SQL语句的插入。
 *   @creator	岳斌
 *   @create 	2012-12-6 
 *   @revision	$Id
 */
public class ExecutionMoreSqlCuai implements ICuai {
	private DB db;
	private JSONArray resultJsa=new JSONArray();
	@Override
	public JSONArray doAction(JSONArray jsonArray) {
		if(jsonArray!=null&&jsonArray.length()>0){
			resultJsa.put(executeSQLS(jsonArray));
		}
		return resultJsa;
	}
	/**执行多条SQL*/
	private int executeSQLS(JSONArray jsonArray) {
		int result=0;
		try{
			int temp=jsonArray.length();
			db=DBFactory.getDB();
			for(int i=temp;i>0;i--){
				String sql=jsonArray.getJSONObject(i-1).getString("SQL");
				db.execute(sql);
				result++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

}
