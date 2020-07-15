package com.fastunit.app.user;

import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * abdp_mainactivityset.list 主界面列表中
 * 得到被选中的行的id，为修改按钮拼url
 * @author 马锐利
 *
 */
public class MainCheckAction extends DefaultAction{
	@Override
	public void doAction(DB db,ActionContext ac)throws Exception{
		ActionResult ar = ac.getActionResult();
		String[] selects = ac.getRequestParameters("_s_abdp_mainactivityset.list");
		String[] ids = ac.getRequestParameters("abdp_mainactivityset.list.id.k");
		for (int i = 0; i <ids.length; i++) {
			if ("1".equals(selects[i])) {
				System.out.println("ids[" + i + "]=" +ids[i]);
				String id = ids[i];
				ar.setUrl("/app/abdp_mainactivityset.form.do?m=e&abdp_mainactivityset.form.id="+id);
				}
		}
	}

}
