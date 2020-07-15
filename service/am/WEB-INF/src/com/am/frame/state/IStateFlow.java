package com.am.frame.state;
/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 
 * 
 */
public interface IStateFlow {

	/**
	 * 
	 * @param ofp
	 * @return {"CODE",1,"MSG","更新成功","SUCCESS",true,"STATE":"state");
	 */
	String execute(OrderFlowParam ofp);
}
