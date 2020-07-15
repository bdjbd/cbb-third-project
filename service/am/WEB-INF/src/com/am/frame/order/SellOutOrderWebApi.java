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
 * @create 2016年5月27日
 * @version 
 * 说明:<br />
 * 订单售出
 */
public class SellOutOrderWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//卖出订单ID
		String orderId=request.getParameter("orderId");
		
		DB db=null;
		JSONObject result=new JSONObject();
		
		try{
			db=DBFactory.newDB();
			
			MemberOrder order=new OrderManager().getMemberOrderById(orderId,db);
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			
			Map<String,String> otherParam=new HashMap<String, String>();
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"308");
			String flResult=StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"3",orderId, otherParam);
		
			result=new JSONObject(flResult);
			
		}catch(Exception e){
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		return result.toString();
	}

}
