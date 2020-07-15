package com.am.instore.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.flow.FlowContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.FlowAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月28日
 *@version
 *说明：入库管理 同意Action
 */
public class InstoreForwardFlowAction extends FlowAction{
	
	@Override
	public void doFlow(DB db, ActionContext ac, FlowContext fc)
			throws Exception {
		
		if(fc.getTaskId()==21){
			//流程id
			String flowId = fc.getFlowInstId();
			Long totalAmount = 0L;
			String orgId = " ";
			Long balance = 0L;
			String querSql = " SELECT *  FROM p2p_instore  WHERE flow_id='"+flowId+"' ";
			MapList list = db.query(querSql);
			if (!Checker.isEmpty(list)) {
				//入库总价
				totalAmount= Long.parseLong(list.getRow(0).get("total_amount"));
				//入库的机构id
				orgId = list.getRow(0).get("orgid");
			}
			//查询机构帐号余额
			String accountInfoSql = "SELECT balance FROM  mall_account_info "
					+ " WHERE member_orgid_id='"+orgId+"'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='GROUP_CASH_ACCOUNT')";
			MapList accountInfoList = db.query(accountInfoSql);
			if (!Checker.isEmpty(accountInfoList)) {
				//帐号余额
				balance= Long.parseLong(accountInfoList.getRow(0).get("balance"));
			}
			if(totalAmount>balance){
				ac.getActionResult().addErrorMessage("账户余额不足！");
				ac.getActionResult().setSuccessful(false);
				fc.setStartNextTask(false);
				return ;
			}
		}
		
		super.doFlow(db, ac, fc);
	}
}
