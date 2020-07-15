package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;

/**
 * 会员信息完善任务参数
 * @author Administrator
 */
public class MemberPerfectParameter implements ITaskParameterGetHtml,ITaskParameterReward {

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		String result=parameterName;
		return result;
	}

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		return "";
	}

}
