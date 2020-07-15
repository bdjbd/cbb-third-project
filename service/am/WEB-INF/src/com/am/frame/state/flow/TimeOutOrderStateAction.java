package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月28日
 * @version 
 * 说明:<br />
 * 订单超时检查Action
 */
public class TimeOutOrderStateAction extends DefaultOrderStateAction {
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			DB db=DBFactory.getDB();
			
			//检查是否超时
			if(checkTimeOutOrder(ofp,db)){
				//超时
				result=new JSONObject(super.execute(ofp));
				result.put("MSG","超时" );
			}else{
				result.put("CODE",0);
				result.put("MSG","未超时");
				result.put("SUCCESS",true);
				result.put("STATE",ofp.stateValue);
			}
			result.put("ERRCODE",0);
			
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

	
	
	/**
	 * 检查是否超时
	 * @param ofp OrderFlowParam
	 * @throws JDBCException 
	 * @return boolean 如果超时，返回TRUE，不超时，返回FALSE
	 */
	private boolean checkTimeOutOrder(OrderFlowParam ofp,DB db) throws JDBCException {
		
		boolean result=false;
		
		int timeOut=Var.getInt("am_dispatcherTimeOut",10);
		
		String checkTimeOutSQL="SELECT disttime,"
				+ "	CASE WHEN now()+'"+timeOut+" min'<now() "
				+ " THEN 'TRUE' ELSE 'FALSE' END AS timeout "
				+ " FROM mall_MemberOrder  "
				+ " WHERE id=?";
		
		MapList map=db.query(checkTimeOutSQL, ofp.orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			if("TRUE".equalsIgnoreCase(map.getRow(0).get("timeout"))){
				result=true;
			};
		}
		
		return result;
	}

}
