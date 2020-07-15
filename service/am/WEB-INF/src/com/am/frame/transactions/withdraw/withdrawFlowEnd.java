package com.am.frame.transactions.withdraw;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.FlowEndAction;

/**
 * 提现流程完成后的处理类
 * @author wz
 *
 */
public class withdrawFlowEnd implements FlowEndAction{

	@Override
	public void cancel(DB arg0, ActionContext arg1, String arg2, boolean arg3) throws Exception {
		
	}

	@Override
	public void pass(DB db, ActionContext ac, String flowInstId) throws Exception {
		
		String sql = "select * from withdrawals where flowid = '"+flowInstId+"'";
				
		MapList list = db.query(sql);
				
//		String usql = "update mall_account_info set balance = '"+list.getRow(0).get("balance")+list.getRow(0).get("cash_withdrawal")+list.getRow(0).get("counter_fee")+"' where id='"+list.getRow(0).get("out_account_id")+"'";
		String usql = "update mall_account_info set settlement_state = '1' where id='"+list.getRow(0).get("out_account_id")+"'";
		
		db.execute(usql);
	}

	@Override
	public void unpass(DB db, ActionContext ac, String flowInstId) throws Exception {
		
		String sql = "select * from withdrawals where flowid = '"+flowInstId+"'";
		
		MapList list = db.query(sql);
		
//		String usql = "update mall_account_info set balance = '"+list.getRow(0).get("balance")+list.getRow(0).get("cash_withdrawal")+list.getRow(0).get("counter_fee")+"' where id='"+list.getRow(0).get("out_account_id")+"'";
		String usql = "update mall_account_info set settlement_state = '5' where id='"+list.getRow(0).get("out_account_id")+"'";
		
		db.execute(usql);
		
	}

}
