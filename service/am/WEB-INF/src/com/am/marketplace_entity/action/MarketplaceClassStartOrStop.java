package com.am.marketplace_entity.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年5月5日
 *@version
 *说明：农村大市场分类 停用/启用Action
 */
public class MarketplaceClassStartOrStop extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_marketplace_class  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//停用--》启用
				updateSQL="UPDATE mall_marketplace_class SET status='1' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 启用-->停用
				updateSQL="UPDATE mall_marketplace_class SET status='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_marketplace_class.do?m=s&clear=am_bdp.mall_marketplace_class.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
