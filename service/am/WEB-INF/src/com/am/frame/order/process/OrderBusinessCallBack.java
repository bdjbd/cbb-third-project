package com.am.frame.order.process;

import org.json.JSONObject;

import com.am.frame.pay.PayManager;
import com.am.frame.transactions.callback.IBusinessCallBack;
import com.fastunit.jdbc.DB;

/**
 * @author YueBin
 * @create 2016年6月24日
 * @version 
 * 说明:<br />
 * 订单支付完成处理类
 */
public class OrderBusinessCallBack implements IBusinessCallBack {

	@Override
	public String callBackExec(String id, String business, DB db, String type)
			throws Exception {
		
		PayManager payManager=new PayManager();
		
		JSONObject result=payManager.processPaymentComplete(db,id);
		
		return result.toString();
	}

}
