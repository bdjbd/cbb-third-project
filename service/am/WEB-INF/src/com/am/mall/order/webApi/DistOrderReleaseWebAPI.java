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
 * @create 2016年5月30日
 * @version 
 * 说明:<br />
 * 订单，放货接口
 */
public class DistOrderReleaseWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//订单id
		String orderId=request.getParameter("orderId");
		//放货密码
		String pasword=request.getParameter("pwd");
		
		DB db=null;
		JSONObject result=new JSONObject();
		try{
			
			if(!Checker.isEmpty(orderId)){
				db=DBFactory.newDB();
				
				//1,验证订单放货密码是否正确
				//2,如果密码正确，修改订单状态
				OrderManager orerManager=new OrderManager();
				
				orerManager.distOrderRelease(db, orderId);
				
				result.put("CODE", 0);
				result.put("MSG","返货成功！");
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
