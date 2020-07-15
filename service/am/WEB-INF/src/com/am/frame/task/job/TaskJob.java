package com.am.frame.task.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.am.frame.task.TaskInit;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 
 * 说明:<br />
 * 
 * 日常任务管理Job
 */
public class TaskJob implements Job{
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//获取所有的企业
		String getOrg="SELECT * FROM ws_org_baseinfo";
		try {
			DB db=DBFactory.getDB();
			MapList orgMap=db.query(getOrg);
			if(Checker.isEmpty(orgMap))return ;
			
			//查询企业企业会员的日常任务
			//删除会员未完成的日常任务
			String deleteFaildTaskSQL="DELETE FROM am_usertask "
					+ " WHERE enterprisetaskid "
					+ " IN ( SELECT et.id FROM am_enterprisetask AS et "
					+ " LEFT JOIN am_tasktemplate AS tt ON et.tasktemplateid=tt.id "
					+ " WHERE tt.tasktemplatetype='0' ) AND taskrunstate='1'";
			db.execute(deleteFaildTaskSQL);
			
			//为所有会员初始化日常任务
			String enterTaskSQL="SELECT et.id,et.orgid FROM am_enterprisetask AS et "
					+ " LEFT JOIN am_tasktemplate AS tt "
					+ " ON et.tasktemplateid=tt.id WHERE tt.tasktemplatetype='0'";
			
			MapList orgTaskMap=db.query(enterTaskSQL);
			
			if(Checker.isEmpty(orgTaskMap))return;
			
			for(int i=0;i<orgTaskMap.size();i++){
				TaskInit.initUserTask(orgTaskMap.getRow(i).get("orgid"),orgTaskMap.getRow(i).get("id"));
			}
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}
}
