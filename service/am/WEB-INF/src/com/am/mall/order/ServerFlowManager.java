package com.am.mall.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.webapi.db.DBManager;
import com.am.mall.beans.commodity.Commodity;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.beans.order.StateFlowSetup;
import com.am.mall.beans.order.StateFlowStepSetup;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月14日
 * @version 
 * 说明:<br />
 * 服务订单流程管理类
 * 
 */
public class ServerFlowManager {
	
	private static ServerFlowManager serverFlowManager;
	
	private ServerFlowManager(){}
	
	public static ServerFlowManager getInstance(){
		if(serverFlowManager==null){
			serverFlowManager=new ServerFlowManager();
		}
		return serverFlowManager;
	}
	
	
	//获得该表可用的所有状态流程
		Map<String,String> getSupeportFlow(String tablename,String StateFieldName){
			return null;
		} 

		/**
		 * 设置首流程状态值，并不调用状态值实现类,
		 * @param stateFlowID 状态流程ID
		 * @param id 订单ID
		 * @return 返回当前的状态值 {CODE:value,MSG:valu,STATE:value}
		 */
		public String setFirstState(String stateFlowID,String orderId){
			
			//返回值
			JSONObject result=new JSONObject();
			
			try{
				//获取流程
				StateFlowSetup flow=getStateFlowSetupById(stateFlowID);
				//首流程状态
				StateFlowStepSetup flowFirstState=getFirstStateFlowStepSetup(stateFlowID);
				
				result.put("CODE", 
						updateTableState(flow.getTableName(),flow.getStateFieldName(), flowFirstState.getStateValue(),flow.getKeyName(),orderId));
				
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
		 * 系统自动实例化StateValue的实现类并执行 
		 * 系统默认实现类，将StateValue的NextStateValue设置到id记录上
		 * @param StateFlowID 状态流程ID
		 * @param currentStateValue 当前流程值
		 * @param id 更新数据的ID
		 * @param OtherParam 其他参数
		 * @return return 0=失败；1=成功；当前的状态值 {CODE:value,MSG:valu,STATE:value}
		 */
		String setNextState(String stateFlowID,String currentStateValue,String id,Map<String,String> OtherParam){
			
			//返回值
			JSONObject result=new JSONObject();
			
			try{
				//获取流程
				StateFlowSetup flow=getStateFlowSetupById(stateFlowID);
				//获取流程当前状态对于的流程状态参数
				StateFlowStepSetup flowState=getStateFlowStepSetupByID(currentStateValue,stateFlowID);
				//执行状态类来更新状态；
				//updateFlowState(flow,flowState);
				
			}catch(Exception e){
				try {
					result.put("MSG",e.getMessage());
					result.put("CODE","-1");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
			return result.toString();
		}

		/**
		 * 得到该状态值的下一状态值集合
		 * @param StateFlowID 状态流程ID
		 * @param StateValue
		 * @return
		 */
		String[] getStateNexts(String StateFlowID,String StateValue){
			return null;
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
			
			DBManager db=new DBManager();
			
			MapList map=db.query(querySQL,new String[]{stateFlowID},new int[]{Type.VARCHAR});
			
			StateFlowSetup result=new StateFlowSetup(map);
			
			return result;
		}
		
		
		/**
		 * 获取流程状态
		 * @param value 流程状态值
		 * @param id 状态流程ID(流程ID)
		 * @return
		 * @throws JDBCException 
		 */
		public StateFlowStepSetup getStateFlowStepSetupByID(String value,String flowId) throws JDBCException{
			
			String getStepSQL="SELECT * FROM mall_StateFlowStepSetup  WHERE statevalue=?  AND stateflowsetupid=? ";
			
			DBManager db=new DBManager();
			MapList stepMap=db.query(getStepSQL,new String[]{value,flowId},new int[]{Type.VARCHAR,Type.VARCHAR});
			
			StateFlowStepSetup result=new StateFlowStepSetup(stepMap);
			
			return result;
		}
		
		
		/**
		 * 获取流程状态
		 * @param value 流程状态值
		 * @param id 状态流程ID(流程ID)
		 * @return
		 * @throws JDBCException 
		 */
		public StateFlowStepSetup getStateFlowStepSetupByID(String flowId) throws JDBCException{
			
			String getStepSQL="SELECT * FROM mall_StateFlowStepSetup  WHERE stateflowsetupid=? ";
			
			DBManager db=new DBManager();
			
			MapList stepMap=db.query(getStepSQL,new String[]{flowId},new int[]{Type.VARCHAR});
			
			StateFlowStepSetup result=new StateFlowStepSetup(stepMap);
			
			return result;
		}
		
		
		/**
		 *根据流程ID获取流程状态
		 * @param flowId 状态流程ID
		 * @return 流程状态集合
		 * @throws JDBCException 
		 */
		public List<StateFlowStepSetup> getStateFlowStepSetups(String flowId) throws JDBCException{
			String getStepSQL="SELECT * FROM mall_StateFlowStepSetup  WHERE  stateflowsetupid=?  ORDER BY sort ";
			
			DBManager db=new DBManager();
			
			MapList stepMap=db.query(getStepSQL,new String[]{flowId},new int[]{Type.VARCHAR});
			
			List<StateFlowStepSetup> result=new ArrayList<StateFlowStepSetup>();
			
			for(int i=0;i<stepMap.size();i++){
				StateFlowStepSetup sfss=new StateFlowStepSetup(stepMap.getRow(i));
				result.add(sfss);
			}
			
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
			
			DBManager db=new DBManager();
			
			MapList map=db.query(sql,new String[]{flowId},new int[]{Type.VARCHAR});
			
			result=new StateFlowStepSetup(map);
			
			return result;
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

			DBManager db=new DBManager();
			
			//更新流程的首流程状态
			String updateSQL="UPDATE "+tableName+" SET "+stateFiledName+"=? WHERE "+keyName+"=?";
			result=db.execute(updateSQL, 
					new String[]{stateValue,keyValue}, 
					new int[]{Type.VARCHAR,Type.VARCHAR});
			
			return result;
		}
		
		
		/**
		 * 获取流程下状态值
		 * @param rrp
		 * @return
		 * @throws Exception
		 */
		public String[] getNextValues(OrderFlowParams rrp,DB db)throws Exception{
			
			String[] nextStateValues=null;
			// 查询当前流程状态信息 SQL
			String sql = "SELECT * FROM mall_StateFlowStepSetup  WHERE stateflowsetupid=? AND statevalue=?";

			MapList map = db.query(sql, new String[] { rrp.currentFlowId,
						rrp.currentKeyValue }, new int[] { Type.VARCHAR,
						Type.VARCHAR });

			if (!Checker.isEmpty(map)) {

				String nextStateValue = map.getRow(0).get("nextstatevalue");
				nextStateValues = new String[] {};

				// 检查是否有下一流程状态，如果有，直接更新，否则，不修改
				if (!Checker.isEmpty(nextStateValue)&& !"null".equalsIgnoreCase(nextStateValue)) {
				// 将下一流程状态分隔
					nextStateValues = nextStateValue.split(",");
				}
			}
			
			return nextStateValues;
		}

		
		
		/***
		 * 执行当前流程的流程Action
		 * @param ofp
		 * @return
		 * @throws JDBCException 
		 */
		public String executeFlowAction(MemberOrder order,Commodity comdity) throws Exception {
			
			String result="";
			
			DBManager db=new DBManager();
			
			//查询当前流程的配置类
			StateFlowStepSetup sfss=getStateFlowStepSetup(order.getOrderState(),comdity.getOrderStateID());

			//构造接口参数
			OrderFlowParams param=builderParamList(order, comdity, db);
			
			//反射执行
			String orderDistClazzName=sfss.getClassPath();
			
			IFlowState orderDist=null;
			
			System.err.print("反射执行 订单执行类:"+orderDistClazzName);
			
			if(orderDistClazzName!=null){
				orderDist=(IFlowState)Class.forName(orderDistClazzName).newInstance();
				result=orderDist.execute(param);
			}
			
			return result;
		}
		
		
		
		
		/**
		 * 根据流程id和流程状态获取流程状态信息
		 * @param distState 派单流程状态值
		 * @param flowId	状态流程ID
		 * @return
		 */
		public StateFlowStepSetup getStateFlowStepSetup(String distState,String flowId ){
			
			StateFlowStepSetup result=null;
			
			try{
				String getOrderDistClazzNameSQL="SELECT * FROM mall_StateFlowStepSetup "
						+ "  WHERE stateValue=? AND stateflowsetupid=?  ";
				
				DBManager db=new DBManager();
				
				MapList map=db.query(getOrderDistClazzNameSQL,
						new String[]{distState,flowId},
						new int[]{Type.VARCHAR,Type.VARCHAR});
				
				if(!Checker.isEmpty(map)){
					result=new StateFlowStepSetup(map);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return result;
		}
		
		
		/**
		 * 构造OrderFlowParam参数
		 * @param order 订单，
		 * @param commodity 商品
		 * @return
		 * @throws JDBCException 
		 */
		public OrderFlowParams builderParamList(MemberOrder order,Commodity commodity,DB db) throws JDBCException{
			
			OrderFlowParams ofp=new OrderFlowParams();
			
			ofp.currentFlowId=commodity.getOrderStateID();
			ofp.currentFlowStateId=commodity.getDistStateID();
			ofp.keyValue=order.getId();
			ofp.currentKeyValue=order.getOrderState();
			ofp.orderId=order.getId();
			
			String currentFlowSQL="SELECT * FROM mall_StateFlowSetup WHERE id=? ";
			
			MapList map=db.query(currentFlowSQL,ofp.currentFlowId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				
				ofp.keyName=map.getRow(0).get("keyname");
				ofp.tableName=map.getRow(0).get("tablename");
				ofp.stateFiledName=map.getRow(0).get("statefieldname");
			}
			
			return ofp;
		}
		
		/**
		 * 构造OrderFlowParam参数
		 * @param order 订单，
		 * @param commodity 商品
		 * @return
		 * @throws JDBCException 
		 */
		public OrderFlowParams builderParamList(MemberOrder order,Commodity commodity,DBManager db) throws JDBCException{
			
			OrderFlowParams ofp=new OrderFlowParams();
			
			ofp.currentFlowId=commodity.getOrderStateID();
			ofp.currentFlowStateId=commodity.getDistStateID();
			ofp.keyValue=order.getId();
			ofp.currentKeyValue=order.getOrderState();
			ofp.orderId=order.getId();
			
			String currentFlowSQL="SELECT * FROM mall_StateFlowSetup WHERE id=? ";
			
			MapList map=db.query(currentFlowSQL,ofp.currentFlowId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				
				ofp.keyName=map.getRow(0).get("keyname");
				ofp.tableName=map.getRow(0).get("tablename");
				ofp.stateFiledName=map.getRow(0).get("statefieldname");
			}
			
			return ofp;
		}
		
}
