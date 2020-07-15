package com.am.frame.task.params;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author YueBin
 * @create 2016年3月3日
 * @version 
 * 说明:<br />
 * 用户任务参数 保存在数据库中的参数 
 * 
 */
public class UserTaskParams {

	//任务奖励参数
	private List<JSONObject> rewardParams=new ArrayList<JSONObject>();
	//任务描述JSONObject
	private JSONObject taskDescription;
	//任务目标
	private JSONObject taskObject;
	//任务执行情况
	private JSONObject taskExecution;
	
	//数据库保存的JSON 原始对象
	private JSONObject tableParams=new JSONObject();
	
	public UserTaskParams(JSONObject paramsStr){
		init(paramsStr);
	}
	
	public UserTaskParams(String paramsStr) {
		try{
			JSONObject prams=new JSONObject(paramsStr);
			init(prams);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void init(JSONObject paramsStr){
		try {
			if(paramsStr!=null){
				this.tableParams=paramsStr;
				if(tableParams.has("TaskDescription")){
						this.taskDescription=tableParams.getJSONObject("TaskDescription");
				}
				if(tableParams.has("TaskObject")){
					this.taskObject=tableParams.getJSONObject("TaskObject");
				}
				if(tableParams.has("TaskReward")){
					JSONArray rewards=tableParams.getJSONArray("TaskReward");
					for(int i=0;i<rewards.length();i++){
						rewardParams.add(rewards.getJSONObject(i));
					}
				}
				if(tableParams.has("TaskExecution")){
					this.taskExecution=tableParams.getJSONObject("TaskExecution");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public String getUserTaskParamsJSON(){
		return this.tableParams.toString();
	}
	
	public JSONObject getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(JSONObject taskDescription) {
		this.taskDescription = taskDescription;
	}
	public JSONObject getTaskObject() {
		return taskObject;
	}
	public void setTaskObject(JSONObject taskObject) {
		this.taskObject = taskObject;
	}
	/**
	 * 任务执行情况
	 * @return
	 */
	public JSONObject getTaskExecution() {
		return taskExecution;
	}
	/**
	 * 任务执行情况
	 * @param taskExecution
	 */
	public void setTaskExecution(JSONObject taskExecution) {
		this.taskExecution = taskExecution;
	}


	public List<JSONObject> getRewardParams() {
		return rewardParams;
	}


	public void setRewardParams(List<JSONObject> rewardParams) {
		this.rewardParams = rewardParams;
	}


	public JSONObject getTableParams() {
		return tableParams;
	}


	public void setTableParams(JSONObject tableParams) {
		this.tableParams = tableParams;
	}
	
}
