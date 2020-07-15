package com.am.mall.iwebAPI;

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
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月26日
 * @version 
 * 说明:<br />
 * 派单接口
 * 参数 orderId，acceptOrderID
 */
public class DispatcherOrderIWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String result=null;
		
		//订单ID
		String orderId=request.getParameter("orderId");
		//接单ID，即店铺ID
		String acceptOrderID=request.getParameter("acceptOrderID");
		DB db = null;
		try {
			db = DBFactory.newDB();
			
			//查询店铺对应的机构ID
			String querySQL="SELECT id,orgcode FROM mall_Store WHERE id=? ";
			
			MapList map=db.query(querySQL,acceptOrderID,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				acceptOrderID=map.getRow(0).get("orgcode");
			}
			
			/**一，获取当前流程状态**/
			//订单信息
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			
			//订单对应的商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID());
			
			//检查订单状态是否为31，只有订单状态为31的可以手工派单
			
			if("31".equals(order.getOrderState())){
				/**2,执行当前流程状态配置动作，跳转流程**/
				Map<String,String> otherParam=new HashMap<String,String>();
				otherParam.put(OrderFlowParam.ACCEPT_ID, acceptOrderID);
				
				result=StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(), "31", orderId, otherParam);
			}else{
				
				JSONObject res=new JSONObject();
				res.put("CODE",0);
				res.put("ERRCODE",1);
				res.put("MSG","订单状态已经过时");
				res.put("SUCCESS",false);
				res.put("CURRENT",order.getOrderState());
				
			}
			
			
		} catch (Exception e) {
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
		
		return result;
	}

}
