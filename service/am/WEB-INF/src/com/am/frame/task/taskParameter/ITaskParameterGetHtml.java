package com.am.frame.task.taskParameter;

import com.am.frame.task.TaskActionParametes;



/**
 * 任务参数获取HTML接口
 * @author Administrator
 *
 */
public interface ITaskParameterGetHtml {
	
	/**
	 * 获取参数的HTML代码
	 * @param parameterName  参数名
	 * @param parameterValue 参数值
	 * @param task  任务
	 * @return  HTML代码
	 */
	public String getHtml(String parameterName,String parameterValue,TaskActionParametes taskActionParam);
}
