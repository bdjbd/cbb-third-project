package com.p2p.recharge;

import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;


/**
 * 充值管理类
 * @author Administrator
 */
public class RechargeManager {

	private static RechargeManager  rechargeManager;
	
	private RechargeManager(){}
	
	public static RechargeManager getInstance(){
		if(rechargeManager==null){
			rechargeManager=new RechargeManager();
		}
		return rechargeManager;
	}
	
	
	/**
	 * 
	 * @param memberCode
	 * @param cash
	 * @return
	 */
	public boolean updateMemberCash(String memberCode,Double cash){
		
		int result=0;
		
		try {
			//完成任务奖励，给会员奖励
			String sql="UPDATE  ws_member  SET cash=COALESCE(cash,0)+"+cash
				+" WHERE member_code="+memberCode;
			
			result=DBFactory.getDB().execute(sql);
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		
		return result>0?true:false;
	}
	
	
	
}
