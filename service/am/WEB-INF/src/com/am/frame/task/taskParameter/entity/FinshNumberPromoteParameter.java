package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;




/**
 * 目标推广数量参数类
 * @author Administrator
 *
 */
public class FinshNumberPromoteParameter implements ITaskParameterGetHtml,
	ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskAcionParam) {
		return "";
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskAcionParam) {
		return "完成"+parameterValue+"个人的推广。";
	}


}
