package com.am.frame.task;

import org.json.JSONObject;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 说明:<br />
 */
public class TaskFillAjaxAction extends AjaxAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String id=ac.getRequestParameter("tempId");
		
		DB db=DBFactory.getDB();
		
		String sql="SELECT * FROM am_TaskTemplate WHERE id='"+id+"'";
		
		MapList map=db.query(sql);
		
		if(Checker.isEmpty(map))return ac;
		
		//enterpTaskparameId     enterpNameId
		Ajax ajax=new Ajax(ac);
		
		String name=map.getRow(0).get("name");
		
		String taskParame=map.getRow(0).get("taskparame");
		
		JSONObject values=new JSONObject();
		
		values.put("NAME", name);
		values.put("TASKPARAME", taskParame);
		
		ajax.addScript("changeTaskValue("+values.toString()+")");
		ajax.send();
		
		return ac;
	}
}
