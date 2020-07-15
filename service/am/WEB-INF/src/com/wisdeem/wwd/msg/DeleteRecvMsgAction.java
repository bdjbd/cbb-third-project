package com.wisdeem.wwd.msg;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;

/***
 * 删除ws_recv_msg消息Action
 * 
 * @author Administrator
 * 
 */
public class DeleteRecvMsgAction extends DefaultAction {
	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String msgId=ac.getRequestParameter("msgid");
		String sql="DELETE FROM ws_recv_msg WHERE msg_id='"+msgId+"' ";
		DBFactory.getDB().execute(sql);
		return ac;
	}
}
