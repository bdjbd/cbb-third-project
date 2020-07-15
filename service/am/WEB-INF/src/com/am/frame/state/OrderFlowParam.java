package com.am.frame.state;


/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 订单流程参数
 * 集合参数:<br />
 * <li>String state_flow_id :状态流程id       </li>
 * <li>String state_value : 状态值            </li>
 * <li>String table_name : 表名               </li>
 * <li>String state_field_name ： 状态字段名  </li>
 * <li>String key_name ： 主键名              </li>
 * <li>String next_state_value ： 下一状态值  </li>
 * <li>ParamList 流程状态参数字段所定义的其他参数 </li>
 * <li>orderId 订单ID </li>
 */
public class OrderFlowParam {
	
	/**订单状态Action编号，在同一个状态有多个Action时使用**/
	public final static  String STATE_ACTON_CODE="动作编号";
	/**接单ID**/
	public final static  String ACCEPT_ID="接单ID";
	
	/**订单ID**/
	public String orderId;
	
	/**状态流程id**/
	public String stateFlowId;
	
	/**表名**/
	public String tableName;
	
	/**状态字段名**/
	public String stateFieldName;
	
	/**状态值**/
	public String stateValue;
	
	/**主键名**/
	public String keyName;
	/**主键值**/
	public String keyValue;
	
	/** 下一状态值**/
	public String nextStateValue;
	/**失败状态值**/
	public String failureStateVaule;
	
	/**参数字段所定义的其他参数**/
	public ParamList paramList;

}
