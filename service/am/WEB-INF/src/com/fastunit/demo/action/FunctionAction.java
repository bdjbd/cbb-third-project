package com.fastunit.demo.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class FunctionAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String id = ac.getRequestParameter("action.function.id");
		String a = ac.getRequestParameter("action.function.a");

		Table table = new Table("demo", "TABLEFUNCTION");
		TableRow tr = table.addUpdateRow();
		tr.setValue("a", a);
		tr.setFunction("b", "b+2");// 设置函数
		tr.setOldValue("id", id);

		db.save(table);
	}

}
