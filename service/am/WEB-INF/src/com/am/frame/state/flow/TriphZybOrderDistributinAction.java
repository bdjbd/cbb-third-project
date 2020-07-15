package com.am.frame.state.flow;

import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />
 * 
 * 智游宝订单配单流程
 * 添加任务到job中，如果成功，则会修改订单状态，如果失败，则不处理
 * 
 */
public class TriphZybOrderDistributinAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		
		//1,调用第三方订单功能，添加异步任务
		
		DB db=null;
		
		try{
			
			db=DBFactory.newDB();
			
			OtherInterfaceTaskManager otherTaskManager=new OtherInterfaceTaskManager();
			
			otherTaskManager.createTask(db,
					"com.am.frame.other.taskInterface.impl.zyb.ZybSendCodeReqOtherTaskImpl",
					ofp.orderId);
			
			logger.info("订单 id："+ofp.orderId+"已提交任务到系统。"
					+ "任务编码：com.am.frame.other.taskInterface.impl.zyb.ZybSendCodeReqOtherTaskImpl");
			
		}catch(Exception e){
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
		
		return "{}";
	}
	
	
}
