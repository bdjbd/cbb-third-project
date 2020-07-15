package com.wisdeem.wwd.management.member;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class GroupSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		Table table = ac.getTable("ws_member_group");
		String orgid = ac.getVisitor().getUser().getOrgId();
		String querySQL = "select * from WS_PUBLIC_ACCOUNTS where orgid='"
				+ orgid + "'";
		MapList listr = db.query(querySQL);
		String public_id = listr.getRow(0).get("public_id");
		String mb_g_id = ac.getRequestParameter("ws_member_group.form.mb_g_id");
		String group_name = ac
				.getRequestParameter("ws_member_group.form.group_name");
		String explain = ac.getRequestParameter("ws_member_group.form.explain");
		String selSQL = "";
		if (mb_g_id == "" || mb_g_id == null) {// 新增

			selSQL = "select group_name from ws_member_group where group_name='"
					+ group_name + "' and orgid='" + orgid + "'";
		} else {
			selSQL = "select group_name from ws_member_group where mb_g_id!="
					+ mb_g_id + " and group_name='" + group_name
					+ "' and orgid='" + orgid + "'";
		}
		MapList list = db.query(selSQL);
		if (list.size() > 0) {
			String groupname = list.getRow(0).get("group_name");
			if (groupname.equals(group_name)) {
				ac.getActionResult().addErrorMessage("分组名称已存在");
				ac.getActionResult().setSuccessful(false);
				return;
			}
		} else {
			TableRow tableRow = table.getRows().get(0);
			tableRow.setValue("public_id", public_id);
			tableRow.setValue("group_name", group_name.trim());
			tableRow.setValue("explain", explain.trim());
			tableRow.setValue("orgid", orgid);
			db.save(table);
			ac.getActionResult().addSuccessMessage("保存成功");
		}
	}
}
