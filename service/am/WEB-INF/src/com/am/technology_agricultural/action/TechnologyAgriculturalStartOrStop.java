package com.am.technology_agricultural.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016.06.14
 *@version
 *说明：科技农技协发布和取消发布
 */
public class TechnologyAgriculturalStartOrStop extends DefaultAction{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_agricultural_technology  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("1".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE mall_agricultural_technology SET status='2',release_time=now() WHERE id='"+Id+"'";
			}
			
			if("2".equalsIgnoreCase(dataStatus)){//发布-->草稿
				updateSQL="UPDATE mall_agricultural_technology SET status='1' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_agricultural_technology.do?m=s&clear=am_bdp.mall_agricultural_technology.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
