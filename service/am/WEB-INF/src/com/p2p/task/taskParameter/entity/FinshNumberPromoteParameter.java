package com.p2p.task.taskParameter.entity;

import com.p2p.task.TaskActionParametes;
import com.p2p.task.taskParameter.ITaskParameterGetHtml;
import com.p2p.task.taskParameter.ITaskParameterReward;



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
