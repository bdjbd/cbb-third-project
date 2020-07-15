package com.am.frame.task.taskParameter;

import java.util.HashMap;
import java.util.Map;

import com.am.frame.task.TaskActionParametes;
import com.am.frame.task.UserTaskAbstract;
import com.am.frame.task.memberPerfect.MemberPerfectTask;
import com.am.frame.task.recharge.RechargeTask;
import com.am.frame.task.toPromoteTask.ToPromote;



/**
 * 任务参数管理类
 * @author Administrator
 */
public class TaskParameterManage {

	/**任务参数集合**/
	private Map<String,String> taskParamsSet=new HashMap<String,String>();
	
	public TaskParameterManage(){
		
		/**任务奖励**/
		taskParamsSet.put(UserTaskAbstract.REWARD_BADGE,"com.am.frame.task.taskParameter.entity.RewardBadgeParameter");//奖励徽章
		taskParamsSet.put(UserTaskAbstract.REWARD_ELECT,"com.am.frame.task.taskParameter.entity.ElectTicketParameter");//奖励电子券
		taskParamsSet.put(UserTaskAbstract.REWARD_SCORE,"com.am.frame.task.taskParameter.entity.RewardScoreParameter");//奖励积分
		
		/**添加任务推广任务参数**/
		taskParamsSet.put(ToPromote.FINSH_PROMOTE_NUMBER,"com.am.frame.task.taskParameter.entity.FinshNumberPromoteParameter");//目标推广数量
		taskParamsSet.put(ToPromote.NOW_PROMOTE_NUMBER,"com.am.frame.task.taskParameter.entity.NowNumberPromoteParameter");//已经推广数量
		
		
		//添加替他任务参数
		
		
		/**任务奖励**/
		taskParamsSet.put(UserTaskAbstract.REWARD_BADGE,"com.am.frame.task.taskParameter.entity.RewardBadgeParameter");//奖励徽章
		taskParamsSet.put(UserTaskAbstract.REWARD_ELECT,"com.am.frame.task.taskParameter.entity.ElectTicketParameter");//奖励电子券
		taskParamsSet.put(UserTaskAbstract.REWARD_SCORE,"com.am.frame.task.taskParameter.entity.RewardScoreParameter");//奖励积分
		taskParamsSet.put(UserTaskAbstract.REWARD_CASH,"com.am.frame.task.taskParameter.entity.RewardCashParameter");//奖励 现金
		
		
		/**添加任务推广任务参数**/
		taskParamsSet.put(ToPromote.FINSH_PROMOTE_NUMBER,"com.am.frame.task.taskParameter.entity.FinshNumberPromoteParameter");//目标推广数量
		taskParamsSet.put(ToPromote.NOW_PROMOTE_NUMBER,"com.am.frame.task.taskParameter.entity.NowNumberPromoteParameter");//已经推广数量
		
		
		/**添加会员信息完善任务参数**/
		taskParamsSet.put(MemberPerfectTask.PERFECT_MEMBER_INFO,"com.am.frame.task.taskParameter.entity.MemberPerfectParameter");
		taskParamsSet.put(MemberPerfectTask.PERFECT_MEMBER_INFO_COMPLETE,"com.am.frame.task.taskParameter.entity.MemberInfoCompleteParameter");
		
		/**添加会员充值任务参数**/
		taskParamsSet.put(RechargeTask.RECHARGE_CASH,"com.am.frame.task.taskParameter.entity.RechargeCashParameter");
		taskParamsSet.put(RechargeTask.IS_RECHARGE,"com.am.frame.task.taskParameter.entity.IsRechargeCashParameter");//是否充值
		
		//添加替他任务参数
		
	}
	
	/**
	 * 获取参数的HTML
	 * @param parameterName
	 * @param parameterValue
	 * @param task
	 * @return
	 */
	public String getHtml(String parameterName, String parameterValue,TaskActionParametes taskActionParam) {
		
		ITaskParameterGetHtml html=(ITaskParameterGetHtml)createParams(parameterName);
		//获取html代码
		return html.getHtml(parameterName, parameterValue, taskActionParam);
	}
	
	
	/**
	 * 根据参数名称构造参数类
	 * @param parameterName 参数名称
	 * @return 参数类
	 */
	private Object createParams(String parameterName){
		Object result=null;
		
		parameterName=processParameteName(parameterName);
		
		String clazzName=taskParamsSet.get(parameterName);
		try {
			result=Class.forName(clazzName).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 执行任务奖励
	 * @param parameterName  参数名
	 * @param parameterValue 参数值
	 * @param task 任务
	 */
	public void executeReward(String parameterName,String parameterValue,TaskActionParametes taskActioParams){
		ITaskParameterReward reward=(ITaskParameterReward)createParams(parameterName);
		
		reward.executeRewar(parameterName, parameterValue,taskActioParams );
	}
	
	
	/**
	 * 处理任务参数。 有多个任务参数时，任务名称后面加#和编号来区分不同的参数。如：<br>
	 * 奖励电子券#1=电子券1id;奖励电子券#2=电子券2id;奖励电子券#3=电子券3id;
	 * @param parameterName 任务参数名称
	 * @return 处理后的任务参数名称
	 */
	private String processParameteName(String parameterName){
		
		String result="";
		
		if(parameterName.contains("#")){
			result=parameterName.split("#")[0];
		}else{
			result=parameterName;
		}
		
		return result;
	}
}
