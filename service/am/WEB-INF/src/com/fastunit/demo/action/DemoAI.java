package com.fastunit.demo.action;

import com.fastunit.Invocation;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.support.ActionInterceptor;

public class DemoAI implements ActionInterceptor {

	@Override
	public void intercept(ActionContext ac, Invocation invocation)
			throws Exception {
		ActionResult result = ac.getActionResult();
		result.addNoticeMessage("before: do something ...");
		invocation.invoke();
		result.addNoticeMessage("after: do something ...");
	}

}
