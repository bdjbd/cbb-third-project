package com.am.mall.order;
/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 流程状态执行类参数
 */
public class OrderFlowParams {

	/**当前流程ID**/
	public String currentFlowId;
	/**当前流程状态ID**/
	public String currentFlowStateId;
	/**更新数据表名**/
	public String tableName;
	/**更新状态字段名**/
	public String stateFiledName;
	/**更新数据库主键名 订单主键ID**/
	public String keyName;
	/**更新数据库主键值 订单主键 订单ID**/
	public String keyValue;
	/**当前值**/
	public String currentKeyValue;
	/**订单ID**/
	public String orderId;
	
}
