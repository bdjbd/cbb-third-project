package com.fastunit.app.user;

import java.util.List;

import com.fastunit.UserSupport;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.framework.sso.SSOUtil;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.user.table.AUser;

public class DeleteUserAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 同步删除关联数据
		List<Table> tables = ac.getTables();
		Table table = tables.get(0);
		List<TableRow> rows = table.getRows();
		for (int i = 0; i < rows.size(); i++) {
			TableRow tr = rows.get(i);
			if (tr.isDeleteRow()) {
				String userId = tr.getOldValue(AUser.USER_ID);
				UserSupport.deleteUser(db, userId);
				// 单点登录-用户同步（没有单点登录时此部分可删除）
				String result = SSOUtil.deleteUser(userId);
				if (result != null) {
					ActionResult ar = ac.getActionResult();
					ar.setSuccessful(false);
					ar.addErrorMessage(result);
					return;
				}
			}
		}
		// delete
		super.doAction(db, ac);
	}

}
