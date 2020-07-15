package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;

/**
 * @author Mike
 * @create 2014年12月2日
 * @version 
 * 说明:<br />
 * 订单取消
 * 订单状态 3-9
 * 订单状态4-9
 * 订单取消状态 30，31，32 -9
 * 
 */
public class OrderCancelStateAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			//获取当前订单状态
			String currentStateValue=ofp.stateValue;
			
			//检查订单状态是否为3，30，31，32，4的状态，如果是，修改状态，如果不是则不动。
			if("3".equals(currentStateValue)||
					"30".equals(currentStateValue)||
					"31".equals(currentStateValue)||
					"32".equals(currentStateValue)||
					"4".equals(currentStateValue)){
				result=new JSONObject(super.execute(ofp));
			}else{
				throw new Exception("只有订单状态为3,30,31,32,4的订单才可以取消订单，目前订单状态为 "+ofp.stateValue);
			}
			
		}catch(Exception e){
			
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return result.toString();
	}

}
