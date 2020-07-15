package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;


public class IsRechargeCashParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		return "";
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		String result="";
		
		if("0".equals(taskActionParam.taskRunState)){
			//任务完成，执行奖励
			result="已经完成任务";
		}else{
			result="任务未完成";
		}
		
		return result;
	}

}
