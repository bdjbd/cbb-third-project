package com.am.mall.order;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * @author Mike
 * @create 2014年12月2日
 * @version 说明:<br />
 *          申请退款申请Action
 */
public class ApplyRefundAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// Action参数 delete拒绝申请 save同意申请
		//审核说明
		String refundexplain=ac.getRequestParameter("mall_refund.form.refundexplain");
		//订单ID
		String orderId=ac.getRequestParameter("mall_refund.form.orderid");
		
		//退款金额
		String applyrefundmenoys=ac.getRequestParameter("mall_refund.form.applyrefundmenoys");
		//实际退款金额
		String realrefundmenoy=ac.getRequestParameter("mall_refund.form.realrefundmenoys");
		
		String actionParam = ac.getActionParameter();

		Table table=ac.getTable("mall_refund");
		
		String appliState = "3";
		
		//获取会员订单 //订单信息
		MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
		Map<String,String> otherParam=new HashMap<String,String>();
		
		//订单对应的商品信息
		Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
		
		if ("delete".equalsIgnoreCase(actionParam)) {
			// 拒绝
			appliState = "3";
			//修改订单状态为拒绝退货
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"910");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"91",order.getId(),otherParam);
			
			
		} else if ("save".equalsIgnoreCase(actionParam)) {
			//退款金额
			double dApplyrefundmenoys=Double.parseDouble(applyrefundmenoys);
			//实际退款金额
			double dRealrefundmenoy=Double.parseDouble(realrefundmenoy);
			
			//实际金额大于退款金额
			if(dApplyrefundmenoys<dRealrefundmenoy){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("实际退款金额不能大于退款金额 ！");
				return ;
			}
			
			table.getRows().get(0).setValue("realrefundmenoy",dRealrefundmenoy);
			
			// 同意
			appliState = "2";
			//修改订单装为同一退货
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"9");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"91",order.getId(),otherParam);
			appliState="4";
			
		} else if("confirm".equalsIgnoreCase(actionParam)){
			//退款完成
			appliState="4";
		}
		
		table.getRows().get(0).setValue("state", appliState);
		
		
		db.save(table);
	}

}
