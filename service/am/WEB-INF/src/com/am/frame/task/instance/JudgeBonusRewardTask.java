package com.am.frame.task.instance;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.reward.impl.RechargeRewardMethod;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.jdbc.DB;
/**
 * 处理用户充值奖励
 * @author xianlin
 *2016年4月14日
 */

public class JudgeBonusRewardTask extends AbstractTask {

	/**获取分红权 本次消费充值的金额数量，单位分**/
	public static String RECHARGEMONEY="RECHARGEMONEY";
	
	/**获取分红权任务编码**/
	public static String TASK_CODE="GET_RECHANGE_DIVIDEND_RIGHT_TASK";
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams, DB db)
			throws Exception {
		boolean bool=false;
		
		//用户充值金额  单位：分
		long money = Long.parseLong(runTaskParams.getParams(RECHARGEMONEY).toString()); 
		
		RechargeRewardMethod.getInstance().UpdateMemberCumulativeChargeMoney(runTaskParams.getMemberId(), money, db);
		//获取变量中设置的奖励的金额
		long setmaxmoney = RechargeRewardMethod.getInstance().returnsetmaxMoney("cumulativecharge", db);
		//用户累计充值金额
		long cumulativechargemoney = RechargeRewardMethod.getInstance().QueryMoney(runTaskParams.getMemberId(), db);
		
		if(setmaxmoney<=cumulativechargemoney)
		{
			bool=true;
		}
		return bool;
	}

}
