package com.am.frame.task.taskParameter.entity;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 奖励现金
 * @author Administrator
 *
 */
public class RewardCashParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		String result="";
		try {
			//完成任务奖励，给会员奖励
			String sql="UPDATE  am_member  SET cash=COALESCE(cash,0)+"+parameterValue
				+" WHERE id='"+taskActionParam.memberCode+"'";
			
			result=String.valueOf(DBFactory.getDB().execute(sql));
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		String result="返现金"+parameterValue+"元";
		
		return result;
	}

}
