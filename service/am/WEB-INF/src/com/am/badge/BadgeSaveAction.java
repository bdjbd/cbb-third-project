package com.am.badge;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

public class BadgeSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
//		super.doAction(db, ac);
		Table table=ac.getTable("MALL_ENTERPRISEBADGE");
		
		db.save(table);
		
		String id=table.getRows().get(0).getValue("id");               
		String fileName=Utils.getFastUnitFilePath("MALL_ENTERPRISEBADGE", "ep_badgeiconpaths", id);
		
		if(!Checker.isEmpty(fileName)&&fileName.length()>1){
			fileName=fileName.substring(0, fileName.length()-1);
			
			String sql="UPDATE mall_enterprisebadge  SET BadgeIconPath='"
			+fileName+"'  WHERE id='"+id+"' ";
			
			db.execute(sql);
		}
	}
}
