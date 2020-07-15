package com.am.frame.state.flow;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.state.OrderFlowParam;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.map.GpsPointDistance;

/**
 * @author Mike
 * @create 2014年11月28日
 * @version 
 * 说明:<br />
 * 查找所有订单状态为3/32，依据组织派单规则派单；
 * 派单成功修改状态到下一个状态值
 */
public class OrderDistributionAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			//已经派单规则派单
			JSONObject distResult=null;
			try{
				distResult=orgDistOrderRule(ofp,db);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(distResult!=null&&distResult.getBoolean("SUCCESS")){
				//派单成功
				
				//接单ID
				String acceptID=distResult.getString("STOREID");
				
				//向派单表中插入派单信息
				insertDistRecv(ofp,acceptID,db);
				
				//更新订单状态到下一个流程状态
				result=new JSONObject(super.execute(ofp));
				
				//更新订单中的派单时间
				updateDistTimeByOrderId(ofp.orderId, db);
				
			}else{
				//派单失败
				result=distResult;
				//更新订单状态为失败状态值
				result=new JSONObject(super.executeFailure(ofp));
			}
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
			
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
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
	 * 向派单表中插入派单数据
	 * @param ofp
	 * @param db
	 * @throws JDBCException 
	 */
	private String insertDistRecv(OrderFlowParam ofp,String acceptId, DB db) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
		String addSQL="INSERT INTO mall_commoditydistribution("
				+ "id, orderid, disttime,AcceptOrderID )VALUES "
				+ "(?, ?, now(),?)";
		
		db.execute(addSQL,
				new String[]{id,ofp.orderId,acceptId},
				new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
		return id;
	}


	/**
	 * 组织派单规则
	 * 1，获得从未接单的所有门店 ->2
	 * 2，获得从未接该订单的所有门店（按照分配时间排序）->3
	 * 3，拼接1+2 的结果集->4
	 * 4，依据订单坐标，寻找10公里（Var中定义）范围内最近的门店，找到->5，未找到->6；
	 * 5，给该门店派单并通知接单ID的设备（手机短信、推送通知）；Update DistState=4   updateAccepe；
	 * 6，未找到门店则 Update OrderState=31；
	 * @param ofp  OrderFlowParam
	 * @return
	 * @throws JDBCException 
	 */
	private JSONObject orgDistOrderRule(OrderFlowParam ofp,DB db) throws Exception {
		
		JSONObject result=new JSONObject();
		
		//从未接到的所有门店
		MapList noRecvOrderMap=findNoRecvOrder(db);
		//获得从未接该订单的所有门店
		MapList noRecvThisOrderMap=findNoRecvThisOrder(ofp.orderId,db);
		
		noRecvOrderMap.add(noRecvThisOrderMap);
		
		//获取会员订单
		MemberOrder order=new OrderManager().getMemberOrderById(ofp.orderId, db);
		
		String storeId=findStore(order, noRecvOrderMap);
		
		//检查是否派单成功
		if(Checker.isEmpty(storeId)){
			//派单失败
			
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",false);
			
		}else{
			//派单成功
			//TODO 发送通知
			
			result.put("STOREID", storeId);
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",true);
		}
		
		return result;
	}
	
	
	/**
	 * 从未接单的所有门店
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	private MapList findNoRecvOrder(DB db) throws JDBCException {
		
		String findNorRecvOrderSQL=
				"SELECT * FROM mall_Store  "+
				"	WHERE id NOT IN (   "+
				"	SELECT acceptorderid FROM mall_CommodityDistribution "+
				"	)   ";
		
		MapList map=db.query(findNorRecvOrderSQL);
		
		return map;
	}
	
	
	/**
	 * 从未接此单的所有门店
	 * @param orderId 订单ID
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	private MapList findNoRecvThisOrder(String orderId, DB db) throws JDBCException {
		
		String findNoRecThisOrderSQL=
				"SELECT * FROM mall_Store                               "+
				"	WHERE id NOT IN (                                     "+
				"	SELECT acceptorderid FROM mall_CommodityDistribution  "+
				"	WHERE orderid <>?"+
				"   ORDER BY disttime "+
				"	)  ";
		
		MapList map=db.query(findNoRecThisOrderSQL,orderId,Type.VARCHAR);
		
		return map;
	}
	
	
	/**
	 * 根据坐标寻找最合适的
	 * @param noRecvOrderMap
	 * @return
	 * @throws JDBCException 
	 */
	private String findStore(MemberOrder order,MapList noRecvOrderMap) throws Exception {
		
		String result=null;
		
		if(!Checker.isEmpty(noRecvOrderMap)){
			
			double stdDistance=Var.getDouble("AutoDispatcherDistance", 10);
			result=filterStoreByOrder(order,stdDistance,noRecvOrderMap);
		}
		
		return result;
	}
	
	
	/**
	 * 根据订单和地址来过滤加盟店
	 * @param order
	 * @param distance
	 * @return 门店ID
	 * @throws JDBCException 
	 */
	private String filterStoreByOrder(MemberOrder order,double stdDistance,MapList storeMap) throws Exception{
		
		String storeId=null;
			
		for(int i=0;i<storeMap.size();i++){
				//计算距离，千米
			double distance=GpsPointDistance.getDistance(
					storeMap.getRow(i).getDouble("latitude", 0), 
					storeMap.getRow(i).getDouble("longitud", 0), 
					Double.valueOf(order.getLatitude()),
					Double.valueOf(order.getLongitud()));
			if(stdDistance>=distance){
				storeId=storeMap.getRow(i).get("id");
				break;
			}
				
		}
		return storeId;
	}
	
}
