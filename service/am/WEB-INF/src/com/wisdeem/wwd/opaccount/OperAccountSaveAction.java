package com.wisdeem.wwd.opaccount;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

public class OperAccountSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String orgid = ac.getVisitor().getUser().getOrgId();
		Table table = ac.getTable("WS_OPERATORS_ACCOUNT");
		TableRow tableRow = table.getRows().get(0);

		String is_valid = ac.getRequestParameter("ws_operators_account.form.is_valid");

		if ("是".equalsIgnoreCase(is_valid)) {
			tableRow.setValue("is_valid", 1);
		} else {
			tableRow.setValue("is_valid", 3);
		}

		db.save(table);
		ac.getActionResult().addSuccessMessage("保存成功");
	}
}
