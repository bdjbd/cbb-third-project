package com.am.mall.order.delivery;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.state.StateFlowManager;
import com.am.frame.state.flow.OrderDistributionStateAction;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.Ajax;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/**
 * @author ZhangZhenXing
 * @Date 2014-11-26 订单派送
 * */
public class DeliveryMallOrder extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		// 订单id
		String orderid = ac.getRequestParameter("id");
		
		
		// 设置订单下一状态
		MemberOrder memberOrder = new OrderManager().getMemberOrderById(orderid);
		Commodity commodity = CommodityManager.getInstance().getCommodityByID(memberOrder.getCommodityID());
		
		Map<String,String> otherParam=new HashMap<String,String>();
		otherParam.put(OrderDistributionStateAction.OPERATER,ac.getVisitor().getUser().getId());
		
		StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(), "4", orderid, otherParam);
		
		
	    Ajax ajax=new Ajax(ac);
	    ajax.addScript("alert('配送成功！');location.reload();");
	    ajax.send();
	}
}
