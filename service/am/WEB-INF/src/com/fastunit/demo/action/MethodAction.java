package com.fastunit.demo.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class MethodAction extends DefaultAction {

	public void a(DB db, ActionContext ac) {
		// TODO do something...
		ac.getActionResult().addSuccessMessage("a");
	}

	public void b(DB db, ActionContext ac) {
		// TODO do something...
		ac.getActionResult().addSuccessMessage("b");
	}

}
