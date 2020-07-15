package com.am.borrowing.collegestudent.action;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.systemAccount.bean.TransactionBusinessBeans;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
/**
 * 资助大学生后台action
 *
 */
public class CollegeStudentAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		TransactionBusinessBeans business = new TransactionBusinessBeans();
		
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		//获取转账金额
//		String out_money = ac.getRequestParameter("mall_college_student_aid_stu.form.sum");
		String out_money = Var.get("StudenSupportMoney");
		//获取自己需处理的业务id
		String business_id = ac.getRequestParameter("mall_college_student_aid_stu.form.id");
		//获取转账账号
		String loginaccout = ac.getRequestParameter("mall_college_student_aid_stu.form.phone");
		//获取转账账号
		String member_id = ac.getRequestParameter("mall_college_student_aid_stu.form.in_member_id");
		
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
			ac.getActionResult().addErrorMessage("组织机构现金账户余额不足，无法资助！");
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().setUrl("/am_bdp/mall_college_student_aid_stu.do?m=s");
		} else{
		
		business.setOut_group_account_code(SystemAccountClass.GROUP_CASH_ACCOUNT);
		business.setIn_group_account_code(SystemAccountClass.CASH_ACCOUNT);
		business.setIs_in_account_code_show(false);
		business.setOut_money(out_money);
		business.setIs_out_money_disabled(true);
		business.setIn_member_id(member_id);
		business.setIs_out_login_phone_disabled(true);
		business.setPerson_remaker("资助大学生"+out_money+"元！");
		
		business.setBusiness_id(business_id);
		//自定义业务的回调处理
		business.setSuccess_call_back("com.am.borrowing.subsidize.SubsidizeStudentCallback");
		
		business.formatBusiness(ac);
		                                                                         
		ac.getActionResult().setUrl("/am_bdp/mall_account_other_virement.form.do?autoback=/am_bdp/mall_college_student_aid_stu.form.do&m=a");
		}
		
		super.doAction(db, ac);
	}
	
}
