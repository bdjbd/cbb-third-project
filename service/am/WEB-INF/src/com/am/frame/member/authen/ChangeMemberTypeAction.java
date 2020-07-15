package com.am.frame.member.authen;

import com.am.frame.member.MemberManager;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * 修改社员类型
 * @author yuebin
 *
 */
public class ChangeMemberTypeAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//社员ID
		String memberId=ac.getRequestParameter("am_member_type_edit.id");
		//社员类型
		String memberType=ac.getRequestParameter("am_member_type_edit.member_type");
		
		MemberManager mm=new MemberManager();
		
		mm.changeMemberType(db, memberId, memberType);
		
		TaskEngine taskEng=TaskEngine.getInstance();
		
		//社员授权等级任务
		taskEng.resetUserTaskByTaskCode(db, memberId,"MEMBER_AUTHORITY_BADGE_TASK", null);
		
		//获取志愿者账户提现资格任务
		taskEng.resetUserTaskByTaskCode(db, memberId,"GET_VOLUNTEER_ACCOUNT_WITH_QUALIFICATION",null);
		
		mm.createInvitationCode(db, memberId, "");
		
	}
	
}
