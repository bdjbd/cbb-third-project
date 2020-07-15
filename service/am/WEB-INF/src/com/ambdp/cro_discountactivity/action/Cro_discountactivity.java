package com.ambdp.cro_discountactivity.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author 张少飞
 *@create 2017/7/11
 *@version
 *说明：汽车公社 汽修厂优惠列表 启用/停用  处理action 支持多选 
 */
public class Cro_discountactivity extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		//获取列表全部列                                                                                       
		String[] List=ac.getRequestParameters("_s_cro_discountactivity.list");
		
		//获取列表全部主键   
		String[] Ids=ac.getRequestParameters("cro_discountactivity.list.id.k");
		
		//获取自定义参数
		String actionParam = ac.getActionParameter();
		
		DB db=DBFactory.getDB();
		
		String updateSQL = "";
		 //(多选,'0'代表未选中,'1'代表选中) 
		if(!Checker.isEmpty(Ids)){
			for(int i=0;i<Ids.length;i++){
				//选中的项List[i]必定为'1'，则进行修改操作
				if("1".equals(List[i])){
					//启用优惠
					if("forward".equals(actionParam)){
						updateSQL="UPDATE cro_discountactivity SET ActivityState='1' WHERE id='"+Ids[i]+"'";
						db.execute(updateSQL);
						ac.getActionResult().setSuccessful(true);
					//优惠停用	
					}else if("backward".equals(actionParam)){
						updateSQL="UPDATE cro_discountactivity SET ActivityState='0' WHERE id='"+Ids[i]+"'";
						db.execute(updateSQL);
						ac.getActionResult().setSuccessful(true);
					}
				}
			}
		}
		return ac;
	}
}

