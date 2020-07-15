package com.am.mall.order.distState;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderDispatcherManager;
import com.am.mall.order.OrderFlowParams;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月17日
 * @version 
 * 说明:<br />
 * 4 已经分配 等待接受分配确认
 * 下一状态值：5,6
 * 处理过程：
 * 0、	记录是否存在，存在->1,不存在->3
 * 1、	判断是否已经超时，超时->2，未超时->3；
 * 2、	设置DistState=7
 * 3、	不操作；
 */
public class AlreadyAssignedDistStateAction implements IFlowState {

	@Override
	public String execute(OrderFlowParams odp) {
		
		String result="false";
		
		try{
			DB db=DBFactory.getDB();
			
			String orderId=odp.keyValue;
			
			//1,检查记录是否存在
			if(checkerExistRecoder(orderId,db)){
				//记录存在
				if(verifyTimeOut(orderId,db)){//检查是否超时
					//超时，设置DistState=7
					OrderDispatcherManager.getInstance().updateDistState(orderId, "7");
				}
				//未超时 不操作；
					
			}else{
				result="false";
			}
			//记录不存在    不操作；
				
			
		}catch(Exception e){
			result="false";
		}
		return result;
	}

	
	/**
	 * 检查是否订单是否超时
	 * @param orderId 订单ID
	 * @param db  DB
	 * @return 超时，返回true，否则，返回false
	 * @throws JDBCException 
	 */
	private boolean verifyTimeOut(String orderId, DB db) throws JDBCException {
		boolean result=false;
		
		//获取系统设置超时时间
		String sysTimeOut=Var.get("am_dispatcherTimeOut");
		
		//2,检查订单是否超时
		String checkTimeOutSQL="SELECT disttime+'"+sysTimeOut+" min' < now() AS timeOut  "
				+ " FROM mall_commoditydistribution "
				+ " WHERE orderid=? ORDER BY disttime ";
		
		MapList map=db.query(checkTimeOutSQL,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			String timeOut=map.getRow(0).get("timeout");
			if("t".equalsIgnoreCase(timeOut)){
				result=true;
			}
		}
		
		return result;
	}



	/**
	 * 检查记录是否存在
	 * @param orderId 订单ID
	 * @return 存在，返回true，否则，返回false
	 * @throws JDBCException
	 */
	private boolean checkerExistRecoder(String orderId,DB db) throws JDBCException {
		
		boolean result=false;
		
		String checkExistSQL="SELECT * FROM  mall_CommodityDistribution WHERE orderid=? ";
		
		MapList map=db.query(checkExistSQL,orderId,Type.VARCHAR);
		
		if(!Checker.isEmpty(map)){
			result=true;
		}
		
		return result;
	}

	
}
