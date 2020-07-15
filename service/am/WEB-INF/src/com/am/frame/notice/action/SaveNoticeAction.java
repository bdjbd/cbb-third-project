package com.am.frame.notice.action;


import org.apache.log4j.Logger;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * 通知保存按钮
 * @author wz
 */
public class SaveNoticeAction extends DefaultAction {
	final Logger logger = Logger.getLogger(SaveNoticeAction.class);
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception 
	{
		
		// super.doAction(db, ac);
		Table table = ac.getTable("am_notice");
		db.save(table);
		String id = table.getRows().get(0).getValue("id");
		ac.setAttribute("am_bdp.am_notice_form.id", id);
		
		
		String contentId = table.getRows().get(0).getValue("jump_id");
		
		String url = table.getRows().get(0).getValue("notice_type");
		
		String sql  = "select * from am_notice where id='"+id+"'";
		
		MapList mapList = db.query(sql);
		
		if (!Checker.isEmpty(mapList)) 
		{
			//路由URL
			
//			String routeUrl = mapList.getRow(0).get("url");
			
			String sqls = "select * from am_advert_type  where id  = '"+url+"'";
			
			MapList lst = db.query(sqls);
			String routeUrl = "";
			if(!Checker.isEmpty(lst)){
				routeUrl = lst.getRow(0).get("route_url");
			}
			String targetUrl=routeUrl.replaceAll("\\[ID\\]", contentId);
			
//			String updateAdvertSql = "UPDATE am_notice  SET url ='" +targetUrl+"',notice_type='"+url+"'  WHERE id='" + id + "' ";
			String updateAdvertSql = "UPDATE am_notice  SET url ='" +targetUrl+"',send_status = '1'  WHERE id='" + id + "' ";
			db.execute(updateAdvertSql);
			
			ac.setSessionAttribute("am_bdp.am_notice_form.id", id);
		}
		
	}
}
