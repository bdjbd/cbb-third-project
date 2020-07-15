package com.cdms.org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

public class SaveChildAdminRole extends DefaultAction {
	Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String[] checks = ac.getRequestParameters("_treecheck");
		if (!Checker.isEmpty(checks)) {
			//如果选中不为空，则清空下级管理员可用角色表，重新添加新数据
			String sql = "delete from ABDP_CHILDADMINROLESET";
			db.execute(sql);
			//添加根节点（根节点页面不可选，直接添加即可）
			Table table = ac.getTable("ABDP_CHILDADMINROLESET");
			TableRow tr1 = table.addInsertRow();
			tr1.setValue("roleid", "role");
			for (int i = 0; i < checks.length; i++) {
				TableRow tr = table.addInsertRow();
				tr.setValue("roleid", checks[i]);
			}
			db.save(table);
		}
	}
}
