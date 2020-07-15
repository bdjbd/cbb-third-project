package com.am.marketplace_entity.action.supers;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午12:45:12
 * @version 审核拒绝
 */
public class RejectMarketplaceEntityFormAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String userId=ac.getVisitor().getUser().getId();
		// 审核拒绝
		String id = ac.getRequestParameter("mall_super_marketplace_entity.form.id");
		String release_status = ac.getRequestParameter("mall_super_marketplace_entity.form.release_status");
		String review_remarks = ac.getRequestParameter("mall_super_marketplace_entity.form.review_remarks");
		String audit_fee = ac.getRequestParameter("mall_super_marketplace_entity.form.audit_fee");
		String memberid = ac.getRequestParameter("mall_super_marketplace_entity.form.member_id");
		
		
		//审核拒绝，退还手续费
		VirementManager vir = new VirementManager();
		if("0".equals(release_status)){
		vir.execute(db,memberid,"","",SystemAccountClass.CASH_ACCOUNT,audit_fee,"发布摊位未通过，退换手续费", "", "", false);
		}else{
		vir.execute(db,memberid,"","",SystemAccountClass.GROUP_CASH_ACCOUNT,audit_fee,"发布摊位未通过，退换手续费", "", "", false);
		}
		
		if(!Checker.isEmpty(review_remarks)){
			String sql = " UPDATE mall_marketplace_entity SET status = 7,review_remarks = '"+review_remarks+"',operationer='"+userId+"'  WHERE id = '"+id+"' ";
			db.execute(sql);
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请输入拒绝原因！");
		}
		
	}
		
}
