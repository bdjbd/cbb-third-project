package com.am.frame.state.flow;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.fastunit.MapList;
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
 * 查找所有31，手工指定组织派单
 */
public class ManualDistOrderAction extends DefaultOrderStateAction {

	/**接单ID KEY**/
	
	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		DB db=null;
		try{
			//接单ID
			String acceptId=ofp.paramList.getValueOfName(OrderFlowParam.ACCEPT_ID);
			
			db=DBFactory.newDB();
			
			//新增 派单记录
			addOrderRecode(ofp.orderId,acceptId, db);
			
			result=new JSONObject(super.execute(ofp));
			
			//更新派单时间到派单表中
			updateDistTimeByOrderId(ofp.orderId,db);
			
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
		}finally {
			if(db!=null){
				try {
					db.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		return result.toString();
	}
	
	
	
	private void updateDistTimeByOrderId(String orderId, DB db) throws JDBCException {
		String updateSQL="UPDATE mall_MemberOrder SET DistTime=now() WHERE id=?";
		db.execute(updateSQL, orderId, Type.VARCHAR);
	}



	/**
	 * 新增 派单记录
	 * @param orderId 订单ID
	 * @param distState 派单状态
	 * @param db
	 * @return 返回主键
	 * @throws JDBCException 
	 */
	public String addOrderRecode(String orderId,String acceptId,DB db) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
		String querySQL="SELECT * FROM mall_commoditydistribution WHERE orderid=? ";
		
		MapList cmdMap=db.query(querySQL, orderId, Type.VARCHAR);
		
		if(!Checker.isEmpty(cmdMap)){
			id=cmdMap.getRow(0).get("id");
			
			String updateSQL="UPDATE mall_commoditydistribution  "
					+ " SET disttime=now(),AcceptOrderID=? "
					+ " WHERE id=? ";
			db.execute(updateSQL,new String[]{
					acceptId,id
			},new int[]{
					 Type.VARCHAR,Type.VARCHAR
			});
		}else{
			
			String addSQL="INSERT INTO mall_commoditydistribution("
				+ "id, orderid, disttime,AcceptOrderID )VALUES "
				+ "(?, ?, now(),?)";
		
			db.execute(addSQL,
				new String[]{id,orderId,acceptId},
				new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
		}
		
		return id;
	}
}
