package com.am.frame.task.taskParameter.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.taskParameter.ITaskParameterGetHtml;
import com.am.frame.task.taskParameter.ITaskParameterReward;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;


public class RechargeCashParameter implements ITaskParameterReward,
		ITaskParameterGetHtml {

	
	private static final Logger logger=LoggerFactory.getLogger(com.am.frame.task.taskParameter.entity.RechargeCashParameter.class);
			
	
	@Override
	public String getHtml(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		return "一次性充值"+parameterValue+"元";
	}

	@Override
	public String executeRewar(String parameterName, String parameterValue,
			TaskActionParametes taskActionParam) {
		
		//updateUserCash(taskActionParam.memberCode,parameterValue);
		
		return "";
	}

	
	/**
	 * 
	 * 修改会员现金额度
	 * @param memberCode
	 * @param parameterValue
	 */
	public void updateUserCash(String memberCode, String parameterValue) {
		
		try{
			
			logger.info("会员充值任务奖励,会员编号:"+memberCode+" \t 奖励金额:"+parameterValue);
			
			String sql="";
			
			DB db=DBFactory.getDB();
			db.execute(sql);
			
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
	}

}
