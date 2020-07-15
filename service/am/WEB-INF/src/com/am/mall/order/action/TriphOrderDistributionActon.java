package com.am.mall.order.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.Action;

/**
 * @author YueBin
 * @create 2016年6月22日
 * @version 
 * 说明:<br />
 * 呼旅 订单配送界面
 */
public class TriphOrderDistributionActon  implements Action
{
	final Logger logger = LoggerFactory.getLogger(TriphOrderDistributionActon.class);

	@Override
	public ActionContext execute(ActionContext ac)throws Exception {
		
		DB db=null;
		
		try{
			
			db=DBFactory.newDB();
			//物流公司
			String logisticsName=ac.getRequestParameter("member_deliver_order_info.logistics_name");
			
			String logisticsOrderCode=ac.getRequestParameter("member_deliver_order_info.logistics_order_code");
			
			//订单ID
			String orderId=ac.getRequestParameter("member_deliver_order_info.id");
			
			String updateSQL="UPDATE mall_memberorder SET logistics_name='" + logisticsName + "' ,logistics_order_code='" + logisticsOrderCode + "' WHERE id='" + orderId + "' ";
			
			logger.debug("TriphOrderDistributionActon.doAction().updateSQL=" + updateSQL);
			
			int sqlNumber=db.execute(updateSQL);
			
			logger.debug("TriphOrderDistributionActon.doAction().sqlNumber=" + sqlNumber);
			
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
