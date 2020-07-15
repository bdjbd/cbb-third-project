package com.am.frame.mallcollegestudentaid.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午4:29:09
 * @version 通过大学生资助申请
 */
public class PassMallCollegeStudentAidAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		String id = ac.getRequestParameter("mall_college_student_aid.form.id");
		//获取审核说明
		String review_info = ac.getRequestParameter("mall_college_student_aid.form.review_info");
		//申请金额
		double amount_of_subsidy = Double.parseDouble(ac.getRequestParameter("mall_college_student_aid.form.money"));
		//申请人
		String member_id = ac.getRequestParameter("mall_college_student_aid.form.member_id");
		
		//修改审核状态为审核通过，并修改审核说明
		String updateStatusSQL = " UPDATE  mall_college_student_aid SET status = 3,review_info = '"+review_info+"',pass_time=now() WHERE id ='"+id+"'";
		int res = db.execute(updateStatusSQL);

	}
	
}
