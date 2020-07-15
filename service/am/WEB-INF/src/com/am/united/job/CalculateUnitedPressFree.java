package com.am.united.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.united.job.service.UnitedService;
import com.fastunit.Var;

/**
 * @author YueBin
 * @create 2016年4月26日
 * @version 
 * 说明:<br />
 * 联合会会费管理
 */
public class CalculateUnitedPressFree implements Job {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//开关  0开  1关
		int freeSwitch=Var.getInt("freeSwitch", 0);
		
		if(freeSwitch==0){
			
		logger.info("计算农技协联合会会费计算开始。");
		
		UnitedService unitedService=new UnitedService();
		try {
			unitedService.calculateUnitedPressFree();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("计算农技协联合会会费结束。");
		}
	}

}
