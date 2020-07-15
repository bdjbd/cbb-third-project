package com.fastunit.demo.i18n;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Action;

public class ChangeLocaleAction implements Action {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String locale = ac.getRequestParameter("i18n.locale");
		ac.setSessionAttribute("demo.i18n.locale", locale);
		ac.getVisitor().setLocaleString(locale);
		return ac;
	}

}
