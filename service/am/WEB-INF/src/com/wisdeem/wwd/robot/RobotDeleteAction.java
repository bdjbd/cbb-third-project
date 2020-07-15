package com.wisdeem.wwd.robot;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class RobotDeleteAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		String[] isDelete = ac
				.getRequestParameters("_s_ws_auto_replay_rule.list");
		String[] ids = ac
				.getRequestParameters("ws_auto_replay_rule.list.rule_id.k");

		String values = "";
		for (int i = 0; i < isDelete.length; i++) {
			if ("1".equals(isDelete[i])) {// 1为选中
			String sql = "delete from  WS_AUTO_REPLAY_RULE where rule_id in ("
					+ ids[i] + ")";
			String sql1 = "delete from  WS_SEND_MSG where rule_id in (" + ids[i]
					+ ")";
			db.execute(sql);
			db.execute(sql1);

			}
		}
	}
}
