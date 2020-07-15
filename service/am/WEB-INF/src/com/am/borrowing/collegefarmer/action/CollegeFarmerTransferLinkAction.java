package com.am.borrowing.collegefarmer.action;

import org.json.JSONObject;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 资助农民后台action
 *
 */
public class CollegeFarmerTransferLinkAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		String out_money = ac.getRequestParameter("mall_college_student_aid_farmer.form.sum");
		String business_id = ac.getRequestParameter("mall_college_student_aid_farmer.form.id");
		String loginaccout = ac.getRequestParameter("mall_college_student_aid_farmer.form.phone");
		
		String assist = ac.getRequestParameter("mall_college_student_aid_farmer.form.assist");
		
		// 查询机构帐号余额
		Long balance = 0L;
		Long money = Long.parseLong(out_money)*100;
		String accountInfoSql = "SELECT balance FROM  mall_account_info " + " WHERE member_orgid_id='" + orgid
				+ "'  AND a_class_id=(SELECT id FROM mall_system_account_class WHERE sa_code='GROUP_CASH_ACCOUNT')";
		MapList accountInfoList = db.query(accountInfoSql);
		if (!Checker.isEmpty(accountInfoList)) {
			// 帐号余额
			balance = Long.parseLong(accountInfoList.getRow(0).get("balance"));
		}
		if (money > balance||balance<0) {
			ac.getActionResult().addErrorMessage("组织机构现金账户余额不足，无法帮扶！");
			ac.getActionResult().setSuccessful(false);
			return;
		} else{
		
		business.setOut_group_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		business.setIn_group_account_code(SystemAccountClass.CASH_ACCOUNT);
		business.setIs_in_account_code_show(false);
		business.setOut_money(out_money);
		business.setIs_out_money_disabled(true);
		business.setIn_member_id(business_id);
		business.setIs_out_login_phone_disabled(true);
		business.setPerson_remaker("精准帮扶，帮扶农民"+out_money+"元！");
		business.setBusiness_id(business_id);
		business.setSuccess_call_back("com.am.borrowing.subsidize.SubsidizeFarmerCallback");
		
		JSONObject jso = new JSONObject();
		jso.put("assist", assist);
		jso.put("out_member_id",ac.getVisitor().getUser().getOrgId());
		jso.put("out_group_account_code", SystemAccountClass.GROUP_CASH_ACCOUNT);
		jso.put("in_member_id", business_id);
		jso.put("out_money", out_money);
		jso.put("roleid", "");
		
		business.setCustomer_params(jso);
		
		business.formatBusiness(ac);
		
		ac.getActionResult().setUrl("/am_bdp/mall_account_other_virement.form.do?autoback=/am_bdp/mall_college_student_aid_farmer.form.do&m=a");
		
		}
		
		
		
		super.doAction(db, ac);
	}
	
}
