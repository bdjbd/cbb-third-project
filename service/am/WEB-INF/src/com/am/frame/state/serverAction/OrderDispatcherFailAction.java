package com.am.frame.state.serverAction;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 31 说明：需要手工分配订单界面可操作该条记录<br />
 * 下一状态值：null <br />
 * 处理过程：<br />
 * 0、	设置DistState=3,传参 接单ID
 */
public class OrderDispatcherFailAction  implements IStateFlow{

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			DB db=DBFactory.getDB();
			
			String updateSQL="UPDATE mall_MemberOrder SET OrderState='31' WHERE id=?";
			db.execute(updateSQL, ofp.orderId,Type.VARCHAR);
			
			result.put("CODE",1);
			result.put("MSG","需要手工分配订单界面可操作该条记录");
			result.put("SUCCESS",true);
			result.put("STATE",ofp.stateValue);
			
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

}
