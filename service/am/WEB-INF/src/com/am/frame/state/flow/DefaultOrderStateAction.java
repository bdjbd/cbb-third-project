package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author Mike
 * @create 2014年11月28日
 * @version 
 * 说明:<br />
 * 默认订单状态流程Action
 * 默认订单状态Action是根据订单ID将流程定义中的状态字段修改成下一个流程状态
 * 
 */
public class DefaultOrderStateAction implements IStateFlow {

	protected Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		DB db=null;
		
		try {
			db=DBFactory.newDB();
			//更新流程定义的SQL
			String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
					ofp.nextStateValue+"' WHERE "+
					ofp.keyName+"='"+ofp.keyValue+"'";
			
			db.execute(updateSQL);
			
			
			result.put("CODE",1);
			result.put("ERRCODE",0);
			result.put("MSG","更新订单状态成功");
			result.put("SUCCESS",true);
			result.put("STATE",ofp.nextStateValue);
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				result.put("ERRCODE",1);
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.stateValue);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
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

	
	/**
	 * 更新数据状态到失败的流程
	 * @param ofp
	 * @return
	 */
	public String executeFailure(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try {
			//更新流程定义的SQL
			String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
					ofp.failureStateVaule+"' WHERE "+
					ofp.keyName+"='"+ofp.keyValue+"'";
			
			DB db = DBFactory.getDB();
			db.execute(updateSQL);
			
			
			result.put("CODE",1);
			result.put("ERRCODE",0);
			result.put("MSG","更新订单状态成功");
			result.put("SUCCESS",true);
			result.put("STATE",ofp.failureStateVaule);
		} catch (Exception e) {
			e.printStackTrace();
			
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE",ofp.failureStateVaule);
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return result.toString();
	}

}
