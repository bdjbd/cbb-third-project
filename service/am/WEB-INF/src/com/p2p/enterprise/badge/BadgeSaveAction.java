package com.p2p.enterprise.badge;

import java.sql.Connection;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

public class BadgeSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
//		super.doAction(db, ac);
		Table table=ac.getTable("P2P_ENTERPRISEBADGE");
		db.save(table);
		String id=table.getRows().get(0).getValue("id");               
		String fileName=Utils.getFastUnitFilePath("P2P_ENTERPRISEBADGE", "ep_badgeiconpaths", id);
		fileName=fileName.substring(0, fileName.length()-1);
		String sql="UPDATE p2p_enterprisebadge  SET BadgeIconPath='"
		+fileName+"'  WHERE id='"+id+"' ";
		System.out.println(fileName);
		Connection conn=db.getConnection();
		conn.createStatement().execute(sql);
		conn.commit();
	}
}
