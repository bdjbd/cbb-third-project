package com.am.frame.systemAccount.action;

import org.json.JSONObject;

import com.am.frame.transactions.withdraw.WidthDrawManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;

/**
 * 账户提现
 * @author wz
 *
 */
public class AccountWidthDrawAction implements Action{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		DB db=DBFactory.newDB();
		
		String outMemberid = ac.getVisitor().getUser().getOrgId();
		
		String id = ac.getRequestParameter("account_withdrawals.form.id");
		
		String outAccountCode = ac.getRequestParameter("account_withdrawals.form.out_account_id");
		
		String inAccountId = ac.getRequestParameter("account_withdrawals.form.in_account_id");
		
		String virementNumber = ac.getRequestParameter("account_withdrawals.form.out_money");
		
		String remarks = ac.getRequestParameter("account_withdrawals.form.remarks");
		
		String account_type  = ac.getRequestParameter("account_withdrawals.form.account_type");
	
		
		String actionParams = ac.getActionParameter();
		
		WidthDrawManager widthDrawManager = new WidthDrawManager();
		
		JSONObject result = widthDrawManager.execute(db,outMemberid, outAccountCode, inAccountId, virementNumber, account_type,remarks);
		
		String sql = "UPDATE withdrawals SET settlement_state = 1 WHERE id = '"+result.getString("id")+"' ";
		db.execute(sql.toString());
		
//		if(!"0".equals(result.get("code"))){
//			
//			ac.getActionResult().setSuccessful(false);
//			ac.getActionResult().addErrorMessage(result.getString("msg"));
//			
//		}else{
//			//获取副主任
//			String sql = "SELECT second_director,director FROM mall_auditpersonsetting WHERE orgid= '"+outMemberid+"' ";
//			MapList list = db.query(sql);
//			if(!Checker.isEmpty(list)){
//				
//				String second_director = list.getRow(0).get("second_director");
//				String director = list.getRow(0).get("director");
//				
//				//修改审核人
//				String updateSql = "UPDATE withdrawals SET audit_person = '"+second_director+"',second_director = '"+second_director+"',director="+director+" WHERE id = '"+result.getString("id")+"' ";
//				db.execute(updateSql);
//				
//				ac.getActionResult().setSuccessful(true);
//				ac.getActionResult().addSuccessMessage("保存申请成功，点击提交后可进行提交审核！");
				
				
//				ac.getActionResult().setUrl("/am_bdp/account_withdrawals.apply.do?m=e&withdrawals.form.id="+result.getString("id")+"&outaccountid="+outAccountCode);
//			}else{
//				ac.getActionResult().setSuccessful(false);
//				ac.getActionResult().addErrorMessage("请先设置审核人！");
//			}
//			
//			
//		
//		}
		return ac;
	
	}
	
}
