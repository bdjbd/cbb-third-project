package com.fastunit.framework.util;

import com.fastunit.Ajax;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.support.Action;
import com.fastunit.user.table.AUserStamp;

/**
 * AJAX-显示图章列表
 * 
 * @author jim
 */
public class StampAction implements Action {

	private static final String SQL = "select " + AUserStamp.PATH + " from "
			+ AUserStamp.TABLENAME + " where " + AUserStamp.USER_ID + "=? order by "
			+ AUserStamp.ORDER + "," + AUserStamp.PATH;

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		StringBuffer html = new StringBuffer();
		String userId = ac.getVisitor().getUser().getId();
		DB db = DBFactory.getDB();
		MapList list = db.query(SQL, userId, Type.VARCHAR);
		for (int i = 0; i < list.size(); i++) {
			html.append("<img class=\"E92I\" src=\"").append(list.getRow(i).get(0))
					.append("\"/>");
		}
		Ajax ajax = new Ajax(ac);
		ajax.addHtml(html.toString());
		ajax.send();
		return ac;
	}
}
