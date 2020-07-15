package com.am.mall.order.distState;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.mall.beans.order.StateFlowStepSetup;
import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.order.ServerFlowManager;

/**
 * @author YueBin
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 默认状态流程分配参数类
 */
public class DefaultDistStateAction implements IFlowState {

	@Override
	public String execute(OrderFlowParams ofp) {
		
		JSONObject result=new JSONObject();
		
		try {
			//获取当前流程状态参数
			ServerFlowManager flowManager=ServerFlowManager.getInstance();
			
			StateFlowStepSetup currentFlowStepSetup=flowManager.getStateFlowStepSetup(
					ofp.currentFlowStateId, ofp.currentFlowId);
			
			//获取下一个派单流程状态的状态值
			String nextState=currentFlowStepSetup.getNextStateValue();
			
			
			//设置返回信息
			result.put("CODE",1);
			result.put("MSG","设置流程状态到"+nextState);
			result.put("STATE",nextState);
			
		} catch (Exception e) {
			try {
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}
		
		return result.toString();
	}

}
