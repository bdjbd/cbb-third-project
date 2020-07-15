package com.am.frame.systemAccount.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

/**
 * @author YueBin
 * @create 2016年5月10日
 * @version 
 * 说明:<br />
 * 启用、停用商品支付Action
 */
public class SatartOrStopComdityPayStatusAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String status=ac.getRequestParameter("status");
		String id=ac.getRequestParameter("id");
		
		if("0".equals(status)){//0 停用；1 启用
			status="1";
		}else{
			status="0";
		}
		
		String updateSQL="UPDATE mall_pay_suppor SET ps_status=? WHERE id=?";
		
		db.execute(updateSQL,
				new String[]{status,id}
				,new int[]{Type.INTEGER,Type.VARCHAR});
		
	}

	
}
