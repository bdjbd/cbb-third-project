package com.am.frame.state.flow;

import org.json.JSONObject;

import com.am.frame.order.service.OrderService;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author YueBin
 * @create 2016年5月25日
 * @version 
 * 说明:<br />
 * 订单卖出
 */
public class SellOutOrderStateAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		DB db=null;
		JSONObject result=new JSONObject();
		
		try{
			db=DBFactory.newDB();
			//处理订单业务流程
			OrderService orderService=new OrderService();
			result=orderService.sellOutOrder(db, ofp.orderId);
			//修改订单状态
			super.execute(ofp);
			
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
