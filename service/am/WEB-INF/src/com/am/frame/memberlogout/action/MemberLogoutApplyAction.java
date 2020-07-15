package com.am.frame.memberlogout.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月14日
 * @version 
 * 说明:<br />
 * 社员注销审核通过Action
 */
public class MemberLogoutApplyAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		super.doAction(db, ac);
		
		//ID
		String id=ac.getRequestParameter("am_member_logout.form.id");
		//审核原因
		String  remarks=ac.getRequestParameter("am_member_logout.form.remarks");
		
		String actionParams=ac.getActionParameter();
		
		
		String updateSQL="UPDATE AM_MEMBER_LOGOUT SET status=?,remarks=? WHERE id=? ";
		
		if(Checker.isEmpty(remarks)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("审核原因不能为空！");
			return ;
		}
		
		if("forward".equalsIgnoreCase(actionParams)){
			//同意  修改社员状态为注销
			db.execute(updateSQL,new String[]{"2",remarks,id},new int[]{Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
			
			//更新社员状态
			String updateMemerSQL="UPDATE am_member SET status=1 "
					+ " WHERE id IN (SELECT memberid FROM AM_MEMBER_LOGOUT WHERE id=? ) ";
			db.execute(updateMemerSQL,new String[]{id},new int[]{Type.VARCHAR});
		}
		if("backward".equalsIgnoreCase(actionParams)){
			//驳回
			updateSQL="UPDATE AM_MEMBER_LOGOUT SET status=? WHERE id=? ";
			db.execute(updateSQL,new String[]{"3",id},new int[]{Type.INTEGER,Type.VARCHAR});
		}
		
	}
	
}
