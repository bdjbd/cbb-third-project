package com.am.mall.order;
/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 流程状态参数执行类，主要完成状态的更新，
 */
public interface IFlowState {
	
	/**
	 * 执行参数类方法
	 * @param rrp 参数值
	 * @return 执行结果，JSON格式字符串
	 */
	String execute(OrderFlowParams param);
}
