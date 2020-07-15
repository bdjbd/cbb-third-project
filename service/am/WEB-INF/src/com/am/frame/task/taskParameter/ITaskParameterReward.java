package com.am.frame.task.taskParameter;

import com.am.frame.task.TaskActionParametes;



/**
 * 执行奖励任务参数
 * @author Administrator
 *
 */
public interface ITaskParameterReward {

	/**
	 * 执行奖励
	 * @param parameterName  参数名
	 * @param parameterValue 参数值
	 * @param task  任务
	 * @return
	 */
	public String executeRewar(String parameterName,String parameterValue,TaskActionParametes taskActionParam);
	
}
