package com.p2p.base.task.param;

public class NowNumberTaskParam implements ITaskParamterGetHtml,
		ITaskParamterGetJL {

	@Override
	public String executeJL(TaskParamActionParamObj tap, String name,
			String value) {
		
		return "";
	}

	@Override
	public String executeHtml(TaskParamActionParamObj tap, String name,
			String value) {
		// TODO Auto-generated method stub
		return "目前已完成"+value+"人的推广。";
	}

}
