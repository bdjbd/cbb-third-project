package com.am.frame.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.frame.pay.PayManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年5月23日
 * @version 
 * 说明:<br />
 * 处理支付完成，订单状态信息
 * pay_id 支付id
 */
public class ProcessOrderPaymentWebAPI implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result=new JSONObject();
		//支付id
		String payId=request.getParameter("PAY_ID");
		
		PayManager payManager=new PayManager();
		
		DB db=null;
		
		try {
			db=DBFactory.newDB();
			result=payManager.processPaymentComplete(db,payId);
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
		
		return result.toString();
	}

}
