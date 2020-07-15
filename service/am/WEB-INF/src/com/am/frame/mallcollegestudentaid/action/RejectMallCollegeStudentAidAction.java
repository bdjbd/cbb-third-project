package com.am.frame.mallcollegestudentaid.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午4:29:57
 * @version 拒绝大学生资助申请
 */
public class RejectMallCollegeStudentAidAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("mall_college_student_aid.form.id");
		//获取拒绝原因
		String review_info = ac.getRequestParameter("mall_college_student_aid.form.review_info");
		if(Checker.isEmpty(review_info)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入拒绝原因(审核信息)");
		}else{
			//修改审核状态为审核驳回，并保存拒绝原因
			String updateStatusSQL = " UPDATE  mall_college_student_aid SET status = 4,review_info = '"+review_info+"' WHERE id = '"+id+"' ";
			db.execute(updateStatusSQL);
		}
		
		super.doAction(db, ac);
	}
	
}
