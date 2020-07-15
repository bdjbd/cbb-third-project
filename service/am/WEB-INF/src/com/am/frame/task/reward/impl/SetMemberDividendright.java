package com.am.frame.task.reward.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Transaction;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * 更新用户分红权
 * @author xianlin
 *
 */
public class SetMemberDividendright implements ITaskReward {

	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	public boolean execute(RunTaskParams param) {
		
		boolean bool=false;
		DB db = null;
		Transaction  tx=null;
		
		try {
			db = DBFactory.newDB();
			if(db.getConnection().getAutoCommit()){
				db.getConnection().setAutoCommit(false);
			}
			//开始事务
			tx=db.beginTransaction();
			
			//会员id
			String MemberId = param.getMemberId();
			//平台设置分红权获取金额
			long setMaxmoney = RechargeRewardMethod.getInstance().returnsetmaxMoney("cumulativecharge", db);
			//用户累计金额 用户账号表中  total_charge_amount
			long MemberMoney = RechargeRewardMethod.getInstance().QueryMoney(MemberId, db);
			
			//用户累计金额
			long Accumulatedamount = MemberMoney%setMaxmoney;
			//用户分红权
			long bonusNumber = MemberMoney/setMaxmoney;
			
			//如果可以获取分红权，进行分红权奖励
			if(bonusNumber>0)
			{
				//扣除累计金额
				RechargeRewardMethod.getInstance().UpdateSetupMemberCumulativeChargeMoney(MemberId, Accumulatedamount, db);
				
				RechargeRewardMethod.getInstance().increaseMemberDividendRight(MemberId, bonusNumber, db);
			}
			
			//提交事务
			tx.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		return bool;
	}
	
}
