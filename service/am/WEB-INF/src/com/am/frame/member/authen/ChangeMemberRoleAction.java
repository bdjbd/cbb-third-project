package com.am.frame.member.authen;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.BadgeImpl;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

public class ChangeMemberRoleAction extends DefaultAction {

	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//社员ID
		String memberId=ac.getRequestParameter("am_member_type_role.id");
		//社员角色
		String memberRole=ac.getRequestParameter("am_member_type_role.member_identity");
		
		String sql="UPDATE am_member SET member_identity=? WHERE id=? ";
		
		db.execute(sql,new String[]{
				memberRole,memberId
		}, new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
		
		//如果是技术专家，则需要增加徽章
		if("93b46702-d0c2-4cc2-bee2-202ecc3469ff".equals(memberRole)){
			//添加技术专家徽章
			String Badge_JSZJ = BadgeImpl.Badge_JSZJ;
			AMBadgeManager badgeManager = new AMBadgeManager();
			badgeManager.addBadgeByEntBadgeCode(db,Badge_JSZJ,memberId);
		}
		
	}
	
	
}
