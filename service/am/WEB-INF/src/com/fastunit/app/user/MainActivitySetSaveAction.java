package com.fastunit.app.user;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.context.ActionResult;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;

/**
 * 主活动维护表单 保存时保证，只有一条记录“是否默认为主活动”设置成是
 * 
 * @author 马锐利
 * 
 */
public class MainActivitySetSaveAction extends DefaultAction {
	private static final String SQL = "select *from ABDP_MAINACTIVITYSET where isdefaultactivity='1'";

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table ABDP_MAINACTIVITYSET = ac.getTable("ABDP_MAINACTIVITYSET");
		TableRow tr = ABDP_MAINACTIVITYSET.getRows().get(0);
		ActionResult ar = ac.getActionResult();
		String isdefaultactivity = ac
				.getRequestParameter("abdp_mainactivityset.form.isdefaultactivity");
		MapList list = db.query(SQL);
		if (tr.isInsertRow()) {
			if (list.size() >= 1 && isdefaultactivity.equals("1")) {
				ar.setSuccessful(false);
				ar.addErrorMessage("只能有一项活动被设置成主活动");
			} else {
				super.doAction(db, ac);
			}
		} else {
			String id=ac.getRequestParameter("abdp_mainactivityset.form.id");
			String SQL1=SQL+"and id!='"+id+"'";
			MapList list1=db.query(SQL1);
			if (list1.size()>=1 && isdefaultactivity.equals("1")) {
				ar.setSuccessful(false);
				ar.addErrorMessage("只能有一项活动被设置成主活动");
			} else {
				super.doAction(db, ac);
			}
		}
	}

}
