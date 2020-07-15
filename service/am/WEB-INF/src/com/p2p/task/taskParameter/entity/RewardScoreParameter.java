package com.p2p.task.taskParameter.entity;

import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.task.TaskActionParametes;
import com.p2p.task.taskParameter.ITaskParameterGetHtml;
import com.p2p.task.taskParameter.ITaskParameterReward;
/***
 * 积分奖励参数
 * @author Administrator
 *
 */
public class RewardScoreParameter implements ITaskParameterGetHtml,
		ITaskParameterReward {

	
	@Override
	public String executeRewar(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		String result="";
		try {
			//完成任务奖励，给会员奖励
			String sql="UPDATE  ws_member  SET integration=COALESCE(integration,0)+"+parameterValue
				+" WHERE member_code="+taskActionParam.memberCode;
			result=String.valueOf(DBFactory.getDB().execute(sql));
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getHtml(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		return "可获取"+parameterValue+"个积分。";
	}

}
