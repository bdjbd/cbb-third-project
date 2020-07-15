package com.am.frame.servicecommodity.action;


import com.am.frame.webapi.member.service.SystemAccountServer;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author wz
 *@create 2016年4月28日
 *@version
 *说明：服务品保存
 */
public class MallServiceCommodityAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM mall_service_commodity  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String dataStatus=map.getRow(0).get("status");
			String updateSQL="";
			
			if("0".equals(dataStatus)){//停用--》启用
				updateSQL="UPDATE mall_service_commodity SET status='1' WHERE id='"+Id+"'";
				
				//启用/停用帐号
				SystemAccountServer saService=new SystemAccountServer();
				saService.startSystemAccount(db, Id,null);
			}
			
			if("1".equalsIgnoreCase(dataStatus)){//1 启用-->停用
				updateSQL="UPDATE mall_service_commodity SET status='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/mall_service_commodity.do?m=s&clear=am_bdp.mall_service_commodity.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
}
