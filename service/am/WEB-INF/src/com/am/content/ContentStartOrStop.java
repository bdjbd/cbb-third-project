package com.am.content;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016.06.02
 *@version
 *说明：内容 发布/撤销Action
 */
public class ContentStartOrStop extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		final String am_content_menucode="content.ss.menucode";
		String Id=ac.getRequestParameter("id");
		String menuCode = (String)ac.getSessionAttribute(am_content_menucode);
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM am_content  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("datastate");
			String updateSQL="";
			
			if("1".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE am_content SET dataState='2',createdate=now() WHERE id='"+Id+"'";
			}
			
			if("2".equalsIgnoreCase(dataStatus)){//1 发布-->草稿
				updateSQL="UPDATE am_content SET dataState='1' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/am_content_assembly.do?menucode="+menuCode);
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
