package com.ambdp.agricultureProject.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月19日
 *@version
 *说明：农业项目管理发布/撤销Action
 */
public class AgricultureProjectsStartOrStop extends DefaultAction{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_agriculture_projects  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("1".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE mall_agriculture_projects SET status='2' WHERE id='"+Id+"'";
			}
			
			if("2".equalsIgnoreCase(dataStatus)){//发布-->草稿
				updateSQL="UPDATE mall_agriculture_projects SET status='1' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_agriculture_projects.do?m=s&clear=am_bdp.mall_agriculture_projects.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
//	public void doAction(DB db, ActionContext ac) throws Exception {
//		
//		// id
//		String id = ac.getRequestParameter("mall_agriculture_projects.form.id");
//		if (!Checker.isEmpty(id)) {
//			//将状态改为
//			String updateSql = "UPDATE mall_agriculture_projects  SET status ='2'  WHERE id='" + id + "' ";
//			db.execute(updateSql);
//		}
//	
//		}
}
		
