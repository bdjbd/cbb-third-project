package com.fastunit.app;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.wd.message.server.MessageOpertion;

public class MessageDelAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// TODO Auto-generated method stub
		String id=ac.getRequestParameter("id");
		MessageOpertion mo=new MessageOpertion();
		mo.deleteMessage(id);
		//super.doAction(db, ac);
	}
}
