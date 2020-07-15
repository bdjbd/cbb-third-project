package com.p2p.enterprise.task;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;
import com.p2p.task.UserTaskManage;

public class StopOrStartTaskAjaxAction extends AjaxAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String id=ac.getRequestParameter("id");
		String type=ac.getRequestParameter("type");
		String sql="SELECT now() ";
		String orgid=ac.getVisitor().getUser().getOrgId();
		DB db=DBFactory.getDB();
		if("stop".equalsIgnoreCase(type)){
			//通用任务，删除
			sql="DELETE FROM p2p_usertask WHERE EnterpriseTaskID='"+id+"'";
			
			db.execute(sql);
			//修改任务状态为停用
			sql="UPDATE p2p_EnterpriseTask  SET ETaskState='0' WHERE id='"+id+"'";
			db.execute(sql);
		}else if("start".equalsIgnoreCase(type)){
			//启用任务
			sql="UPDATE p2p_EnterpriseTask  SET ETaskState='1' WHERE id='"+id+"'";
			db.execute(sql);
			UserTaskManage.initTask(orgid, id);
		}
		Ajax ajax=new Ajax(ac);
		ajax.addScript("alert('已"+("stop".equalsIgnoreCase(type)?"停用":"启用")+"');location.reload();");
		ajax.send();
		return ac;
	}
}
