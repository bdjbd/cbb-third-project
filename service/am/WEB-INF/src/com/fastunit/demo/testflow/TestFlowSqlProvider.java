package com.fastunit.demo.testflow;

import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class TestFlowSqlProvider implements SqlProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String sql1 = "select * from T_FLOWTEST where  workflowid ='$RS{flowinstid,t_flowtest.form.flow.workflowid,-1}'";
		String sql2 = "select * from T_FLOWTEST where  id ='$RS{t_flowtest.form.flow.id,t_flowtest.form.flow.id,-1}'";
		String flowinstid = ac.getRequestParameter("flowinstid");
		String id = ac.getRequestParameter("t_flowtest.form.flow.id");
		if (!Checker.isEmpty(flowinstid)) {
			return sql1;
		}
		if (!Checker.isEmpty(id)) {

			return sql2;
		}

		return "";
	}

}
