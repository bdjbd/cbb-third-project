package com.am.frame.task.recharge;

import com.fastunit.util.Checker;
import com.p2p.task.IUserTask;
import com.p2p.task.UserTaskAbstract;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 会员每日充值任务
 * @author Administrator
 *一次性充值>=元，则奖励XX元
 */
public class RechargeTask extends UserTaskAbstract implements IUserTask,Comparable<RechargeTask>{
	
	private static final String tag="com.p2p.task.recharge.RechargeTask";

	public static final String RECHARGE_TASK_ID="cbc1e019-8539-45a9-9886-f52a0f64001e";
	
	//一次性充值=100;奖励=10
	public static final String RECHARGE_CASH="一次性充值";
	//是否充值
	public static final String IS_RECHARGE="是否充值";
	
	//充值奖励金额比例
	private double rechargeCashRang=0d;
	
	
	
	/**
	 * 会员充值任务更新
	 * @param  memberCode 会员编号
	 * @param  rechargeCash 充值金额
	 */
	public boolean updateTaskProgress(String memberCode,double rechargeCash){
		
		boolean result=false;
		
		double targetRechargeCash=Double.valueOf(this.taskExplainParams.getValueOfName(RECHARGE_CASH));
		
		
		//判断是否达到目标金额，如果达到，则奖励任务
		if(rechargeCash>=targetRechargeCash&&"1".equals(this.taskRunState)){
			
			Utils.Log(tag,"奖励会员充值任务，充值现金:"+rechargeCash);
			
			this.executeReward();
			this.taskRunState="0";
			result=true;
		}
		
		saveTaskParams();
		return result;
	}

	
	
	@Override
	public void init(String memberCode, String taskId) {
		super.init(memberCode, taskId);
		
		//获取奖励金额
		String rechargCashRange=this.taskExplainParams.getValueOfName(RECHARGE_CASH);
		
		if(rechargCashRange!=null&&Checker.isDecimal(rechargCashRange)){
			this.rechargeCashRang=Double.valueOf(rechargCashRange);
		}
	}

	
	
	@Override
	public int compareTo(RechargeTask o) {
		//值越大，越靠前
		return (int)(o.getRechargeCashRang()-this.rechargeCashRang);
	}

	
	/**
	 * 获取充值任务的一次性充值金额。
	 * @return  任务目标，
	 */
	public double getRechargeCashRang() {
		return rechargeCashRang;
	}
	
	
}
