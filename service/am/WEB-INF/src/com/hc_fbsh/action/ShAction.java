package com.hc_fbsh.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.hc_fbsh.util.ShUpdate;

/**
 * @author 郭仁杰
 *  说明：单个审核action
 */
public class ShAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {

		String id = ac.getRequestParameter("hc_fbsh.form.id");
		String actionParam = ac.getActionParameter();
		DB db = DBFactory.getDB();
		ShUpdate.update(ac, db, actionParam, id);
		return ac;
	}

	
}
