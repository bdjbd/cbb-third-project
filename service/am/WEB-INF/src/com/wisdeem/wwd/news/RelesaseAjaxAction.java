package com.wisdeem.wwd.news;

import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.AjaxAction;

public class RelesaseAjaxAction extends AjaxAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String nid=ac.getRequestParameter("nid");
		
		String sql="UPDATE newsdetail  SET datastatus=2,release_date=now() WHERE nid="+nid;
		
		DBFactory.getDB().execute(sql);
		
		Ajax ajax=new Ajax(ac);
		ajax.addScript("location.reload();");
		ajax.send();
		
		return ac;
	}
	
}
