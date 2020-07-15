package com.am.frame.state.flow;

import com.am.frame.state.OrderFlowParam;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * @author YueBin
 * @create 2016年6月30日
 * @version 
 * 说明:<br />
 * 酒店，租车配单Action
 */
public class TriphHotelRentalOrderAction extends DefaultOrderStateAction {

	@Override
	public String execute(OrderFlowParam ofp) {
		String result=super.execute(ofp);
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			//1,检查订单的商城分类，如果是酒店类的，需要执行消费任务
			String querSQL="SELECT mc.mall_class,mc.id,od.memberid  "
					+ " FROM mall_commodity AS mc "
					+ " LEFT JOIN mall_MemberOrder AS od ON od.commodityid=mc.id "
					+ " WHERE od.id=? ";
			MapList orderMsgMap=db.query(querSQL,
					new String[]{
					ofp.orderId
			},new int[]{
					Type.VARCHAR
			});
			
			if(!Checker.isEmpty(orderMsgMap)){
				Row row=orderMsgMap.getRow(0);
				String memberId=row.get("memberid");
				TaskEngine taskEngine=TaskEngine.getInstance();
				
				//执行酒店消费任务
				RunTaskParams params=new RunTaskParams();
				params.setMemberId(memberId);
				params.setTaskCode("CONSUMPTION_TASK");
				params.setMemberOrderId(ofp.orderId);
				//执行任务
				taskEngine.executTask(params);
			}
			
			//1，酒店类订单，在订单执支付，到带使用状态时，发送短信
			OrderManager orderManager=new OrderManager();
			orderManager.notifyClient(db,ofp.orderId);
			
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
		
		
		return result;
	}
	
}
