package com.am.cooperation.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 *@author wangxi
 *@create 2016年7月7日
 *@version
 *说明：我要合作审核Action
 */
public class CooperateExamineAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//处理结果
		String remarks=ac.getRequestParameter("triph_cooperation.form.remarks");
		//合作id
		String Id=ac.getRequestParameter("triph_cooperation.form.id");
		
		/**修改处理状态和处理结果**/
		String updeteSql="UPDATE triph_cooperation SET status='2',remarks='"+remarks+"' WHERE id='"+Id+"'";
		
		db.execute(updeteSql);
	}

}
