package com.am.frame.task.params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.am.frame.task.task.ITask;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 
 * @author YueBin
 * 
 * 任务运行时任务参数
 * 任务参数类，在初始化任务时代入
 * 至少包含任务对象,及会员id,任务名称名称
 * 任务执行时的任务参数，不是任务表中的人物参数
 */
public class RunTaskParams {
	
	/**
	 * 任务ID
	 */
	protected String taskId;
	/**
	 * 任务会员id
	 */
	protected String memberId;
	/**会员任务ID**/
	protected String memberOrderId;
	
	public String getMemberOrderId() {
		return memberOrderId;
	}

	public void setMemberOrderId(String memberOrderId) {
		this.memberOrderId = memberOrderId;
	}

	/**
	 * 奖励目标会员集合
	 */
	protected List<String> targetMemberList=new ArrayList<String>();
	
	/***
	 * 任务类全限定名 报名+类名
	 */
	protected String taskClassName;
	
	/**
	 * 任务机构名称
	 * **/
	protected String orgId;
	
	
	/**
	 * 企业任务名称
	 */
	protected String taskName;
	/**
	 * 企业任务名称编码
	 * **/
	protected String taskCode;
	
	protected ITask task;
	
	//任务参数集合
	protected Map<String,Object> params=new HashMap<String,Object>();
	
	/**
	 * 向执行任务参数中增加参数
	 * @param key
	 * @param value
	 */
	public void pushParam(String key,Object value){
		params.put(key, value);
	}
	
	/**
	 * 增加任务执行奖励目标会员id
	 * @param miemberId
	 */
	public void addRewardTargetMember(String miemberId){
		if(!targetMemberList.contains(miemberId)){
			targetMemberList.add(miemberId);
		}
	}
	
	/**
	 * 获取参数
	 * @param key
	 * @return 获取任务参数
	 */
	public <T> T getParams(String key){
		T t = (T) params.get(key);
		T result=t;
		return result;
	}
	
	/**
	 * 获取参数
	 * @return 获取任务参数
	 */
	public Map<String,Object> getParams(){
		return params;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}
	public void setTask(ITask task) {
		this.task = task;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	/**
	 * 设置奖励规则的当前规则参数
	 * @param rewParams
	 */
	public void setRewParams(JSONObject rewParams) {
		this.rewParams = rewParams;
	}
	
	/**
	 * 设置任务名，此处为企业任务名称
	 * @param taskName
	 */
	public void setTaskName(String taskName) {
		
		this.taskName = taskName;
	}
	
	//奖励规则本身的参数
	protected JSONObject rewParams;
	
	public String getTaskId() {
		return taskId;
	}
	
	public String getMemberId() {
		return memberId;
	}
	
	public String getTaskClassName() {
		return taskClassName;
	}
	
	public ITask getTask() {
		return task;
	}
	
	
	public String getTaskName() {
		if(Checker.isEmpty(this.taskName)){
			//如果人们名称为空，则会通过人物编号获取
			
			DB db=null;
			try{
				db=DBFactory.newDB();
				
				String querySQL="SELECT name FROM am_EnterpriseTask WHERE task_code=? ";
				
				MapList map=db.query(querySQL,this.taskCode, Type.VARCHAR);
				
				if(!Checker.isEmpty(map)){
					String name=map.getRow(0).get("name");
					this.taskName=name;
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
		}
		return taskName;
	}
	/**
	 * 获取当前规则的奖励规则参数
	 * @return
	 */
	public JSONObject getRewParams() {
		return rewParams;
	}
	
	public String getOrgId() {
		return orgId;
	}

	/**
	 * 获取奖励会员奖励目标
	 * @return
	 */
	public List<String> getTargetMemberList() {
		return targetMemberList;
	}
	
	/**
	 * 获取企业任务code
	 * @return
	 */
	public String getTaskCode() {
		return taskCode;
	}
	
	/**
	 * 获取企业任务code
	 * @return 任务编码
	 */
	public void setTaskCode(String taskCode) {
		this.taskCode=taskCode;
	}
	
}
