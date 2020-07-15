package com.wisdeem.wwd.publicAccount;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

public class DeletePublicAccount extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取当前登陆人id
		String username = ac.getVisitor().getUser().getId();

		// 获得要删除的数据的编号
		String isUpdate[] = ac.getRequestParameters("_s_ws_public_accounts.list");
		String ids[] = ac.getRequestParameters("ws_public_accounts.list.public_id");

		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < isUpdate.length; i++) {
			if ("1".equals(isUpdate[i])) {
				list.add(new String[] { ids[i] });
			}
		}
		// 批量删除
		String sql = "delete from WS_ACTDETAL where public_id = ? ";
		String sqlstr = "delete from WS_PUBLIC_ACCOUNTS where public_id = ? ";
		db.executeBatch(sql, list, new int[] { Type.INTEGER });
		db.executeBatch(sqlstr, list, new int[] { Type.INTEGER });
		ac.getActionResult().addSuccessMessage("删除成功");
	}

}
