package com.am.frame.state;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.mall.beans.order.MemberOrder;
import com.am.mall.beans.order.StateFlowSetup;
import com.am.mall.beans.order.StateFlowStepSetup;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月27日
 * @version 
 * 说明:<br />
 * 流程管理类，本系统的所有流程都通过此类来管理运行<br />
 * 此类主要提供以下几个方法：<br />
 * <ul>
 * 	<li>
 * //获得该表可用的所有状态流程<br />
 * Map getSupeportFlow(String tablename,String StateFieldName);
 * 	</li>
 * <li>
 * //设置首流程状态值，并不调用状态值实现类,<br />
 * //返回当前的状态值 {CODE:value,MSG:valu,STATE:value}<br />
 * String setFirstState(String StateFlowID,String id); 
 * </li>
 * <li>
 * //系统自动实例化StateValue的实现类并执行<br />
 * //系统默认实现类，将StateValue的NextStateValue设置到id记录上<br />
 * //return 0=失败；1=成功；当前的状态值 {CODE:value,MSG:valu,STATE:value}<br />
 * String setNextState(String StateFlowID,String StateValue,String id,HashMap OtherParam);<br />
 * <li>
 * //得到该状态值的下一状态值集合<br />
 * String[] getStateNexts(String StateFlowID,String StateValue); 
 * </li>
 * 
 * </ul>
 */
public class StateFlowManager {
	
	private StateFlowManager(){}
	
	private static StateFlowManager stateFlowManager;
	
	public static StateFlowManager getInstance(){
		
		if(stateFlowManager==null){
			stateFlowManager=new StateFlowManager();
		}
		return stateFlowManager;
	}

	private Logger logger=LoggerFactory.getLogger(com.am.frame.state.StateFlowManager.class);
	
	/**
	 * 获得该表可用的所有状态流程
	 * @param tablename 表名称
	 * @param StateFieldName 状态字段
	 * @return 
	 */
	public Map<String,String> getSupeportFlow(String tablename,String stateFieldName){
		
		return null;
	}

	
	/**
	 * 设置首流程状态值，并不调用状态值实现类,
	 * @param StateFlowID 流程状态ID
	 * @param id  需要更新的数据ID
	 * @return 返回当前的状态值 {CODE:value,MSG:valu,STATE:value}
	 */
	public String setFirstState(String stateFlowID,String id){
		//返回值
		JSONObject result=new JSONObject();
		
		try{
			//获取流程
			StateFlowSetup flow=getStateFlowSetupById(stateFlowID);
			//首流程状态
			StateFlowStepSetup flowFirstState=getFirstStateFlowStepSetup(stateFlowID);
			
			result.put("CODE", 
					updateTableState(flow.getTableName(),flow.getStateFieldName(), flowFirstState.getStateValue(),flow.getKeyName(),id));
			
			result.put("STATE",flowFirstState.getStateValue());
			result.put("MSG","");
			
		}catch(Exception e){
			try {
				result.put("MSG",e.getMessage());
				result.put("CODE","-1");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		return result.toString();
	
	}
	
	/**
	 * 更新表字段状态
	 * @param tableName 表名
	 * @param stateFiledName 状态字段名
	 * @param stateValue 状态值
	 * @param keyName 更新Where字段名称
	 * @param keyValue 更新Where字段值
	 * @return 影响行数
	 * @throws JDBCException
	 */
	public int updateTableState(String tableName,String stateFiledName,String stateValue,String keyName,String keyValue) throws JDBCException{
		
		int result=0;
		DB db=DBFactory.getDB();
		
		//更新流程的首流程状态
		String updateSQL="UPDATE "+tableName+" SET "+stateFiledName+"=? WHERE "+keyName+"=?";
		result=db.execute(updateSQL, 
				new String[]{stateValue,keyValue}, 
				new int[]{Type.VARCHAR,Type.VARCHAR});
		
		return result;
	}
	

	/**
	 * 获取状态流程的第一个流程状态
	 * @param flowId 
	 * @return
	 * @throws JDBCException 
	 */
	public StateFlowStepSetup getFirstStateFlowStepSetup(String flowId) throws JDBCException{
		
		StateFlowStepSetup result=null;
		String sql="SELECT * FROM mall_StateFlowStepSetup  WHERE  stateflowsetupid=?  ORDER BY sort ";
		
		DB db=DBFactory.getDB();
		MapList map=db.query(sql,new String[]{flowId},new int[]{Type.VARCHAR});
		
		result=new StateFlowStepSetup(map);
		
		return result;
	}
	
	
	/**
	 * 系统自动实例化StateValue的实现类并执行
	 * 系统默认实现类，将StateValue的NextStateValue设置到id记录上
	 * @param StateFlowID 流程状态
	 * @param StateValue 当前流程状态
	 * @param id  更新数据ID
	 * @param OtherParam  其他参数值，如ORDERID
	 * @return return 0=失败；1=成功；当前的状态值 {CODE:value,MSG:valu,STATE:value}
	 */
	public String setNextState(String stateFlowID,String stateValue,String id,Map<String,String> otherParam){
		
		logger.info("系统自动实例化StateValue的实现类并执行  StateFlowID:"+stateFlowID+"\nStateValue:"+stateValue+"\nid:"+id);
		
		String result="";
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			result=setNextState(stateFlowID, stateValue, id, otherParam, db);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * 系统自动实例化StateValue的实现类并执行
	 * 系统默认实现类，将StateValue的NextStateValue设置到id记录上
	 * @param StateFlowID 流程状态
	 * @param StateValue 目标流程状态
	 * @param id  更新数据ID
	 * @param OtherParam  其他参数值，如ORDERID
	 * @return return 0=失败；1=成功；当前的状态值 {CODE:value,MSG:valu,STATE:value}
	 */
	public String setNextState(String stateFlowID,String stateValue,String id,Map<String,String> otherParam,DB db){
		
		logger.info("系统自动实例化StateValue的实现类并执行  StateFlowID:"+stateFlowID+"\nStateValue:"+stateValue+"\nid:"+id);
		
		JSONObject result=new JSONObject();
		
		try{
			
			OrderManager orderManager=new OrderManager();
			//获取订单信息
			MemberOrder order=orderManager.getMemberOrderById(id, db);
			
			//构造状态参数
			OrderFlowParam ofp=builderOrderFlowParam(stateFlowID,stateValue,id,otherParam,db);
			//获取流程状态信息
			StateFlowStepSetup stateFlowss=getStateFlowStepSetup(stateValue, stateFlowID,db);
			
			String actionCode=otherParam.get(OrderFlowParam.STATE_ACTON_CODE);
			
			if(!Checker.isEmpty(actionCode)){
				stateFlowss=getStateFlowStepSetup(stateValue, stateFlowID,actionCode,db);
			}
			
			
			//根据状态值获取状态的执行类并执行
			if(stateFlowss!=null&&stateFlowss.getClassPath()!=null){
					
				IStateFlow stateFlow=(IStateFlow)Class.forName(stateFlowss.getClassPath()).newInstance();
				String res=stateFlow.execute(ofp);
					
				result=new JSONObject(res);
			}else{
				logger.info("未找到对应的订单的流程");
			}
			
			//stateValue  82 订单完成
			if("82".equals(stateValue)){
				orderManager.updateOrderCompleteDate(id, db);
			}
			
			//如果是执行订单完成Action，则调用分销任务
			if("102".equals(otherParam.get(OrderFlowParam.STATE_ACTON_CODE))){
//				logger.info("订单执行代码102 执行任务");
//				TaskEngine taskEngine=TaskEngine.getInstance();
//				
//				
//				RunTaskParams params=new RunTaskParams();
////				params.setTaskName("分利任务");
//				params.setTaskCode(ConsumerInterestTask.TASK_ECODE);
//				params.setMemberId(order.getMemberID());
//				params.setMemberOrderId(order.getId());
//				
//				taskEngine.executTask(params);
//				
//				params=new RunTaskParams();
//				params.setTaskName("消费任务");
//				params.setMemberId(order.getMemberID());
//				params.setMemberOrderId(order.getId());
//				taskEngine.executTask(params);
//				
			}
			
			
		}catch(Exception e){
			try{
				result.put("CODE",0);
				result.put("MSG",e.getMessage());
				result.put("SUCCESS",false);
				result.put("STATE","");
				
			}catch(JSONException je){
				je.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return result.toString();
	}
	


	/**
	 * 得到该状态值的下一状态值集合
	 * @param StateFlowID 流程状态ID
	 * @param StateValue  状态值
	 * @return 返回该状态值的下一状态值集合
	 */
	public String[] getStateNexts(String stateFlowID,String stateValue){
		
		logger.info("得到该状态值的下一状态值集合 stateFlowID:"+stateFlowID+"\nstateValue:"+stateValue);
		return null;
	}

	
	/********************内部接口****************************/
	
	/**
	 *构造状态参数
	 * @param stateFlowID  流程ID
	 * @param stateValue 状态值
	 * @param id  更新数据ID
	 * @param otherParam  其他参数
	 * @return OrderFlowParam 订单流程参数集合
	 * @throws JDBCException 
	 */
	private OrderFlowParam builderOrderFlowParam(String stateFlowID,String stateValue, String id, Map<String, String> otherParam,DB db) throws JDBCException {
		
		OrderFlowParam ofp=new OrderFlowParam();
		
		ofp.orderId=id;
		
		ofp.stateFlowId=stateFlowID;
		
		//判断是否有ActonCode
		String actionCode=null;
		
		if(!Checker.isEmpty(otherParam)){
			actionCode=otherParam.get(OrderFlowParam.STATE_ACTON_CODE);
		}
		
		/**1,获取流程参数**/
		String findFlowStateSQL="SELECT * FROM  mall_StateFlowSetup WHERE id=?";
		
		MapList map=db.query(findFlowStateSQL,stateFlowID ,Type.VARCHAR );
		
		if(!Checker.isEmpty(map)){
			//表名
			ofp.tableName=map.getRow(0).get("tablename");
			//主键名
			ofp.keyName=map.getRow(0).get("keyname");
			//状态字段名
			ofp.stateFieldName=map.getRow(0).get("statefieldname");
			logger.info("订单 id："+id+"\t 的流程名称为："+map.getRow(0).get("name"));
			
		}
		
		/**2,获取状态参数**/
		
		StateFlowStepSetup stateFlowss=null;
		
		if(!Checker.isEmpty(actionCode)){
			
			actionCode=OrderFlowParam.STATE_ACTON_CODE+"="+actionCode;
			stateFlowss=getStateFlowStepSetup(stateValue, stateFlowID,actionCode,db);
		}else{
			stateFlowss=getStateFlowStepSetup(stateValue, stateFlowID,db);
		}
		
		if(stateFlowss!=null){
			ofp.nextStateValue=stateFlowss.getNextStateValue();
			
			ofp.failureStateVaule=stateFlowss.getFailureStateVaule();
			
			//状态值
			ofp.stateValue=stateFlowss.getStateValue();
			
			ParamList paramList=new ParamList();
			paramList.parse(stateFlowss.getParamList());
			
			ofp.paramList=paramList;
			
			//将otherParam中的参数添加到paramList中
			Iterator<String> itesr=otherParam.keySet().iterator();
			
			while(itesr.hasNext()){
				String key=itesr.next();
				ofp.paramList.add(key, otherParam.get(key));
			}
			
		}
		
		//更新表主键值
		ofp.keyValue=id;
		
		return ofp;
	}
	
	
	/**
	 * 根据流程id和流程状态获取流程状态信息
	 * @param distState 派单流程状态值
	 * @param flowId	状态流程ID
	 * @return
	 */
	public StateFlowStepSetup getStateFlowStepSetup(String distState,String flowId,DB db ){
		
		StateFlowStepSetup result=null;
		
		try{
			String getOrderDistClazzNameSQL="SELECT * FROM mall_StateFlowStepSetup "
					+ "  WHERE stateValue=? AND stateflowsetupid=?    ORDER BY sort ";
			
			MapList map=db.query(getOrderDistClazzNameSQL,
					new String[]{distState,flowId},
					new int[]{Type.VARCHAR,Type.VARCHAR});
			
			if(!Checker.isEmpty(map)){
				result=new StateFlowStepSetup(map);
			}else{
				logger.info("未找到对应的流程情况");
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 根据流程id和流程状态获取流程状态信息
	 * @param distState 派单流程状态值
	 * @param flowId	状态流程ID
	 * @return
	 */
	public StateFlowStepSetup getStateFlowStepSetup(String distState,String flowId,String actioCode,DB db ){
		
		StateFlowStepSetup result=null;
		
		try{
			
			actioCode="%"+actioCode+"%";
			
			String getOrderDistClazzNameSQL="SELECT * FROM mall_StateFlowStepSetup "
					+ "  WHERE stateValue=? AND stateflowsetupid=?  AND ParamList LIKE ?  ORDER BY sort ";
			
			MapList map=db.query(getOrderDistClazzNameSQL,
					new String[]{distState,flowId,actioCode},
					new int[]{Type.VARCHAR,Type.VARCHAR,Type.VARCHAR});
			
			if(!Checker.isEmpty(map)){
				result=new StateFlowStepSetup(map);
			}
			
		}catch(JDBCException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public JSONObject updateStateData(DB db,OrderFlowParam ofp) throws JDBCException, JSONException{
		
		JSONObject result=new JSONObject();
		
		if(!Checker.isEmpty(ofp.nextStateValue)&&!"NULL".equalsIgnoreCase(ofp.nextStateValue)){
			//更新状态表数据到下一个状态
			String updaeSQL="UPDATE "+ofp.tableName+"  SET "+
					ofp.stateFieldName+"='"+
					ofp.nextStateValue+
					"' WHERE "+ofp.keyName+"='"+ofp.keyValue+"'";
			
			db.execute(updaeSQL);
		}
		
		result.put("CODE",1);
		result.put("MSG","更新成功");
		result.put("SUCCESS",true);
		result.put("STATE",ofp.nextStateValue);
		
		return result;
	}
	
	/**
	 * 根据ID获取状态流程
	 * @param id 流通流程ID
	 * @return   状态流程对象
	 * @throws JDBCException 
	 */
	public StateFlowSetup getStateFlowSetupById(String stateFlowID) throws JDBCException{
		
		String querySQL="SELECT id,tablename,statefieldname,keyname "
				+ " FROM mall_StateFlowSetup  WHERE id=? ";
		
		DB db=DBFactory.getDB();
		MapList map=db.query(querySQL,new String[]{stateFlowID},new int[]{Type.VARCHAR});
		
		StateFlowSetup result=new StateFlowSetup(map);
		
		return result;
	}
	
	
}
