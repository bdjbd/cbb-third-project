package com.am.frame.order;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />
 * 根据订单信息更新订单状态
 */
public class UpdateOrderStateByOrderIdWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
//		 memberId:memberId,
//         orderInfo:orderInfo
		//会员ID
		String memberId=request.getParameter("memberId");
		//订单信息
		String orderInfo=request.getParameter("orderInfo");
		DB db=null;
		try {
			if(!Checker.isEmpty(orderInfo)){
				db=DBFactory.newDB();
				
				//订单信息
				JSONObject orderInfoJS=new JSONObject(orderInfo);
				String orderId=orderInfoJS.getString("ORDERID");
				String nexState=orderInfoJS.getString("NEXT_STATE");
				
				//获取会员订单
				MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
				//根据流程状态ID获取商品信息
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
				//获取派单ID
				Map<String,String> otherParam=new HashMap<String,String>();
				//更新订单状态到下一个默认设置状态
				StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),nexState,order.getId(), otherParam);
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
		
		return null;
	}

}
