package com.am.mall.order;

import java.util.UUID;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.beans.order.StateFlowStepSetup;
import com.am.mall.commodity.CommodityManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.map.GpsPointDistance;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 订单分配管理类
 */
public class OrderDispatcherManager {
	
	
	private static OrderDispatcherManager orderManager=null;
	
	private OrderDispatcherManager(){}
	
	
	public static OrderDispatcherManager getInstance(){
		if(orderManager==null){
			orderManager=new OrderDispatcherManager();
		}
		return orderManager;
	}
	
	/**
	 * 依据订单坐标，寻找10公里（Var中定义）范围内最近的门店进行派单，<br />
	 * 派单成功并通知接单ID的设备（手机短信、推送通知）；
	 * 如未找到门店则DistState=6，OrderState=31；
	 * @param orderId 订单ID
	 * @return 
	 */
	public String orderDispatcher(String orderId){
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			MemberOrder memberOrder=new OrderManager().getMemberOrderById(orderId,db);
			//1，检查订单的合法性
			if(memberOrder!=null&&"3".equals(memberOrder.getOrderState())){
				//2,检查订单是否超时
				String checkTimeOutSQL="SELECT disttime+'10 min' < now() AS timeOut  "
						+ " FROM mall_commoditydistribution "
						+ " WHERE orderid=? ORDER BY disttime ";
				
				MapList map=db.query(checkTimeOutSQL,orderId,Type.VARCHAR);
				
				if(Checker.isEmpty(map)){
					//如果在配单中不存在，直接按照坐标去查询，分配订单
					//3，依据订单坐标，寻找10公里（Var中定义）范围内最近的门店进行派单.
					//获得标准距离，千米.默认为10公里
					double standerDistance=Var.getDouble("AutoDispatcherDistance",10);
					//获取可以配送加盟店
					String storeId=filterStoreByOrder(memberOrder,standerDistance,db);
					
					if(storeId!=null){
						//如果可接单加盟店存在，则将订单直接分配给对应的加盟店
						String addSQL="INSERT INTO mall_commoditydistribution("
								+ "id, orderid, diststate, disttime, acceptorderid )VALUES "
								+ "(?, ?, ?, now(),?)";
						
						db.execute(addSQL,
								new String[]{UUID.randomUUID().toString(),orderId,"4",storeId},
								new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
						
						
					}else{
						//可以接单的加盟店不存在，将数据更新至订单分配数据表中,由人工进行分配
						//如果可接单加盟店存在，则将订单直接分配给对应的加盟店
						String addSQL="INSERT INTO mall_commoditydistribution("
								+ "id, orderid, diststate, disttime )VALUES "
								+ "(?, ?, ?, now())";
						
						db.execute(addSQL,
								new String[]{UUID.randomUUID().toString(),orderId,"3"},
								new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
					}
				}else{
					//如果存在，则判断是否超时，如果超时
					String timeOut=map.getRow(0).get(1);
					if("t".equalsIgnoreCase(timeOut)){
						//超时不处理
						
					}else{
						//未超时,继续分配菜单
						//不处理
					}
				}
			}
		}catch(JDBCException e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * 依据订单坐标，寻找10公里（Var中定义）范围内最近的门店进行派单，
	 * 派单成功并通知接单ID的设备（手机短信、推送通知）；
	 * 如未找到门店则DistState=6，OrderState=31；
	 * @param orderId
	 * @return 返回可以接单的门店ID,如果没有，返回null
	 */
	public String distributionOrder(String orderId){
		
		String storeId=null;
		
		try{
			double standerDistance=Var.getDouble("AutoDispatcherDistance",10);
			
			DB db=DBFactory.getDB();
			
			//获取可以接单的组织
			storeId=filterStoreByOrder(orderId, standerDistance, db);
			
			//TODO  推送功能未实现   百度推送，短信推送等方式确定再修改。
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return storeId;
	}
	
	
	/**
	 * 根据订单和地址来过滤加盟店
	 * @param order
	 * @param distance
	 * @return 门店ID
	 * @throws JDBCException 
	 */
	private String filterStoreByOrder(MemberOrder order,double stdDistance,DB db) throws JDBCException{
		
		String storeId=null;
		//按照筛选标准的筛选门店SQL
		String getStoreSQL="";
		MapList storeMap=db.query(getStoreSQL);
			
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
	
	
	/**
	 * 根据订单和地址来过滤加盟店
	 * @param orderId 订单编号
	 * @param distance
	 * @return 门店ID
	 * @throws JDBCException 
	 */
	private String filterStoreByOrder(String orderId,double stdDistance,DB db) throws JDBCException{
		
		MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
		
		String storeId=null;
		storeId=filterStoreByOrder(order, stdDistance, db);
		
		return storeId;
	}
	
	
	/**
	 * 新增 派单记录
	 * @param orderId 订单ID
	 * @param distState 派单状态
	 * @param db
	 * @throws JDBCException 
	 */
	public void addOrderRecode(String orderId,String distState, DB db) throws JDBCException {
		
		String addSQL="INSERT INTO mall_commoditydistribution("
				+ "id, orderid, diststate, disttime )VALUES "
				+ "(?, ?, ?, now())";
		
		db.execute(addSQL,
				new String[]{UUID.randomUUID().toString(),orderId,distState},
				new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
	}


	/**
	 * 更新服务状态,直接更新订单流程状态，不执行流程Action
	 * @param keyValue
	 * @param disState
	 */
	public boolean updateDistState(String orderId, String disState) {
		boolean result=false;
		
		try{
			DB db=DBFactory.getDB();
			
			String updateSQL="UPDATE mall_CommodityDistribution SET diststate=? WHERE orderid=? ";
			
			if(db.execute(updateSQL, 
					new String[]{disState,orderId},
					new int[]{Type.VARCHAR,Type.VARCHAR})>=0){
				result=true;
			}
			
		}catch(Exception e){
			result=false;
		}
		
		return result;
	}


	/**
	 * 设置订单分配状态,此方法会调用服务流程中的流程状态流程Action
	 * @param OrderFlowParams param参数
	 * @param distState 分配状态
	 */
	public String setDistState(OrderFlowParams param, String distState) {
		
		JSONObject result=new JSONObject();
		
		try{
			ServerFlowManager flowManager=ServerFlowManager.getInstance();
			
			//获取订单分配状态参数和Action，并执行及状态Action
			StateFlowStepSetup sfss=flowManager.getStateFlowStepSetup(
					distState,param.currentFlowStateId);
			
			
			String distClazz=sfss.getClassPath();
			if(distClazz!=null){
				IFlowState flowState=(IFlowState)Class.forName(distClazz).newInstance();
				
				result.put("SUCCESS",flowState.execute(param));
			}
			
			//构造返回值
			result.put("CODE", 1);
			result.put("MSG","");
			result.put("STATE",param.keyValue);
			result.put("ORDERID",param.orderId);
			
			
		}catch(Exception e){
			e.printStackTrace();
			
			try {
				result.put("CODE", 0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}
		return result.toString();
	}


	/**
	 * 获取带分配，未超时的订单集合
	 * 
	 * @return  MapList 待分配，未超时的订单集合
	 */
	public MapList getWaitDistOrders(DB db) {
		
		MapList result=null;
		try{
			int timeOut=Var.getInt("am_dispatcherTimeOut",10);
			
			String filterOrderSQL="SELECT * FROM mall_CommodityDistribution  "
					+ "	WHERE diststate='3' "
					+ " AND disttime+'"+timeOut+" min'>now()";
			
			result=db.query(filterOrderSQL);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}


	/**
	 * 根据订单ID获取订单分配状态值
	 * @param orderId 订单ID
	 * @param db DB
	 * @return 订单分配状态值
	 */
	public String getOrderidStateValueById(String orderId, DB db) {
		
		String result=null;
		
		try{
			String stateSQL="SELECT * FROM mall_CommodityDistribution WHERE orderid=? ";
			
			MapList map=db.query(stateSQL,orderId, Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				result=map.getRow(0).get("diststate");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 接单方法
	 * @param distId 派单ID
	 * @param isAccept 是否接单，true接单，false拒绝接单
	 * @param acceptId  接单人ID
	 * @param db  DB
	 * @throws Exception
	 */
	public void acceptDispatcher(String distId,String isAccept,String acceptId,DB db ) throws Exception{
	
		JSONObject result=new JSONObject();
		
		if("true".equalsIgnoreCase(isAccept)){
			//接单处理流程
			//1，检查是否过时
			if(!checkTimeOut(distId,db)){
				//2，如果没有过时，接单，并保持数据到数据库
				result=accetpDist(distId,acceptId,db);
			}else{
				result.put("CODE",0);
				result.put("MSG","订单已超时");
			}
		}else{
			//拒绝分配
			//保存成功 ，修改派单状态为已经接单,派单状态为 
			String findDistOrderInfoSQL="SELECT * FROM mall_CommodityDistribution WHERE id=?";
			
			MapList map=db.query(findDistOrderInfoSQL,distId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				String orderId=map.getRow(0).get("orderid");
				
				MemberOrder order=new OrderManager().getMemberOrderById(orderId,db);
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
				
				OrderFlowParams param=ServerFlowManager.getInstance().builderParamList(order, commodity, db);
				
				try {
					result=new JSONObject(OrderDispatcherManager.getInstance().setDistState(param,"6"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 检查是否过时
	 * @param distId  派单ID
	 * @return  超时返回TRUE，未超时，返回false
	 * @throws JDBCException 
	 */
	private boolean checkTimeOut(String distId,DB db) throws JDBCException {
		boolean result=false;
		
		int timeOut=Var.getInt("am_dispatcherTimeOut",10);
		
		String checkSQL="SELECT   CASE WHEN disttime+'"+timeOut+" min'< now() THEN 'true' ELSE 'false' END AS timeout "
				+ " FROM mall_CommodityDistribution WHERE id=? ";
		
		MapList map=db.query(checkSQL,distId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)&&"t".equalsIgnoreCase(map.getRow(0).get("timeout"))){
			result=true;
		}
		
		return result;
	}
	
	
	
	/**
	 * 接单
	 * @param distId  派单ID
	 * @param acceptId 接单组织或者个人ID
	 * @param acceptType  接单类型 个人为PERSONAL，组织为GROUP
	 * @throws JDBCException 
	 */
	private JSONObject accetpDist(String distId, String acceptId,DB db) throws JDBCException{
		
		JSONObject result=new JSONObject();
		
		Table table=new Table("am_bdp", "MALL_COMMODITYDISTRIBUTION");
		
		TableRow row=table.addUpdateRow();
		
		row.setOldValue("id", distId);
		row.setValue("acceptorderid", acceptId);
	
		if(db.save(table).length>0){
			//保存成功 ，修改派单状态为已经接单,派单状态为 
			String findDistOrderInfoSQL="SELECT * FROM mall_CommodityDistribution WHERE id=?";
			
			MapList map=db.query(findDistOrderInfoSQL,distId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				String orderId=map.getRow(0).get("orderid");
				
				MemberOrder order=new OrderManager().getMemberOrderById(orderId,db);
				Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
				
				OrderFlowParams param=ServerFlowManager.getInstance().builderParamList(order, commodity, db);
				
				try {
					result=new JSONObject(OrderDispatcherManager.getInstance().setDistState(param,"5"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
}
