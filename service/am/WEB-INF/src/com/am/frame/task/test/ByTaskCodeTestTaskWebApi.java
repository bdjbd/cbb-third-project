package com.am.frame.task.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年8月3日
 * @version 
 * 说明:<br />
 * 任务测试接口
 */
public class ByTaskCodeTestTaskWebApi implements IWebApiService {

	//日志
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//会员id
		String  memberId=request.getParameter("member_id");
		//任务code
		String taskCode=request.getParameter("task_Code");
		
		//1,初始化任务
		//2，执行任务
		DB db=null;
		
		try{
			
			testTask(memberId,taskCode,db);
			
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
		return null;
	}

	
	private void testTask(String memberId, String taskCode,DB db) {
		TaskEngine taskEngine = TaskEngine.getInstance();
		//taskEngine.distributionAllTaskToUser(memberId);
		taskEngine.addUserAllTask(memberId,null, db);
		//1,个用户初始化任务
	
		RunTaskParams params=new RunTaskParams();
		params.setMemberId(memberId);
		params.setTaskCode(taskCode);
		
		logger.info("memberId:"+memberId);
		logger.info("taskCode:"+taskCode);
		
		 //执行任务
		taskEngine.executTask(params);		
	}

}
