package com.wisdeem.wwd.opaccount;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

public class StartOperatorsAccount extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		// 获取当前登陆人id
		String username = ac.getVisitor().getUser().getId();

		// 获得要修改的数据的编号
		String isUpdate[] = ac.getRequestParameters("_s_ws_operators_account.list");
		String ids[] = ac.getRequestParameters("ws_operators_account.list.public_id");
		String data_status[] = ac.getRequestParameters("ws_operators_account.list.data_status");

		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < isUpdate.length; i++) {
			if ("1".equals(isUpdate[i])) {
				if ("2".equals(data_status[i])) {
					ac.getActionResult().addErrorMessage("已启用的数据不能再启用，请重新选择！");
					return;
				}
				list.add(new String[] { ids[i] });
			}
		}
		// 批量更新
		String sql = "update WS_OPERATORS_ACCOUNT set data_status=2 where public_id = ? ";
		db.executeBatch(sql, list, new int[] { Type.INTEGER });
		ac.getActionResult().addSuccessMessage("启用成功");
	}

}
