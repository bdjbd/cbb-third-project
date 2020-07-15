package com.wd.menu.action;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;
/**
 * 
 * @author zhujun 2012-05-16
 * 使用范围： terminalmenu.tree.js的deleteTerminalMenu()
 * 功能：删除终端菜单管理树节点
 */
public class DeleteTerminalMenuAction implements Action {

	private static final String SQL = "delete  from abdp_TerminalMenu where id = ?";
	private static final int[] SQL_TYPE = new int[] { Type.VARCHAR };

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		Ajax ajax = new Ajax(ac);
		String id = ac.getRequestParameter("nodeid");
		if (!Checker.isEmpty(id)) {
			DB db = DBFactory.getDB();
			db.execute(SQL, new String[] { id }, SQL_TYPE);
			ajax.addScript("window.parent.location='/app/terminalmenu.frame.do';");
			ajax.addScript("reloadTree();");
			ajax.send();
		}

		return ac;
	}
}
