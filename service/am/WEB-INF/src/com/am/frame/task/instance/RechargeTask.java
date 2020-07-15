package com.am.frame.task.instance;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月7日 上午10:39:00 
 * @version 1.0   
 */
public class RechargeTask extends AbstractTask{

	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams, DB db)
			throws Exception {
		/**
		 * 更新用户任务为已完成。
		 * 如果返回true，执行奖励规则
		 */
		db=DBFactory.getDB();
		
		String udpateSQL="UPDATE am_usertask SET taskrunstate='0' WHERE id='"+this.id+"' ";
		
		runTaskParams.addRewardTargetMember(runTaskParams.getMemberId());
		
		db.execute(udpateSQL);
		
		return true;
		
		}	
	}

