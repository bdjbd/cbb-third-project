package com.am.frame.state.flow;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author Mike
 * @create 2014年11月29日
 * @version 
 * 说明:<br />
 * 接受订单分配
 * 1，修改订单状态
 * 2，更新订单信息表中的订单接单ID
 */
public class AcceptOrderStateAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		String res=super.execute(ofp);
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			//更新接单ID
			String acceptId=ofp.paramList.getValueOfName(OrderFlowParam.ACCEPT_ID);
			
			String uddateSQL="UPDATE mall_MemberOrder SET AcceptOrderId=?,acceptTime=now() WHERE id=? ";
			
			db.execute(uddateSQL, new String[]{acceptId,ofp.orderId},new int[]{Type.VARCHAR,Type.VARCHAR});
			
			result=new JSONObject(res);
			
		}catch(Exception e){
			e.printStackTrace();
			
			try {
				result.put("CODE",0);
				result.put("ERRCODE",1);
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
}
