package com.ambdp.cro_discountactivity.action;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author 张少飞
 *@create 2017/7/11
 *@version
 *说明：汽车公社 汽修厂优惠列表内   启用/停用Action 仅支持单选
 */
public class Cro_discountactivityStartOrStop extends DefaultAction{

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		String Id=ac.getRequestParameter("id");
		
		DB db=DBFactory.getDB();
		String checkSQL="SELECT * FROM cro_discountactivity  WHERE id='"+Id+"'";
		MapList map=db.query(checkSQL);
		
		if(!Checker.isEmpty(map)){
			String activitystate = map.getRow(0).get("activitystate");
			String updateSQL="";
			
			if("0".equals(activitystate)){//停用--》启用
				updateSQL="UPDATE cro_discountactivity SET activitystate='1' WHERE id='"+Id+"'";
			}
			
			if("1".equalsIgnoreCase(activitystate)){//启用-->停用
				updateSQL="UPDATE cro_discountactivity SET activitystate='0' WHERE id='"+Id+"'";
			}
			
			if(!Checker.isEmpty(updateSQL)){
				db.execute(updateSQL);
			}
		}
		
		ac.getActionResult().setUrl("/am_bdp/cro_discountactivity.do?m=s&clear=am_bdp.cro_discountactivity.query");
		ac.getActionResult().setSuccessful(true);
		return ac;
	}
//	public void doAction(DB db, ActionContext ac) throws Exception {
//		
//		// id
//		String id = ac.getRequestParameter("cro_discountactivity.form.id");
//		if (!Checker.isEmpty(id)) {
//			//将状态改为
//			String updateSql = "UPDATE cro_discountactivity  SET status ='1'  WHERE id='" + id + "' ";
//			db.execute(updateSql);
//		}
//	
//		}
}
		
