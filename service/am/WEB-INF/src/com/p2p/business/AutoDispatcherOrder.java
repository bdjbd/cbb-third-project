package com.p2p.business;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.wisdeem.wwd.WeChat.Utils;

/**
 * 自动派单服务
 * @author Administrator
 *
 */
public class AutoDispatcherOrder implements Job {
	
	public final String tag="AutoDispatcherOrder";

	@Override
	public void execute(JobExecutionContext jc) throws JobExecutionException 
	{
		int interval=Var.getInt("interval",10);
		
		//recv_time>current_timestamp- interval '"+interval+" minutes'  AND
		//找出所有的未接单的订单记录 并且订单状态为 已经下单(2)的订单。
//		String checkOrderCode="SELECT * FROM p2p_DispatchRecod   "
//				+ " WHERE  ORStatus=0 ORDER BY recv_time ";
		//BUG 498829 调度管理-派单管理-自动派单接口-订单状态为取消时，仍然可以派单 修改
		String checkOrderCode="SELECT dr.* FROM p2p_DispatchRecod AS dr "
				+ " LEFT JOIN ws_order AS od ON od.order_code=dr.order_code "
				+ " WHERE od.data_status=2 and (now()-recv_time)> interval '" + interval + "  Minutes'";
		try 
		{
			DB db=DBFactory.getDB();
			MapList map=db.query(checkOrderCode);
			
			Utils.Log(tag, "自动派单轮训任务启动成功，共发现" + map.size() + "条待派订单。sql=" + checkOrderCode);
			
			if(!Checker.isEmpty(map))
			{
				String orderCode;
				for(int i=0;i<map.size();i++)
				{
					orderCode=map.getRow(i).get("order_code");
					
					Utils.Log(tag, "自动对" + orderCode + "号订单，进行派单操作。");
					
					DispatcherOrderService.dispatcherOrder(orderCode);
				}
			}
			
		} 
		catch (JDBCException e) 
		{
			e.printStackTrace();
		}
		
		
	}

}
