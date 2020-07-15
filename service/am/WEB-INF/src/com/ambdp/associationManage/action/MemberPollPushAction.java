package com.ambdp.associationManage.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月15日
 *@version
 *说明：社员代表大会发布Action
 */
public class MemberPollPushAction extends DefaultAction{
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// id
		String Id = ac.getRequestParameter("mall_member_poll.form.id");
		if (!Checker.isEmpty(Id)) {
			//将状态改为发布  0---草稿 1----发布
			String updateSql = "UPDATE mall_member_poll  SET status ='1',push_time='now()'  WHERE id='" + Id + "' ";
			db.execute(updateSql);
		}
	}

}
