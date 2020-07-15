package com.am.frame.state.serverAction;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.DefaultServerFlowAction;
import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.am.mall.reward.RewardManager;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 *  说明：订单已服务未评价
 * 7=订单完成
 * 下一状态值：8
 * 处理过程：更新状态值
 * 0、	调用该商品相应奖励规则
 */
public class OrderCompleteStateAction implements IStateFlow{

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		try{
			
			result=new JSONObject(new DefaultServerFlowAction().execute(ofp));
			//执行奖励规则
			executeCommoditReward(ofp);
			
		}catch(Exception e){
			try{
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
				
			}catch(JSONException je){
				je.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return result.toString();
	}

	
	
	private void executeCommoditReward(OrderFlowParam param) {
		
		//获取订单ID
		String orderId=param.orderId;
		
		//执行订单完成商品奖励规则
		RewardManager.getInstance().executeRewardByOrderId(orderId);
		
	}
}
