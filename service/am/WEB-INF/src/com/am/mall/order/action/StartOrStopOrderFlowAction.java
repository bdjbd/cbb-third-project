package com.am.mall.order.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

/**
 * @author YueBin
 * @create 2016年4月29日
 * @version 
 * 说明:<br />
 * 订单流程停用、启用
 */
public class StartOrStopOrderFlowAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String id=ac.getRequestParameter("id");
		String status=ac.getRequestParameter("status");
		//status	1:停用
		//2：启用
		if("1".equals(status)){
			status="2";
		}else{
			status="1";
		}
		
		String udpateSQL="UPDATE mall_StateFlowSetup SET status=? WHERE id=? ";
		
		db.execute(udpateSQL,new String[]{status,id},new int[]{Type.INTEGER,Type.VARCHAR});
		
	}
	
}
