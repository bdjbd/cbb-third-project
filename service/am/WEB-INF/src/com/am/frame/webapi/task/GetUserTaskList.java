package com.am.frame.webapi.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年8月4日
 * @version 
 * 说明:<br />
 * 获取社员任务列表
 * 
 */
public class GetUserTaskList implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		
		//社员ID
		String  memberId=request.getParameter("member_id");
		//任务状态 
		String runState=request.getParameter("run_state");
		
		String querSQL=
		" select au.id AS utid,au.taskparame,au.createdate,am.membername,au.id,au.taskrunstate "+
		",ae.Name AS taskName,ae.TaskParame AS ttskparame "+
		" from am_usertask as au "+
		" left join am_member as am  on am.id = au.memberid "+
		" left join am_enterprisetask as ae on ae.id = au.entertaskid "+
		" LEFT JOIN am_TaskTemplate AS at On at.id=ae.tasktemplateid "+
		" where at.is_show_view=1 "+
		" AND au.memberid = '"+memberId+"' and au.taskrunstate= '"+runState+"'";
		
		DB db=null;
		
		JSONArray returnJsonArray = new JSONArray();
		
		try{
			db=DBFactory.newDB();
			
			MapList map=db.query(querSQL);
			
			int row_count=map.size();
			for(int i=0;i<row_count;i++) 
			{
				Row row=map.getRow(i);
				int column_count=row.size();
				JSONObject jo = new JSONObject();
				for (int j =0; j<column_count;j++) {
					String currentValue=row.get(j);
					if (currentValue == null || currentValue.trim().equalsIgnoreCase("null")) {
							jo.put(row.getKey(j).toUpperCase(), "");
					} else {
						currentValue=StringEscapeUtils.unescapeHtml4(currentValue);
						jo.put(row.getKey(j).toUpperCase(),currentValue);
					}
				}
			
				returnJsonArray.put(jo);
			}
			
			
			result.put("DATA", returnJsonArray);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return result.toString();
	}

}
