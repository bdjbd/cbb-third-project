package com.p2p.base.task.param;

public class FinshNumberTaskParam implements ITaskParamterGetHtml,
		ITaskParamterGetJL {

	@Override
	public String executeJL(TaskParamActionParamObj tap, String name,
			String value) {
		
		return "";
	}

	@Override
	public String executeHtml(TaskParamActionParamObj tap, String name,
			String value) {
		String rValue="完成"+value+"个人的推广。";
		return rValue;
	}

}
