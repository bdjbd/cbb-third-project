package com.am.frame.state;

import org.json.JSONException;
import org.json.JSONObject;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;


/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 默认服务订单状态Action
 */
public class DefaultServerFlowAction implements IStateFlow {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		try{
			
			DB db=DBFactory.getDB();
			
			result=StateFlowManager.getInstance().updateStateData(db, ofp);
			
		}catch(Exception e){
			
			try {
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return result.toString();
	}
	
}
