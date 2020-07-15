package com.am.frame.task.taskParameter.entity;

import com.am.frame.elect.ElectTicketManager;
import com.am.frame.elect.EterpElectTicket;
import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;



/**
 * 电子券任务属性
 * @author Administrator
 *
 */
public class ElectTicketParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		
		//添加电子券
		ElectTicketManager etManager=ElectTicketManager.getInstance();
		boolean result=etManager.addMemberElectTicket(taskActionParam.memberCode, parameterValue);
		
		return ""+result;
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		//获取企业电子券信息
		ElectTicketManager etm=ElectTicketManager.getInstance();
		EterpElectTicket eet=etm.getEterElectTicketById(parameterValue);
		
		return "奖励"+eet.getSname();
	}

	
}
