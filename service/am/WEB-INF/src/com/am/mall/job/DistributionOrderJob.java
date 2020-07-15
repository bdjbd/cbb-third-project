package com.am.mall.job;

import java.util.HashMap;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 订单分配JOB
 * 
 */
public class DistributionOrderJob implements Job {

	
	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			//1，遍历订单状态为30的订单，执行超时检查Action
			String status="30";
			checkDistTimeout(db,status);
			
			//2，过滤获取订单状态为3和32的订单集合。
			MapList distMap=getDistMapList(db);
			
			//3，遍历订单状态为3和32的集合，进行自动派单
			autoDistOrder(distMap,db);
			
		}catch(Exception e){
			e.printStackTrace();
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}

	
	/**
	 * 遍历集合自动派单
	 * @param distMap
	 * @throws JDBCException 
	 */
	private void autoDistOrder(MapList distMap,DB db) throws JDBCException {
		
		if(!Checker.isEmpty(distMap)){
			StateFlowManager stateFlowManager=StateFlowManager.getInstance();
			//订单id
			String orderId=distMap.getRow(0).get("id");
			String stateValue="";
			
			Commodity commodity=null;
			
			Map<String,String> otherParam=new HashMap<String, String>();
			
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"330");
			
			for(int i=0;i<distMap.size();i++){
				
				//订单状态值
				stateValue=distMap.getRow(i).get("orderstate");
				//商品ID
				String commodtyid=distMap.getRow(i).get("commodityid");
				//订单对应的商品信息
				commodity=CommodityManager.getInstance().getCommodityByID(commodtyid, db);
				
				orderId=distMap.getRow(0).get("id");
				//订单ID
				String orderStateID=commodity.getOrderStateID();
				
				stateFlowManager.setNextState(orderStateID,stateValue,orderId, otherParam);
			}
		}
		
	}

	/**
	 * 过滤获取订单状态为3和32的订单集合。
	 * 
	 * 订单派单规则，如果订单配送方式为预约送货，则需要到预约日期后才可以配送，进行订单分配
	 * @param db
	 * @return MapList
	 * @throws JDBCException 
	 */
	private MapList getDistMapList(DB db) throws JDBCException {
		
		String filedSQL="SELECT id,orderstate,commodityid "
				+ "  FROM mall_MemberOrder WHERE orderstate IN (?,?) "
				//理性农业项目 送货方式和预约送货日期控制
				+ "  AND CASE WHEN shipping_method=2 THEN bespeak_time <=now() ELSE 1=1 END  ";
		
		MapList map=db.query(filedSQL,
				new String[]{"3","32"},
				new int[]{Type.VARCHAR,Type.VARCHAR});
		
		return map;
	}


	/**
	 * 遍历订单状态为30的订单，执行超时检查Action
	 * @param db DB 
	 * @throws JDBCException 
	 */
	private void checkDistTimeout(DB db,String status) throws JDBCException{
		
		int timeOut=Var.getInt("am_dispatcherTimeOut",10);
		
		//只对超时的action执行超时检查
		String filterOrderSQL="SELECT id,memberid,orderstate,disttime "
				+ " FROM mall_MemberOrder  "
				+ " WHERE orderstate='"+status+"' "
				+ " AND  disttime+'"+timeOut+" min' >now()";
		
		MapList map=db.query(filterOrderSQL);
		
		if(!Checker.isEmpty(map)){
			
			StateFlowManager stateFlowManager=StateFlowManager.getInstance();
			//订单id
			String orderId=map.getRow(0).get("id");
			
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			
			Commodity commodity=CommodityManager.getInstance().getCommodityByID(order.getCommodityID(), db);
			
			Map<String,String> otherParam=new HashMap<String, String>();
			
			otherParam.put(OrderFlowParam.STATE_ACTON_CODE,"303");
			
			for(int i=0;i<map.size();i++){
				stateFlowManager.setNextState(commodity.getOrderStateID(),"30",orderId, otherParam);
			}
		}
		
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

}
