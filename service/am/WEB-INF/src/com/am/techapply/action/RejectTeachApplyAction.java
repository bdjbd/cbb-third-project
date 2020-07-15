package com.am.techapply.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月3日 下午7:06:43
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class RejectTeachApplyAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("mall_tech_apply.form.id");
		//获取拒绝原因
		String review_remarks = ac.getRequestParameter("mall_tech_apply.form.remark");
		if(Checker.isEmpty(review_remarks)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入拒绝原因");
		}else{
			//修改审核状态为审核驳回，并保存拒绝原因
			String updateStatusSQL = " UPDATE  am_member SET is_auth = 3,remark = '"+review_remarks+"' WHERE id = '"+id+"' ";
			db.execute(updateStatusSQL);
		}
	}	
}
