package com.p2p.task.taskParameter;

import java.util.HashMap;
import java.util.Map;

import com.p2p.task.TaskActionParametes;
import com.p2p.task.UserTaskAbstract;
import com.p2p.task.toPromoteTask.ToPromote;


/**
 * 任务参数管理类
 * @author Administrator
 */
public class TaskParameterManage {

	/**任务参数集合**/
	private Map<String,String> taskParamsSet=new HashMap<String,String>();
	
	public TaskParameterManage(){
		
		/**任务奖励**/
		taskParamsSet.put(UserTaskAbstract.REWARD_BADGE,"com.p2p.task.taskParameter.entity.RewardBadgeParameter");//奖励徽章
		taskParamsSet.put(UserTaskAbstract.REWARD_ELECT,"com.p2p.task.taskParameter.entity.ElectTicketParameter");//奖励电子券
		taskParamsSet.put(UserTaskAbstract.REWARD_SCORE,"com.p2p.task.taskParameter.entity.RewardScoreParameter");//奖励积分
		
		/**添加任务推广任务参数**/
		taskParamsSet.put(ToPromote.FINSH_PROMOTE_NUMBER,"com.p2p.task.taskParameter.entity.FinshNumberPromoteParameter");//目标推广数量
		taskParamsSet.put(ToPromote.NOW_PROMOTE_NUMBER,"com.p2p.task.taskParameter.entity.NowNumberPromoteParameter");//已经推广数量
		
		
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
		return html.getHtml(parameterName, parameterValue, taskActionParam);
	}
	
	
	private Object createParams(String parameterName){
		Object result=null;
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
}
