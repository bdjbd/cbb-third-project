package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;



/**
 * 已推广数量属性
 * @author Administrator
 *
 */
public class NowNumberPromoteParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		return "";
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		return "目前已完成"+parameterValue+"人的推广。";
	}


}
