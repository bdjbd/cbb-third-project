package com.am.mall.order.action;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月13日
 * @version 
 * 说明:<br />
 * 理性农业订单配送说明
 */
public class OrderDistributionAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		String area_dc_member_id=ac.getRequestParameter("member_deliver_order_info.area_dc_member_id");
		String cdid=ac.getRequestParameter("member_deliver_order_info.cdid");
		
		//订单ID
		String orderId=ac.getRequestParameter("member_deliver_order_info.id");
		
		if(!Checker.isEmpty(area_dc_member_id)){
			String updateSQL="UPDATE mall_CommodityDistribution SET area_dc_member_id=? WHERE id=?";
			db.execute(updateSQL,new String[]{
					area_dc_member_id,cdid
			},new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			//获取派单ID
			Map<String,String> otherParam=new HashMap<String,String>();
			//更新订单状态到下一个默认设置状态
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"401",order.getId(), otherParam);
			
		}else{
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请选择配送人员！");
		}
		
		
		
	}
	
}
