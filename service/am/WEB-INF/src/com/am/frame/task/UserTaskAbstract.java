package com.am.frame.task;

import com.am.frame.task.taskParameter.TaskParameterManage;
import com.am.frame.task.taskParameter.TaskParameterSet;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 用户任务抽象类
 * @author Administrator
 *
 */
public abstract class UserTaskAbstract implements IUserTask {
	
	public static final String tag="UserTaskAbstract";
	
	/**
	 * 任务说明参数集合
	 */
	protected TaskParameterSet taskExplainParams=new TaskParameterSet(); 
	/**
	 * 任务进度完成情况参数集合
	 */
	protected TaskParameterSet taskProgressParams=new TaskParameterSet();
	/**
	 *任务参数管理者
	 */
	protected TaskParameterManage paramManager=new TaskParameterManage();
	/**用户ID**/
	protected String userId="";
	/**任务ID**/
	protected String taskId="";
	/**推广码**/
	protected String promotionCode="";
	/**任务状态  默认为1 ，未完成，0 完成**/
	protected String taskRunState="1";
	
	/**属性动作参数**/
	protected TaskActionParametes taskActioParams;
	
	/** 奖励 积分**/
	public static final String REWARD_SCORE = "奖励积分";
	/**奖励电子券**/
	public static final String REWARD_ELECT = "奖励电子券";
	/**奖励徽章**/
	public static final String REWARD_BADGE= "奖励徽章";
	/**奖励现金**/
	public static final String REWARD_CASH="奖励现金";
	
	/**
	 * 初始化任务参数
	 */
	@Override
	public void init(String memberCode, String taskId) 
	{
		this.userId = memberCode;
		this.taskId = taskId;
		
		//清除参数数据
		taskExplainParams.clear();
		taskProgressParams.clear();
		
		try 
		{
			DB db = DBFactory.getDB();
			
			String memberInfoSQL="SELECT * FROM am_Member WHERE id='"+this.userId+"'";
			
			MapList memberMap=db.query(memberInfoSQL);
			
			if(!Checker.isEmpty(memberMap)){
				this.promotionCode=memberMap.getRow(0).get("promotioncode");
			}
			
			String sql = "SELECT * FROM am_usertask WHERE memberid='"+ userId + "' AND id='" +taskId + "'";
			MapList map = db.query(sql);
			
			if (!Checker.isEmpty(map))
			{
				//获取任务状态
				this.taskRunState=map.getRow(0).get("taskrunstate");
				
				//获取任务参数   任务参数格式: 目标 奖励 进度  
				// 1,目标推广人数=2;奖励积分=10;|已推广人数=0; 
				// 2,目标推广人数=2;奖励电子卷=电子券编码;|已推广人数=0;  
				// 3,目标推广人数=2;奖励徽章=徽章编码;|已推广人数=0;
				// 4,完善会员信息|奖励积分=10
				String parame = map.getRow(0).get("taskparame");
				
				if(!Checker.isEmpty(parame))
				{
					
					//检查是否包含说明和奖励分隔符
					if(parame.contains(TASK_EXPLAIN_PROGRESS_DECOLLATOR))
					{
						
						String[] params=parame.split("\\"+TASK_EXPLAIN_PROGRESS_DECOLLATOR);
						
						if(!Checker.isEmpty(params)&&!Checker.isEmpty(params[0]))
						{//任务描述说明
							//解析任务说明参数到任务说明参数集合
							parseParameter(params[0],this.taskExplainParams);
						}
						
						if(!Checker.isEmpty(params)&&params.length>1&&!Checker.isEmpty(params[1]))
						{//任务进度
							//解析任务进度参数到任务进度参数集合
							parseParameter(params[1],this.taskProgressParams);
						}
					}else{
						//不存在任务进度分隔符
						if(!Checker.isEmpty(parame))
						{//任务描述说明
							//解析任务说明参数到任务说明参数集合
							parseParameter(parame,this.taskExplainParams);
						}
					}
				}
			}
			
			//构造属性参数
			builderActionParams();
			
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getHtml()
	{
		String html="【任务说明】<br>";
		html+=getParamSetHtml(taskExplainParams);
		
		html+="<br>【任务完成情况】<br>";
		html+=getParamSetHtml(taskProgressParams);
		
		return html;
	}

	/**
	 * 解析任务参数到指定的参数集合，<br>
	 * 参数格式：  name1=value1;name2=value2;...
	 * @param params 任务参数
	 * @param paramsSet 参数集合
	 */
	protected void parseParameter(String params,TaskParameterSet paramsSet){
		if(!Checker.isEmpty(params))
		{
			String[] ps=params.split(TASK_PARAMTERE_DECOLLATOR);
			
			String[] kv;
			for(int i=0;i<ps.length;i++)
			{
				kv=ps[i].split("=");
				paramsSet.add(kv[0],kv.length>1?kv[1]:"");
			}
		}
	}
	
	/**
	 * 获取参数集合的html
	 * @param parameterSet
	 * @return
	 */
	protected String getParamSetHtml(TaskParameterSet parameterSet){
		
		String html="";
		
		for(int i=0;i<parameterSet.size();i++)
		{
			html+=paramManager.getHtml(parameterSet.getName(i),parameterSet.getValue(i),taskActioParams);
			html+="<br>";
		}
		
		return html;
	}

	/**
	 * 完成任务，执行任务奖励，此任务中已经判断任务是否完成  ，0 已经完成，1，未完成
	 */
	protected void executeReward() {
		//任务未完成，执行奖励
		for(int i=0;i<this.taskExplainParams.size();i++){
			paramManager.executeReward(taskExplainParams.getName(i),taskExplainParams.getValue(i), taskActioParams);
		}
	}
	
	/**
	 * 更新任务参数
	 */
	protected void saveTaskParams(){
		try{
			//任务参数格式还原
			String taskParameterStr=taskExplainParams.toString()
					+TASK_EXPLAIN_PROGRESS_DECOLLATOR
					+taskProgressParams.toString();
			
			// 保存任务参数到数据库SQL
			String updateSQL="UPDATE am_usertask   "
					+ " SET "
					+ " taskrunstate='"+this.taskRunState+"', " 
					+ " taskparame='"+taskParameterStr+"' " 
					+ " WHERE id='"+this.taskId+"'";
			
			DB db=DBFactory.getDB();
			
			db.execute(updateSQL);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取用户ID
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取任务ID
	 * @return
	 */
	public String getTaskId() {
		return taskId;
	}
	
	/**
	 * 构造属性参数
	 * @return
	 */
	protected void builderActionParams() {
		taskActioParams=new TaskActionParametes();
		taskActioParams.memberCode=this.userId;
		taskActioParams.taskID=this.taskId;
		taskActioParams.taskRunState=this.taskRunState;
	}
	
}
