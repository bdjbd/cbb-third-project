package com.am.mall.order.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.mall.order.OrderManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年5月28日
 * @version 
 * 说明:<br />
 * 订单接单，拒绝接单WebApi接口
 */
public class OrderDistincationRecvOrRefuseWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//订单ID
		String orderId=request.getParameter("orderId");
		//接单状态，recv:接单； refuse:拒绝接单
		String state=request.getParameter("state");
		
		DB db=null;
		
		JSONObject result=new JSONObject();
		
		try{
			
			if(!Checker.isEmpty(orderId)){
				db=DBFactory.newDB();
				
				OrderManager orerManager=new OrderManager();
				if("recv".equals(state)){
					//接单
					result=orerManager.recvOrder(db,orderId);
				}else if("refuse".equals(state)){
					//拒绝接单
					result=orerManager.refuseOrder(db,orderId);
				}
			}
			
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
		
		return result.toString();
	}

}
