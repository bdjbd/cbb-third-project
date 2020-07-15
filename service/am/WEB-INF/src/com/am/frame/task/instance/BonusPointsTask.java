package com.am.frame.task.instance;

import org.jfree.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.am.mall.beans.order.MemberOrder;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月17日
 * @version 
 * 说明:<br />
 * 消费积分奖励
 * 奖励任务参数
{
"TaskDescription" : "{}",
"TaskObject" : {
"type":"ratio/range",
"taskRange":[
{"range":""},{"range":""}
],
"taskRatio":0.01
},
"TaskReward" : [
{"rewName":"奖励规则名称","rewardParams":{}}],
"TaskExecution":{}}
 */
public class BonusPointsTask extends AbstractTask {
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception {
		boolean result=false;
		
		//会员订单ID
		String memberOrderId=runTaskParams.getMemberOrderId();
		String querOrderSQL="SELECT * FROM mall_memberorder WHERE id=? ";
		
		MapList orderMap=db.query(querOrderSQL,memberOrderId,Type.VARCHAR);
		
		MemberOrder order=null;
		
		if(!Checker.isEmpty(orderMap)){
			order=new MemberOrder(orderMap.getRow(0));
		}
		
		//根据任务目标判断任务是否需要奖励
		result=judgmentRewardValue(db,order,runTaskParams);
			
		return result;
	}
	
	/***
	 * 根据任务目标判断任务是否需要奖励
	 * @param db
	 * @param order
	 * @param runTaskParams
	 * @return
	 */
	private boolean judgmentRewardValue(DB db, MemberOrder order,
			RunTaskParams runTaskParams)throws Exception {
		boolean result=false;
		
		//先判断奖励形式
		if(this.taskparames==null){
			Log.error("任务参数配置不正确，请检查任务参数！");
			return false;
		}
		JSONObject taskObject=this.taskparames.getTaskObject();
		
		if(taskObject==null||taskObject.get("type")==null){
			throw new Exception("任务参数配置不正确，请检查任务参数！");
		}
		
		//任务奖励类型  ratio/range
		if("ratio".equals(taskObject.get("type"))){
			//按照订单比例计算积分
			runTaskParams.getTargetMemberList().add(runTaskParams.getMemberId());
			double taskRatio=taskObject.getDouble("taskRatio");
			
			//根据积分计算奖励积分值
			runTaskParams.pushParam(REWARD_VALUE,taskRatio*order.getTotalPrice());
			
			result=true;
		}
		if("range".equals(taskObject.get("type"))){
			//根据购买区间奖励
			JSONArray range=taskObject.getJSONArray("taskRange");
			
			//[{"min":1,"max":20,value:""}]
			double totalPrice=order.getTotalPrice();
			for(int i=0;i<range.length();i++){
				JSONObject item=range.getJSONObject(i);
				
				//小于等于最大值，大于最小值 则在次区间内，进行奖励
				if(totalPrice<=item.getDouble("max")
						&&totalPrice>item.getDouble("min")){
					
					runTaskParams.getTargetMemberList().add(runTaskParams.getMemberId());
					runTaskParams.pushParam(REWARD_VALUE,item.get("value"));
					result=true;
					break;
				}
			}
		}
		
		return result;
	}

	
	
}
