package com.am.frame.order.use.action;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

/**
 * @author YueBin
 * @create 2016年6月26日
 * @version 
 * 说明:<br />
 * 订单使用Action
 */
public class OrderUserAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		super.doAction(db, ac);
		//订单ID
		String orderId=ac.getRequestParameter("mall_memberorder_use.form.id");
		
		MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
		//订单对应的商品信息
		Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			
		Map<String,String> otherParam=new HashMap<String,String>();
		
		//执行订单完成Action
		otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"3208");
		
		/**2,执行当前流程状态配置动作，跳转流程**/
		StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"320",order.getId(), otherParam);
		
		//保存备注信息
		String remark=ac.getRequestParameter("mall_memberorder_use.form.remark");
		
		String updateSQL="UPDATE mall_MemberOrder SET remark=? WHERE id=? ";
		db.execute(updateSQL,new String[]{
				remark,orderId
		},new int[]{
				Type.VARCHAR,Type.VARCHAR
		});
		
	}
	
}
