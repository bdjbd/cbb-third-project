package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;

/**
 * 会员信息完善任务参数
 * @author Administrator
 */
public class MemberInfoCompleteParameter implements ITaskParameterGetHtml,ITaskParameterReward {

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		String result="信息未完善";
		
		if("0".equals(taskActionParam.taskRunState)){
			//任务完成
			result="信息已完善";
		}
		return result;
	}

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		return "";
	}

}
