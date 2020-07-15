package com.am.frame.state.distAction;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.StateFlowManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 4 已经分配 等待接受分配确认
 * 下一状态值：5,6
 * 处理过程：
 * 0、	记录是否存在，存在->1,不存在->3
 * 1、	判断是否已经超时，超时->2，未超时->3；
 * 2、	设置DistState=7
 * 3、	不操作，返回失败
 */
public class AlreadyAssignedDistStateAction implements IStateFlow {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			DB db=DBFactory.getDB();
			
			StateFlowManager.getInstance().updateStateData(db, ofp);
			
			//派单数据主键
			String distID=ofp.keyValue;
			
			//订单ID
			String orderId=ofp.orderId;
			
			//获取会员订单
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			//根据流程状态ID获取商品信息
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			
			//检查记录是否存在
			if(checkRecodExists(distID,db)){
				//如果存在，判断是否超时
				if(checkTimeOut(distID,db)){
					
					Map<String,String> otherParam=new HashMap<String,String>();
					
					//如果超时，设置派单状态为7
					String res=StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(),"7",orderId,otherParam);
					
					result=new JSONObject(res);
				}
				//如果没有超时，则不操作
				result.put("CODE",0);
				result.put("MSG","派单记录不存在");
				result.put("SUCCESS",false);
			}else{
				//如果不存在，不操作，返回失败
				result.put("CODE",0);
				result.put("MSG","派单记录不存在");
				result.put("SUCCESS",false);
			}
			
			
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

	
	/**
	 * 检查是否超时，超时返回true，否则返回false
	 * @param distID 派单状态
	 * @return 超时返回true，否则返回false
	 * @throws JDBCException 
	 */
	private boolean checkTimeOut(String distID,DB db) throws JDBCException {
		boolean result=false;
		
		//获取系统设置超时时间
		String sysTimeOut=Var.get("am_dispatcherTimeOut");
		
		//2,检查订单是否超时
		String checkTimeOutSQL="SELECT CASE WHEN disttime+'"+sysTimeOut+
				" min' < now() THEN 'TRUE' ELSE 'FALSE' END AS timeOut  "
				+ " FROM mall_commoditydistribution  WHERE id=? ORDER BY disttime ";
		
		MapList map=db.query(checkTimeOutSQL,distID,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			String timeOut=map.getRow(0).get("timeout");
			if("TRUE".equalsIgnoreCase(timeOut)){
				result=true;
			}
		}
		
		return result;
	}


	/**
	 * 检查记录是否存在，存在返回true，不存在，返回false
	 * @param distID 派单ID
	 * @return 存在返回true，不存在，返回false
	 * @throws JDBCException 
	 */
	private boolean checkRecodExists(String distID,DB db) throws JDBCException {

		boolean result=false;
		
		String checkExistSQL="SELECT * FROM  mall_CommodityDistribution WHERE id=? ";
		
		MapList map=db.query(checkExistSQL,distID,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			result=true;
		}
		
		return result;
	}

}
