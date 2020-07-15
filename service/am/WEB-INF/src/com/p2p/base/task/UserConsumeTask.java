package com.p2p.base.task;

import com.p2p.base.ParametersList;

/**
 * Author: Mike
 * 2014年7月20日
 * 说明：
 *  用户消费任务
 **/
public class UserConsumeTask extends UserTaskAbstract {

	@Override
	public ParametersList getTaskParam() {
		return targetParame;
	}

	@Override
	public boolean saveTaskParam() {
		return false;
	}

	@Override
	public String getTaskCompletionStatus() {
		return null;
	}

	@Override
	public String getUserTaskInterface() {
		return null;
	}

	@Override
	public void init(String userID, String userTaskID) {
		// TODO Auto-generated method stub
		
	}

}
