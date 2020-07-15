package com.am.mall.order.distState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2014年11月17日
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
public class PaymentOrderDistStateAction implements IFlowState {

	private static Logger loger=LoggerFactory.getLogger("com.am.mall.order.distState.PaymentOrderDistStateAction");
	
	@Override
	public String execute(OrderFlowParams ofp) {
		try{
			DB db=DBFactory.getDB();
			
			//检查记录是否存在，不存在，新增。
			loger.info("检查记录是否存在");
			if(!checkDistExister(ofp.keyValue,db)){
				//不存在，新增
				loger.info("记录不存在");
				OrderDispatcherManager.getInstance().addOrderRecode(ofp.keyValue,"3",db);
			};
			
			//判断接单ID是否存在
			if(checkRecvOrderId(ofp.keyValue,db)){
				//存在 ，给该门店派单并通知接单ID，update：distState=4；OrderState=4；
				sendNotify(ofp.keyValue,db);
				
			}else{
				//不存在 ，计算未接单的所有门店
				
				//过滤获取从未接该店的所有门店，按照时间排序
				
				//依据坐标，寻找附件最近的门店，找到->6,未找到->7
				
				loger.info("派单ID不存在，计算接单部门");
				
				MapList map=findRightStore(ofp.keyValue,db);
				
				if(!Checker.isEmpty(map)){
					//找到了  给该门店派单并通知 ID，update：distState=4；OrderState=4；
					//门店ID
					String storeId=map.getRow(0).get("id");
					loger.info("找到门店派单并通知,门店ID："+storeId);
					
					sendNotify(ofp.keyValue,storeId,db);
					
					OrderDispatcherManager.getInstance().updateDistState(ofp.keyValue,"4");
					
					//更新接单ID到订单分配表中
					String updateSQL="UPDATE mall_CommodityDistribution SET AcceptOrderID=? WHERE orderid=?";
					db.execute(updateSQL, new String[]{storeId,ofp.keyValue},new int[]{Type.VARCHAR,Type.VARCHAR});
				}else{
					//未找到  未找到门店则DistState=6，OrderState=31；
					loger.info("未找到 ");
					
					OrderDispatcherManager.getInstance().updateDistState(ofp.keyValue,"6");
					
					new OrderManager().updateOrderState(ofp.keyValue,"31");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 发送接单消息给对应的店铺
	 * @param orderId
	 * @param storeId
	 * @param db
	 */
	private void sendNotify(String orderId, String storeId,DB db) {
		
	}

	/**
	 * 发送接单消息给对应的店铺
	 * @param orderId
	 * @param db
	 */
	private void sendNotify(String orderId,DB db) {
	}


	/**
	 * 查询可以获取接收此订单的人
	 * @param orderId 订单编号
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	private MapList findRightStore(String orderId, DB db) throws JDBCException {
		String filterSQL=
				"(SELECT * FROM mall_Store  WHERE id NOT IN ("+
						"	SELECT acceptorderid  "+
						"	FROM mall_CommodityDistribution "+
						"	WHERE acceptorderid IS NOT NULL)) UNION "+
						"	(SELECT *   "+
						"		FROM mall_Store WHERE id NOT IN ( "+
						"			SELECT mcd.AcceptOrderID "+
						"			FROM mall_CommodityDistribution mcd "+
						"			INNER  JOIN mall_Store ms  "+
						"			ON ms.ID=AcceptOrderID  "+
						"			WHERE mcd.OrderID=? )) ";
		
		MapList map=db.query(filterSQL,orderId,Type.VARCHAR);
		
		return map;
	}


	/**
	 * 判断接单ID是否存在
	 * @param keyValue
	 * @param db
	 * @return  接单ID存在，返回TRUE
	 * @throws JDBCException 
	 */
	private boolean checkRecvOrderId(String orderId, DB db) throws JDBCException {
		boolean result=false;
		
		String checkSQL="SELECT * FROM mall_CommodityDistribution WHERE orderid=? ";
		MapList map=db.query(checkSQL,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			String acceptorderId=map.getRow(0).get("acceptorderid");
			if(!Checker.isEmpty(acceptorderId)){
				result=true;
			}
		}
		
		return result;
	}


	/**
	 * 检查派单记录是否存在
	 * @param keyValue
	 * @param db
	 * @return
	 * @throws JDBCException 
	 */
	private boolean checkDistExister(String orderId,DB db) throws JDBCException {
		boolean result=false;
		
		String checkSQL="SELECT * FROM mall_CommodityDistribution WHERE orderid=? ";
		MapList map=db.query(checkSQL,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			loger.info("记录存在");
			result=true;
		}
		
		return result;
	}

}
