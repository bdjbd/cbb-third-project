package com.am.frame.member.authen;



import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class SaveAuthenAjaAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		String[] list = ac.getRequestParameters("_s_mall_userbadge.list");
		String[] enterprisebadgeid = ac.getRequestParameters("mall_userbadge.list.enterprisebadgeid");
		
		Table table = ac.getTable("mall_userbadge");
		
		if (!Checker.isEmpty(list)) {
			for (int i = 0; i < list.length; i++) {
				for (int j = i+1; j < list.length; j++) {
					if (enterprisebadgeid[i].equals(enterprisebadgeid[j])) {
						ac.getActionResult().setSuccessful(false);
						ac.getActionResult().addErrorMessage("数据有重复，无法保存");
					}
				}
			}
		}
	db.save(table);
	}
}
