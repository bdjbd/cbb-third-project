package com.am.marketplace_entity.action.supers;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午12:11:52
 * @version 表单通过审核
 */
public class PassMarketplaceEntityFormAction extends DefaultAction {
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String userId=ac.getVisitor().getUser().getId();
		// 通过审核
		String id = ac.getRequestParameter("mall_super_marketplace_entity.form.id");
		String release_status = ac.getRequestParameter("mall_super_marketplace_entity.form.release_status");
		String review_remarks = ac.getRequestParameter("mall_super_marketplace_entity.form.review_remarks");
		String audit_fee = ac.getRequestParameter("mall_super_marketplace_entity.form.audit_fee");
		String memberid = ac.getRequestParameter("mall_super_marketplace_entity.form.member_id");
		
		//审核通过  ，向抗风险自救金账户转入手续费
		VirementManager vir = new VirementManager();
		if("0".equals(release_status)){		
		vir.execute(db,"",memberid,"",SystemAccountClass.ANTI_RISK_SELF_SAVING_ACCOUNT,audit_fee,"发布农村大市场摊位手续费", "", "", false);
		}else{
		vir.execute(db,"",memberid,"",SystemAccountClass.GROUP_ANTI_RISK_SELF_SAVING_ACCOUNT,audit_fee,"发布农村大市场摊位手续费", "", "", false);	
		}			
		String sql = " UPDATE mall_marketplace_entity SET status = 3,review_remarks = '"+review_remarks+"',shelve_time='now()',operationer='"+userId+"' WHERE id = '"+id+"' ";
		db.execute(sql);
	}

	
}
