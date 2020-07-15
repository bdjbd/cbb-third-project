package com.am.mall.order.stateFlow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderFlowParams;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 默认流程状态执行类<br />
 * 将数据状态更新到下一个状态，如果有多个下一个状态，则这更新到第一个状态。
 * 
 */
public class DefaultFlowStateAction implements IFlowState {

	@Override
	public String execute(OrderFlowParams param) {
		
		return stepping(param).toString();
	}
	
	
	/**
	 * 默认跳转到下一个流程
	 * @param param OrderFlowParam 参数
	 * @return 执行结果
	 */
	public JSONObject stepping(OrderFlowParams param){
		
		JSONObject result=new JSONObject();
		try {
			//查询当前流程状态信息 SQL
			String sql="SELECT * FROM mall_StateFlowStepSetup  WHERE stateflowsetupid=? AND statevalue=?";
			
			DB db=DBFactory.getDB();
			
			MapList map=db.query(sql,
					new String[]{param.currentFlowId,param.currentKeyValue},
					new int[]{Type.VARCHAR,Type.VARCHAR}
			);
			
			if(!Checker.isEmpty(map)){
				
				String nextStateValue=map.getRow(0).get("nextstatevalue");
				String[] nextStateValues=new String[]{};
				
				//检查是否有下一流程状态，如果有，直接更新，否则，不修改。
				//同时，如果当前流程状态登录下一个流程状态，则不执行。
				if(!Checker.isEmpty(nextStateValue)
						&&!"null".equalsIgnoreCase(nextStateValue)
						&&!nextStateValue.equalsIgnoreCase(param.currentKeyValue)){
					
					//将下一流程状态分隔
					nextStateValues=nextStateValue.split(",");
					//更新SQL
					String updateSQL="UPDATE "+param.tableName+" SET "+param.stateFiledName+"=? WHERE "+param.keyName+"=?";
					db.execute(updateSQL, 
							new String[]{nextStateValues[0],param.keyValue}, 
							new int[]{Type.VARCHAR,Type.VARCHAR});
					
					//构造返回值
					result.put("CODE", 1);
					result.put("MSG","");
					result.put("STATE",nextStateValues[0]);
					result.put("ORDERID",param.orderId);
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			try {
				result.put("CODE", 1);
				result.put("MSG", e.getMessage());
				result.put("STATE",param.currentKeyValue);
				result.put("ORDERID",param.orderId);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}
		return result;
	}

}
