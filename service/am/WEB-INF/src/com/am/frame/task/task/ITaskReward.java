package com.am.frame.task.task;

import com.am.frame.task.params.RunTaskParams;



public interface ITaskReward {
	/**
	 * 执行任务奖励
	 * @param memberID
	 * @param value
	 * @return
	 */
    boolean execute(RunTaskParams param);
    
}
