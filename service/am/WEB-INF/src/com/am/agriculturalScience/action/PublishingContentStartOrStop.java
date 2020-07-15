package com.am.agriculturalScience.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月26日
 *@version
 *说明：文章管理发布和取消发布
 */
public class PublishingContentStartOrStop extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		String checkSQL="SELECT * FROM mall_publishing_content  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//草稿--》发布
				updateSQL="UPDATE mall_publishing_content SET status='1',release_time=now() WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//发布-->草稿
				updateSQL="UPDATE mall_publishing_content SET status='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_publishing_content.do?m=s&clear=am_bdp.mall_publishing_content.query");
		ac.getActionResult().setSuccessful(true);
		
	
	}
}
