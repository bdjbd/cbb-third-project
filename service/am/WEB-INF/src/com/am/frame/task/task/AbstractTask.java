package com.am.frame.task.task;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.params.UserTaskParams;
import com.am.mall.reward.IRewardRule;
import com.am.mall.reward.RewardRuleParam;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * 用户任务抽象类
 * @author Administrator
 *
 *任务参数格式
	 {"TaskDescription" : "{}",
	"TaskObject" : "{}",
	"TaskReward" : [{"rewName":"规则关键字，在数据库中保存MALL_REWARDRULESETUP","奖励参数":{}},
	{"rewName":"规则关键字，在数据库中保存MALL_REWARDRULESETUP","奖励参数":{}},
	{"rewName":"规则关键字，在数据库中保存MALL_REWARDRULESETUP","奖励参数":{}}
	],
	"TaskExecution":{"目标推广人数" : "10","已推广人数" : "0"}}
 *
 */
public abstract class AbstractTask  implements ITask {
	
	protected  Logger logger=LoggerFactory.getLogger(getClass());
	
	/**VIEW显示任务目标JSONObject 对象集合KEY**/
	public static String Task_Show_Target="taskShowTarget";
	
	/**VIEW显示任务当前进度对象JSONObject 集合KEY    taskCurrentProcess**/
	public static String Task_Current_Process="taskCurrentProcess";
	
	/**
	 * 用户任务表信息
	 *   taskrunstate character varying(2), 用户任务完成状态未完成=1/完成=0；
	 *   					默认为1，新任务即为1；
	 *   					任务完成条件达成时=0，该状态在更新用户任务数据
	 *   					时判断条件达成后更新 
	 *   memberid character varying(36),  会员id
	 *   taskparame character varying(2000), -- 任务参数
	 *   createdate date, -- 建立时间
	 *   id character varying(36) NOT NULL, 任务id
	 *   entertaskid  企业任务id
	 */
	public static final String tag="AbstractTask";
	
	/**任务状态，0：不再执行任务，1:任务可以继续执行**/
	protected String taskRunState="1";
	/**任务名称**/
	protected String taskName;
	
	protected String memberId;
	/**用户任务参数**/
	protected UserTaskParams taskparames;
	/**企业任务id**/
	protected String entertaskid;
	/**am_usertask id 用户任务id**/
	protected String id;
	
	/**任务view参数**/
	protected String taskSetInterface;
	
	
	protected String taskClassPath;
	/**任务相关信息，查询sql为：
	 * SELECT etu.tasktemplatetype,etu.classpath,ut.* FROM am_usertask AS ut 
	 *  LEFT JOIN am_enterprisetask AS eu ON ut.entertaskid=eu.id
	 *   LEFT JOIN am_tasktemplate AS etu ON eu.tasktemplateid=etu.id 
	 *   WHERE etu.classpath='"+taskClassName+"' 
	 *    AND ut.memberid='"+memberId+"'
	 * **/
	protected Row taskRow;
	
	/***
	 * 执行更新用户数据操作
	 * @param strJson  
	 * @return  返回结果，如果返回true，表示执行奖励，
	 * 			如果返回false，表示不执行奖励
	 * 
	 */
	public abstract boolean updateUserTaskData(RunTaskParams runTaskParams,DB db)throws Exception;
	
	//任务奖励配置map
	protected Map<String,Row> rewardMap=new HashMap<String, Row>();
	
	
	/**
	 * 初始化任务  初始化任何，获取任务信息
	 */
	@Override
	public void initTask(RunTaskParams params) {
		String taskClassName=params.getTaskClassName();
		String memberId=params.getMemberId();
		
		//根据任务类路径和类会员id，查询任务信息
		String getTaskSQL="SELECT etu.tasktemplatetype,etu.classpath,ut.* ,"+
						" eu.name AS taskName,eu.tasksetinterface "+
						" FROM am_usertask AS ut "+
						" LEFT JOIN am_enterprisetask AS eu ON ut.entertaskid=eu.id "+
						" LEFT JOIN am_tasktemplate AS etu ON eu.tasktemplateid=etu.id "+
						" WHERE etu.classpath='"+taskClassName+"' "+
						"   AND ut.memberid='"+memberId+"'";
		DB db=null;
		
		try {
			db=DBFactory.newDB();
			MapList taskMap=db.query(getTaskSQL);
			
			if(!Checker.isEmpty(taskMap)){
				taskRow=taskMap.getRow(0);
				this.entertaskid=taskRow.get("entertaskid");
				this.memberId=memberId;
				this.taskRunState=taskRow.get("taskrunstate");
				this.id=taskRow.get("id");
				this.taskClassPath=taskRow.get("classpath");
				this.taskName=taskRow.get("taskname");
				this.taskSetInterface=taskRow.get("tasksetinterface");
				
				this.taskparames=new UserTaskParams(taskRow.get("taskparame"));
			}
			
			//获取奖励规则信息
			String getReawardRuleSQL="SELECT * FROM mall_rewardrulesetup";
			
			MapList rewardRuleMap=db.query(getReawardRuleSQL);
			
			if(!Checker.isEmpty(rewardRuleMap)){
				for(int i=0;i<rewardRuleMap.size();i++){
					Row row=rewardRuleMap.getRow(i);
					rewardMap.put(row.get("name"),row);
				}
			}
		} catch (JDBCException e) {
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
	}
	
	@Override
	public boolean execute(RunTaskParams params) {
		
		DB db=null;
		try{
			db=DBFactory.newDB();
			//更新用户任务数据 如果返回true，表示执行奖励，返回false不执行奖励
			if(updateUserTaskData(params,db)){
				
				//1，检查任务是否为一次性任务，如果是一次性任务或者日常任务，需要检查任务状态，
				//如果任务状态为完成，则，不执行奖励，如果没有完成，则执行奖励
				// taskrunstate 未完成=1/完成=0； 
				
				// tasktemplatetype 任务类型 0：日常任务；1：一次性任务；2：累计任务
				String tasktemplatetype = "";
				if(taskRow.size()>0)
				{
					tasktemplatetype=taskRow.get("tasktemplatetype");
				}
				 
				
				if("0".equals(tasktemplatetype)||"1".equals(tasktemplatetype)){
					
					if("1".equals(taskRow.get("taskrunstate"))){
						logger.info("非累积性任务，tasktemplatetype："+tasktemplatetype+",任务未完成，执行奖励。");
						executeReward(params,db);
						//2，如果是一次性任务，或者是日常任务，则在执行奖励后需要将任务状态修改成已完成
						updateTaskToFinished(params,db);
					}else{
						logger.info("非累积性任务，tasktemplatetype："+tasktemplatetype+",任务已完成，不执行奖励。");
					}
				}else{
					logger.info(taskName+"累计任务，执行奖励");
					executeReward(params,db);
				}
			}
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
		return false;
	}
	
	/**
	 * 更新任务状态
	 * @param params
	 */
	private void updateTaskToFinished(RunTaskParams params,DB db)throws Exception {
		String udpateSQL="UPDATE am_usertask SET taskrunstate=0 WHERE id=? ";
		
		db.execute(udpateSQL,this.id,Type.VARCHAR);
		
		logger.info("任务完成，更新任务状态.");
		
	}
	
	/**
	 * 执行奖励
	 * @param params
	 */
	protected void executeReward(RunTaskParams params,DB db){
		if(this.taskparames!=null){
			for(int i=0;i<taskparames.getRewardParams().size();i++){
				
				JSONObject rewParams=taskparames.getRewardParams().get(i);
				try {
					String rewName=rewParams.getString("rewName");
					
					if(Checker.isEmpty(rewName)){
						continue;
					}
					
					Row rewardRow=rewardMap.get(rewName);
					
					if(rewardRow==null){
						logger.info(rewName+"没有对应的奖励规则");
						return ;
					}
					
					String clazzName=rewardRow.get("classpath");
					
					Object rewardObj=Class.forName(clazzName).newInstance();
					
					//奖励有两类  商品购买奖励和任务奖励，
					//在此处，商品类型的奖励也可以在任务中配置使用
					if(rewardObj instanceof ITaskReward){
						// 任务奖励
						logger.info(this.taskName+"执行任务奖励");
						ITaskReward reward=(ITaskReward)rewardObj;
						//设置任务奖励执行参数
						params.setTask(this);
						params.setRewParams(rewParams);
						//执行任务奖励
						reward.execute(params);
					}
					if(rewardObj instanceof IRewardRule){
						
						//商品购买奖励，商品购买奖励任务必须要有订单ID
						logger.info(this.taskName+"商品购买奖励");
						IRewardRule reward=(IRewardRule)rewardObj;
						List<String> members=params.getTargetMemberList();
						for(int j=0;j<members.size();j++){
							RewardRuleParam rrp=new RewardRuleParam();
							rrp.memberId=members.get(j);
							rrp.orderId=params.getMemberOrderId();
							
							if(!Checker.isEmpty(rrp.orderId)){
								logger.info(this.taskName+"中无订单ID，无法执行商品奖励");
							}else{
								reward.execute(rrp);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

	/**
	 * 得到任务变量
	 */
    @Override
	public String getTaskJson(String taskID) {
    	
    	DB db = null;
    	
    	ResultSet rst = null;
    	
    	/**
    	 * 返回值json格式
    	 */
    	JSONObject retJson = null;
    
    	String sql = "SELECT TASKPARAME FROM　AM_USERTASK WHERE ID='"+taskID+"'";
    	
    	try {
    		db = DBFactory.newDB();
    		rst=db.getResultSet(sql);
    		
    		retJson= TaskUtil.getInstance().resultSetToJSON(rst);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		  if(db!=null){
			  try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		  }
		}
    	
    	return retJson.toString();
	}

    /**
     * 得到任务HtmlView模板
     * 此功能使用htmltemplate完成
     */
    @Override
	public String getTaskHtmlView(String taskID){
    	//任务模板
		//查询数据对象
    	logger.info("taskparames:"+this.taskparames.toString());
    	
    	String viewHtmlTemplate=this.taskSetInterface;
    	
    	//1,获取任务模板
    	//2,获取当前任务进度信息
    	//3,在进度中用 TaskExecution.taskCurrentProcess对象中的每个item的KEY替换成VALUE
    	try{
    		if(viewHtmlTemplate!=null&&!Checker.isEmpty(viewHtmlTemplate)){
    			
    			viewHtmlTemplate=StringEscapeUtils.unescapeHtml4(viewHtmlTemplate);
    			
    			//当前进度
    			if(this.taskparames.getTaskExecution()!=null&&this.taskparames.getTaskExecution().has(Task_Current_Process)){
    				JSONObject curretnProces = this.taskparames.getTaskExecution().getJSONObject(Task_Current_Process);
            		
            		if(curretnProces!=null){
            			Iterator<String> iter=curretnProces.keys();
                		
                		while(iter.hasNext()){
                			String key=iter.next();
                			
                			String value=curretnProces.getJSONObject(key).getString("VALUE");
                			
                			viewHtmlTemplate=viewHtmlTemplate.replaceAll("\\$am\\{"+key+"\\}",value);
                		}
            		}
    			}
        		
        		
        		//替换模板中的任务任务目标参数
        		if(this.taskparames.getTaskObject()!=null&&this.taskparames.getTaskObject().has(Task_Show_Target)){
        			JSONObject showTarget = this.taskparames.getTaskObject().getJSONObject(Task_Show_Target);
            		if(showTarget!=null){
            			Iterator<String> iter=showTarget.keys();
                		
                		while(iter.hasNext()){
                			String key=iter.next();
                			
                			String value=showTarget.getJSONObject(key).getString("VALUE");
                			
                			viewHtmlTemplate=viewHtmlTemplate.replaceAll("\\$am_tg\\{"+key+"\\}",value);
                		}
            		}
        		}
        		
        		
        		//替换任务名称 $am{taskName}
        		viewHtmlTemplate=viewHtmlTemplate.replaceAll("\\$am\\{taskName\\}",this.taskName);
        		
        		//替换任务描述 taskDesc
        		if(this.taskparames.getTaskDescription()!=null&&this.taskparames.getTaskDescription().has("content")){
        			String conent=this.taskparames.getTaskDescription().getString("content");
            		viewHtmlTemplate=viewHtmlTemplate.replaceAll("\\$am\\{taskDesc\\}",conent);
        		}
        		
        	}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	logger.info("getView html：\n"+viewHtmlTemplate);
    	
    	return viewHtmlTemplate;
	}
    
    /****
     * 给用户初始化任务
     * @param memberId 会员id
     * @param entTaskId 企业任务idO
     * @return
     * @throws JDBCException 
     */
    @Override
	public boolean addTaskByUserId(String memberId,String entTaskId,DB db) throws JDBCException{
    	boolean result=false;
    	
    	String taskUuid=UUID.randomUUID().toString();
    	
		String insertTaskSQL="INSERT INTO am_usertask( "+
		           " taskrunstate, memberid, taskparame, createdate, id, entertaskid) "+
		           " VALUES ('1','"+memberId+"',("
		           	+ " SELECT taskparame FROM am_enterprisetask "
		           	+ " WHERE id='"+entTaskId+"')"+
		           " , now(),'"+taskUuid+"','"+entTaskId+"') ";
		    	
    	db.execute(insertTaskSQL);
    	result=true;
    	
    	return result;
    }
    
    
	public String getTaskRunState() {
		return taskRunState;
	}
	public void setTaskRunState(String taskRunState) {
		this.taskRunState = taskRunState;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getEntertaskid() {
		return entertaskid;
	}
	public void setEntertaskid(String entertaskid) {
		this.entertaskid = entertaskid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskClassPath() {
		return taskClassPath;
	}
	public void setTaskClassPath(String taskClassPath) {
		this.taskClassPath = taskClassPath;
	}
	
	/**
	 * 获取任务参数
	 * @return
	 */
	public UserTaskParams getTaskparames() {
		return taskparames;
	}
    
	/**
	 * 更新用户任务参数
	 */
	protected void updateUserTaskParams() {
		String updateSQL="UPDATE AM_USERTASK SET taskparame=? WHERE id=? ";
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			db.execute(updateSQL,new String[]{
					this.taskparames.getTableParams().toString(),this.id
			},new int[]{
					Type.VARCHAR,Type.VARCHAR
			});
			
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
	}
	
	/**
	 * 更新会员任务参数
	 * @param value   当前值
	 * @param valeKey   值关键字
	 * @throws JSONException
	 */
	protected void updateUserTaskParams(String value,String valeKey) throws JSONException{
		
		//更新任务字段中的数据
		JSONObject userTaskParams=this.taskparames.getTableParams();
		JSONObject TaskExecution=userTaskParams.getJSONObject("TaskExecution");
		
		if(TaskExecution!=null&&TaskExecution.has(Task_Current_Process)){
			userTaskParams.getJSONObject("TaskExecution").getJSONObject(Task_Current_Process)
			.getJSONObject(valeKey).put("VALUE", value);
			
			updateUserTaskParams();
		}
	}
    
}
