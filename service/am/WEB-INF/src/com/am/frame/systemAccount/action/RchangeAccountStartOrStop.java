package com.am.frame.systemAccount.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wangxi
 *@create 2016年4月11日
 *@version
 *说明：系统账户分类管理  启用停用Action
 */
public class RchangeAccountStartOrStop extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_transactions_class  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("u_status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//停用--》启用
				updateSQL="UPDATE mall_transactions_class SET u_status='1' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 启用-->停用
				updateSQL="UPDATE mall_transactions_class SET u_status='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_transactions_class.do?m=s&clear=am_bdp.mall_transactions_class.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
