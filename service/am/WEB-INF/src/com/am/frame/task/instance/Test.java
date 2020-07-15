package com.am.frame.task.instance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.TaskEngine;
import com.p2p.service.IWebApiService;

public class Test implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		RunTaskParams runt = new RunTaskParams();
		
		runt.setMemberId("186726e4-b516-4aa9-94d7-814bc1f6fa61");
		
		runt.pushParam("RECHARGEMONEY",1200);
		
		runt.setTaskName("充值奖励任务");
		
		TaskEngine.getInstance().executTask(runt);
		
		return "已完成";
	}

}
