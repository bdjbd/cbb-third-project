package com.am.frame.state.flow.zyb;

import com.am.frame.other.taskInterface.OtherInterfaceTaskManager;
import com.am.frame.state.IStateFlow;
import com.am.frame.state.OrderFlowParam;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 说明:<br />
 *          智游宝，订单退款流程Action
 */
public class TriphReturnMeonyStateAction implements IStateFlow {

	@Override
	public String execute(OrderFlowParam ofp) {
		// 1,获取流程信息
		// 2,添加退款流程任务

		DB db = null;

		try {
			db=DBFactory.newDB();
			
			
			//检查订单是否可以退款，如果可以，则退款
			if("3".equals(ofp.stateValue)||"320".equalsIgnoreCase(ofp.stateValue)){
				// 退单任务编码
				String taskImplCode = "com.am.frame.other.taskInterface.impl.zyb.ZybCancelNewTaskImpl";

				OtherInterfaceTaskManager otherManager = new OtherInterfaceTaskManager();

				otherManager.createTask(db, taskImplCode, ofp.orderId);
			}

			//修改订单状态为订单为退款中
			String updateSQL="UPDATE "+ofp.tableName+" SET "+ofp.stateFieldName+"='"+
					ofp.nextStateValue+"' WHERE "+
					ofp.keyName+"='"+ofp.keyValue+"'";
			
			db.execute(updateSQL);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
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
