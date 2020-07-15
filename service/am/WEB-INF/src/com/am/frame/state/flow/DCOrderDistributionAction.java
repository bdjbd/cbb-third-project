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
 * @create 2016年04月29日
 * @version 
 * 说明:<br />
 * 理性农业派单规则:
 *		依据订单的收货地址，将订单分配到对应的收货地址对应的省市区的配送中心,区县配送中心也有门店
 * 订单状态 3 支付完成、32 接单超时
 * 
 * 查找所有订单状态为3/32，依据组织派单规则派单；
 * 	如果订单类型为预约配送的并且没有到预约送货时间，则不配送，则不自动配单
 * 派单成功修改状态到下一个状态值
 * 
 */
public class DCOrderDistributionAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			JSONObject distResult=null;
			try{
				distResult=dcDistOrderRule(ofp,db);
//				distResult=orgDistOrderRule(ofp, db);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(distResult!=null&&distResult.getBoolean("SUCCESS")){
				//派单成功
				
				//接单ID  配送中心 机构ID
				String acceptID=distResult.getString("DCID");
				
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
	 * 配送中心接单
	 * 1，获取订单的省，市，区县信息->2;
	 * 2，查询订单省市区县的配送纵向->3;
	 * 3，给配送中心配单，Update distState=4, updateAccepe;
	 * 4，未找到配送中心,Update orderState=31;
	 * 
	 */
	private JSONObject dcDistOrderRule(OrderFlowParam ofp,DB db)throws Exception {
		JSONObject result=new JSONObject();
		
		String dcOrgId=findDCByOrderId(db,ofp.orderId);
		//检查是否派单成功
		if(Checker.isEmpty(dcOrgId)){
			//派单失败
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",false);
			
		}else{
			//派单成功
			//TODO 发送通知
			
			result.put("STOREID", dcOrgId);
			result.put("DCID", dcOrgId);
			result.put("ORDERID", ofp.orderId);
			result.put("SUCCESS",true);
		}
		
		return result;
	}
	
	/**
	 * 根据订单id获取对应的配送中心
	 * @param db DB
	 * @param orderId  订单ID
	 * @return  配送中心机构id  如果没有，返回null
	 * @throws JDBCException 
	 */
	private String findDCByOrderId(DB db, String orderId) throws JDBCException {
		
		String dcId=null;
		
		String findSQL="SELECT dc.orgid,dc.id,dc.gap_name,dc.gap_code,dc.province_id,dc.city_id,dc.zone_id "+
				 " FROM mall_distribution_center AS dc "+
				 " LEFT JOIN mall_MemberOrder AS morder "+
				 " ON morder.province_id=dc.province_id AND morder.city_id=dc.city_id AND morder.zone_id=dc.zone_id "+
				 " WHERE morder.id=? ";
		
		MapList recvDcMap=db.query(findSQL, orderId, Type.VARCHAR);
		
		if(!Checker.isEmpty(recvDcMap)){
			dcId=recvDcMap.getRow(0).get("orgid");
		}
		return dcId;
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
