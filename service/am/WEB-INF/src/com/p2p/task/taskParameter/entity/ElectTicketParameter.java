package com.p2p.task.taskParameter.entity;

import com.p2p.task.TaskActionParametes;
import com.p2p.task.taskParameter.ITaskParameterGetHtml;
import com.p2p.task.taskParameter.ITaskParameterReward;

/**
 * 电子券任务属性
 * @author Administrator
 *
 */
public class ElectTicketParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		return null;
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		return null;
	}

}
