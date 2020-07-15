package com.wisdeem.wwd.common;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.framework.util.MessageUtil;
import com.fastunit.support.Action;
import com.fastunit.util.Checker;

public class AjaxAction implements Action{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String msg = MessageUtil.getMessage4Ajax(ac);
		Ajax ajax = new Ajax(ac);
		if (!Checker.isEmpty(msg)) {
		    ajax.addHtml(msg);
		}
		ajax.send();
		return ac;
	}
}
