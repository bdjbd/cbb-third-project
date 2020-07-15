package com.am.frame.task.task;

import com.am.frame.task.params.RunTaskParams;



public interface ITaskEngine {
	
	/**
	 * 为所有会员分配任务，实体任务ID
	 * @param EntityTaskID
	 * @return
	 */
	boolean distributionTaskToUsers(String EntityTaskID); 

	/**
	 * 为所有会员删除任务，实体任务ID
	 * @param EntityTaskID
	 * @return
	 */
	boolean deleteTaskToUsers(String EntityTaskID);

	/**
	 * 为会员分配所有任务，会员ID
	 * @param memberID
	 * @return
	 */
	boolean distributionAllTaskToUser(String memberID);

	/**
	 * 执行任务
	 * @param MemberID
	 * @return
	 */
	boolean executTask(RunTaskParams parames);

	/**
	 * 获得任务详情
	 * @param MemberID  会员ID
	 * @param userTaskID    会员任务ID
	 * @return
	 */
	String getTaskHtmlView(String MemberID,String userTaskID);



}
