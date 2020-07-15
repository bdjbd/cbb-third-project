package com.am.frame.favorites.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月1日
 *@version
 *说明：我的收藏 停用/启用Action
 */
public class FavoritesStartOrStop extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM me_favorites  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("data_state");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//停用--》启用
				updateSQL="UPDATE me_favorites SET data_state='1' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 启用-->停用
				updateSQL="UPDATE me_favorites SET data_state='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/me_favorites.do?m=s&clear=am_bdp.me_favorites.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
