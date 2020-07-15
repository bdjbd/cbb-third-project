package com.wd.menu.action;

import java.util.List;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/**
 * 
 * @author zhujun 2012-05-15
 * 
 * 单元：终端客户表单-terminalmenu.form
 * 功能：先保存终端菜单管理数据在保存菜单按钮参数数据
 *
 */
public class SaveTerminalMenuAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table Tablea = ac.getTable("abdp_TerminalMenu");
		TableRow tr = Tablea.getRows().get(0);
		if (tr.isInsertRow()) {
			db.save(Tablea);
			String id = tr.getValue("id");

			Table tableb = ac.getTable("TerminalMenuParam");
			List<TableRow> tls = tableb.getRows();
			for (int i = 0; i < tls.size(); i++) {
				TableRow trb = tls.get(i);
				trb.setValue("menuid", id);
			}
			db.save(tableb);
			ac.setSessionAttribute("terminalmenu.form.id", id);
		} else {
			super.doAction(db, ac);
		}
	}


}
