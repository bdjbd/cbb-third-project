package com.am.logisticsinfo.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author yangdong
 *@create 2016年5月04日
 *@version 物流信息发布撤销Action
 */
public class LogisticsInfoStartOrStopAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=null;
		
		try{
			
			db=DBFactory.newDB();
			
			String checkSQL="SELECT * FROM mall_logistics_info  WHERE id='"+Id+"'";
			MapList map=db.query(checkSQL);
			
			if(!Checker.isEmpty(map)){
				String dataStatus=map.getRow(0).get("status");
				String updateSQL="";
				
				if("1".equals(dataStatus)){//发布
					updateSQL="UPDATE mall_logistics_info SET status=2,create_time=now() WHERE id='"+Id+"'";
				}
				
				if("2".equalsIgnoreCase(dataStatus)){//撤销
					updateSQL="UPDATE mall_logistics_info SET status=1,create_time=null WHERE id='"+Id+"'";
				}
				
				if(!Checker.isEmpty(updateSQL)){
					db.execute(updateSQL);
				}
			}
			
			ac.getActionResult().setUrl("/am_bdp/mall_logistics_info.do?m=s&clear=am_bdp.mall_logistics_info.query");
			ac.getActionResult().setSuccessful(true);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(db!=null){
				db.close();
			}
		}
		
		return ac;
	}
}
