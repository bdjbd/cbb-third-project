package com.am.frame.state.distAction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.p2p.map.GpsPointDistance;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 3=订单付款
 * 说明：等待分配
 * 下一状态值：4
 * 处理过程：更新状态值
 * 0、	DistState=null->1, DistState!=null->2
 * 1、	增加DistState=3记录,接单ID=null->2, 接单ID!=null->6
 * 2、	获得从未接单的所有门店->3
 * 3、	获得从未接该订单的所有门店（按照分配时间排序）->4
 * 4、	拼接 2+3 的结果集->5
 * 5、	依据订单坐标，寻找10公里（Var中定义）范围内最近的门店，找到->6，未找到->7；
 * 6、	给该门店派单并通知接单ID的设备（手机短信、推送通知）；(Update)DistState=4;
 * 7、	未找到门店则DistState=6，OrderState=31；
 */
public class PaymentOrderDistStateAction implements IStateFlow {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		JSONObject result=new JSONObject();
		
		try{
			
			//订单ID
			String orderId=ofp.orderId;
			
			DB db=DBFactory.getDB();
			
			//更新订单状态
			result=StateFlowManager.getInstance().updateStateData(db, ofp);
			
			MemberOrder order=new OrderManager().getMemberOrderById(orderId,db);
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			
			Map<String,String> otherParam=new HashMap<String,String>();
			
			
			String distId="";
			
			//检查派单状态记录是否存在
			MapList maps=checkerDistExist(orderId,db);
			
			if(Checker.isEmpty(maps)){
				//如果不存在，增加记录，并设置派单状态为3
				distId=addOrderRecode(orderId, "3", db);
			}else{
				distId=maps.getRow(0).get("id");
			}
			
			MapList map=checkRecvOrderId(orderId, db);
			if(!Checker.isEmpty(map)){
				String acceptorderId=map.getRow(0).get("acceptorderid");
				if(!Checker.isEmpty(acceptorderId)){
					//接单ID存在 
					//接单ID！=null
					//通知给该门店派单并通知接单ID的设备（手机短信、推送通知）；(Update)DistState=4;
					//接单ID
					distId=map.getRow(0).get("id");
					StateFlowManager.getInstance().setNextState(commodity.getDistStateID(),"4",distId, otherParam);
				
				}else{
					//接单ID不存在
					//寻找从未接单的所有门店
					MapList noRecvOrderMap=findNoRecvOrder(db);
					//从为接此单的所有门店
					MapList noThisOrderMap=findNoRecvThisOrder(orderId,db);
					//合并集合
					noRecvOrderMap.add(noThisOrderMap);
					//根据坐标寻找最合适的
					String storeId=findStore(order,noRecvOrderMap);
					
					if(!Checker.isEmpty(storeId)){
						//更新ID到接单ID中
						String updateSQL="UPDATE mall_CommodityDistribution SET AcceptOrderID=? WHERE OrderID=?";
						
						db.execute(
								updateSQL,
								new String[]{storeId,orderId},
								new int[]{Type.VARCHAR,Type.VARCHAR});
						
						//找到合适的门店
						distId=map.getRow(0).get("id");
						StateFlowManager.getInstance().setNextState(commodity.getDistStateID(),"4",distId, otherParam);
					
					}else{
						//未找到门店则DistState=6，OrderState=31；
						StateFlowManager.getInstance().setNextState(commodity.getDistStateID(), "6", distId, otherParam);
						StateFlowManager.getInstance().setNextState(commodity.getOrderStateID(), "31", orderId, otherParam);
					}
				}
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
	 * 检查订单对应的派单信息是否存在
	 *  如果存在，返回ture，否则返回false
	 * @param orderId  订单ID
	 * @return 存在返回ture，不存在，返回false
	 * @throws JDBCException 
	 */
	private MapList checkerDistExist(String orderId,DB db) throws JDBCException {
		
		
		String checkExistSQL="SELECT * FROM  mall_CommodityDistribution WHERE orderid=?";
		
		MapList map=db.query(checkExistSQL,orderId,Type.VARCHAR);
		
		return map;
	}
	
	
	/**
	 * 新增 派单记录
	 * @param orderId 订单ID
	 * @param distState 派单状态
	 * @param db
	 * @return 返回主键
	 * @throws JDBCException 
	 */
	public String addOrderRecode(String orderId,String distState, DB db) throws JDBCException {
		
		String id=UUID.randomUUID().toString();
		
		String querySQL="SELECT * FROM mall_commoditydistribution WHERE orderid=? ";
		
		MapList cmdMap=db.query(querySQL, orderId, Type.VARCHAR);
		
		if(!Checker.isEmpty(cmdMap)){
			id=cmdMap.getRow(0).get("id");
			
			String updateSQL="UPDATE mall_commoditydistribution SET disttime=now() WHERE id=? ";
			db.execute(updateSQL, id, Type.VARCHAR);
			
		}else{
			String addSQL="INSERT INTO mall_commoditydistribution("
					+ "id, orderid, diststate, disttime )VALUES "
					+ "(?, ?, ?, now())";
			
			db.execute(addSQL,
					new String[]{id,orderId,distState},
					new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
		}
		
		
		return id;
	}

	/**
	 * 判断接单ID是否存在
	 * @param keyValue
	 * @param db
	 * @return  接单ID存在，返回TRUE
	 * @throws JDBCException 
	 */
	private MapList checkRecvOrderId(String orderId, DB db) throws JDBCException {
		
		String checkSQL="SELECT * FROM mall_CommodityDistribution WHERE orderid=? ";
		MapList map=db.query(checkSQL,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			String acceptorderId=map.getRow(0).get("acceptorderid");
			if(!Checker.isEmpty(acceptorderId)){
			}
		}
		
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
	 * 根据坐标寻找最合适的
	 * @param noRecvOrderMap
	 * @return
	 * @throws JDBCException 
	 */
	private String findStore(MemberOrder order,MapList noRecvOrderMap) throws JDBCException {
		
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
	private String filterStoreByOrder(MemberOrder order,double stdDistance,MapList storeMap) throws JDBCException{
		
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
