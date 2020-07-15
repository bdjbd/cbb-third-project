package com.p2p.base.task.param;

import java.util.HashMap;

public class TaskParamterManager 
{
	/** 目标数量 **/
	public static final String FINSH_NUMBER = "目标推广人数";
	/** 已推广 **/
	public static final String NOW_NUMBER = "已推广人数";
	/** 奖品 **/
	public static final String PRIZE = "奖品";
	/** 奖励 积分**/
	public static final String REWARD_SCORE = "奖励积分";
	/**奖励电子券**/
	public static final String REWARD_ELECT = "奖励电子券";
	/**奖励徽章**/
	public static final String REWARD_BADGE= "奖励徽章";
	
	private HashMap mParamterActionList=new HashMap();
	
	public TaskParamterManager()
	{
		mParamterActionList.put(FINSH_NUMBER, "p2p.base.task.param.XXXX");
	}
	
	public String executeJL(TaskParamActionParamObj tap,String name,String value)
	{
		String rValue="";
		
		ITaskParamterGetJL jl=(ITaskParamterGetJL)createClass(mParamterActionList.get(name).toString());
		rValue=jl.executeJL(tap, name, rValue);
		
		return rValue;
	}
	
	public String getHtml(TaskParamActionParamObj tap,String name,String value)
	{
		String rValue="";
		
		ITaskParamterGetHtml gh=(ITaskParamterGetHtml)createClass(mParamterActionList.get(name).toString());
		rValue=gh.executeHtml(tap, name, rValue);
		
		return rValue;
	}
	
	private Object createClass(String className)
	{
		//实现反射
		return new Object();
	}
}
