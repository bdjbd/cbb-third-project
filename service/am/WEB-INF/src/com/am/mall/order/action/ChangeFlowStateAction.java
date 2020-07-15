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
import com.fastunit.support.Action;

/**
 * @author YueBin
 * @create 2016年7月6日
 * @version 
 * 说明:<br />
 * 修改流程订单的状态Action
 * 
 */
public class ChangeFlowStateAction implements Action {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		
		DB db=null;
		
		try{
			//订单ID
			String orderId=ac.getRequestParameter("orderId");
			
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			//获取派单ID
			Map<String,String> otherParam=new HashMap<String,String>();
			//更新订单状态到下一个默认设置状态
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"4",order.getId(),otherParam,db);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
		
		return ac;
	}

}
