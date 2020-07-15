package com.am.techapply.action;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.BadgeImpl;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** * @author  作者：yangdong
 * @date 创建时间：2016年5月3日 下午7:06:24
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class PassTeachApplyAction  extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("mall_tech_apply.form.id");
		//申请人
		String member_id = ac.getRequestParameter("mall_tech_apply.form.id");
		//获取审核说明
		String review_remarks = ac.getRequestParameter("mall_tech_apply.form.remark");
		//修改审核状态为审核通过，并修改审核说明
		String updateStatusSQL = " UPDATE  am_member SET is_auth =1,remark = '"+review_remarks+"' WHERE id = '"+id+"' ";
		int res = db.execute(updateStatusSQL);
		
		String Badge_JSZJ = BadgeImpl.Badge_JSZJ;
		
		if(res!=0){
			AMBadgeManager badgeManager = new AMBadgeManager();
			badgeManager.addBadgeByEntBadgeCode(db,Badge_JSZJ,member_id);
		}
		
	}

}
