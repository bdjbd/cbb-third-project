package com.p2p.task.taskParameter;

import com.p2p.task.TaskActionParametes;


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
