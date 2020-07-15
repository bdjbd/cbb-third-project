package com.am.united.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.united.job.service.OrderExpired;

public class OrderExpiredJob implements Job {
private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	
		
		logger.info("检查购买股份订单支付是否过期开始。");
		
		OrderExpired orderexpired=new OrderExpired();
		try {
			orderexpired.orderExpiredstatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("购买股份订单支付是否过期检查结束。");
	}

}
