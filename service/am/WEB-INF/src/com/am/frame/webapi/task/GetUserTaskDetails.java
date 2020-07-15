package com.am.frame.webapi.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.task.task.TaskEngine;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年8月4日
 * @version 
 * 说明:<br />
 * 根据任务id获取任务信息
 * 
 */
public class GetUserTaskDetails implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		DB db=null;

		JSONObject result=new JSONObject();
		
		try{
			//会员ID
			String memberId=request.getParameter("member_id");
			
			//会员任务ID
			String userTaskId=request.getParameter("user_task_id");
			
			TaskEngine taskEngine=TaskEngine.getInstance();
			//获取任务视图代码
			String viewResult=taskEngine.getTaskHtmlView(memberId,userTaskId);
			
			result.put("CODE", 0);
			result.put("VIEW", viewResult);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
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
