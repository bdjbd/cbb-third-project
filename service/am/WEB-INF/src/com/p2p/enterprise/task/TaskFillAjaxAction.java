package com.p2p.enterprise.task;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

public class TaskFillAjaxAction extends AjaxAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String id=ac.getRequestParameter("tempId");
		
		DB db=DBFactory.getDB();
		
		String sql="SELECT * FROM p2p_TaskTemplate WHERE id='"+id+"'";
		
		MapList map=db.query(sql);
		
		if(Checker.isEmpty(map))return ac;
		
		//enterpTaskparameId     enterpNameId
		Ajax ajax=new Ajax(ac);
		
		String name=map.getRow(0).get("name");
		
		String taskParame=map.getRow(0).get("taskparame");
		
		ajax.addScript("changeTaskValue({'NAME':'"+name+"','TASKPARAME':'"+taskParame+"'})");
		ajax.send();
		
		return ac;
	}
}
