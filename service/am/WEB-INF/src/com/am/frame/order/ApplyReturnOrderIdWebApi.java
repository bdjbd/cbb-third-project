package com.am.frame.order;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />
 * 订单申请退款
 */
public class ApplyReturnOrderIdWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject reuslt=new JSONObject();

		DB  db=null;
		try{
			
			//订单IDorderId
			String orderId=request.getParameter("orderId");
			//会员ID
			String membeId=request.getParameter("memberId");
			
			db=DBFactory.newDB();
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			Map<String,String> otherParam=new HashMap<String,String>();
			//执行订单完成Action  3,4 状态的订单都可以进行退单
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"311");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"3",order.getId(),otherParam);
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"411");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"4",order.getId(),otherParam);
			
			//智游宝类退款
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"32011");
			StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"320",order.getId(),otherParam);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return reuslt.toString();
	}

}
