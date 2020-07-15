package com.am.mall.beans.order;

import java.io.Serializable;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月13日
 * @version 
 * 说明:<br />
 * 流程状态
 */
public class StateFlowStepSetup  implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String stateFlowSetupID;
	private String stateName;
	private String stateValue;
	private String nextStateValue;
	private String classPath;
	private String paramList;
	private int sort;
	private String failureStateVaule;
	
	private ParamList params;
	
	public StateFlowStepSetup(){
		
	}
	
	public StateFlowStepSetup(MapList map){
		try{
			if(!Checker.isEmpty(map)){
				
				Row row=map.getRow(0);
				
				this.id=row.get("id");
				this.stateFlowSetupID=row.get("stateflowsetupid");
				this.stateName=row.get("statename");
				this.stateValue=row.get("statevalue");
				this.nextStateValue=row.get("nextstatevalue");
				this.classPath=row.get("classpath");
				this.paramList=row.get("paramlist");
				this.sort=row.getInt("sort",-1);
				this.failureStateVaule=row.get("failurestatevaule");
				
				this.parseParam();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StateFlowStepSetup(Row row){
		try{
			if(row!=null){
				
				this.id=row.get("id");
				this.stateFlowSetupID=row.get("stateflowsetupid");
				this.stateName=row.get("statename");
				this.stateValue=row.get("statevalue");
				this.nextStateValue=row.get("nextstatevalue");
				this.classPath=row.get("classpath");
				this.paramList=row.get("paramlist");
				this.sort=row.getInt("sort",-1);
				
				this.parseParam();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void parseParam(){
		this.params=new ParamList();
		this.params.parse(this.paramList);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStateFlowSetupID() {
		return stateFlowSetupID;
	}
	public void setStateFlowSetupID(String stateFlowSetupID) {
		this.stateFlowSetupID = stateFlowSetupID;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getStateValue() {
		return stateValue;
	}
	public void setStateValue(String stateValue) {
		this.stateValue = stateValue;
	}
	public String getNextStateValue() {
		return nextStateValue;
	}
	public void setNextStateValue(String nextStateValue) {
		this.nextStateValue = nextStateValue;
	}
	public String getClassPath() {
		return classPath;
	}
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
	public String getParamList() {
		return paramList;
	}
	public void setParamList(String paramList) {
		this.paramList = paramList;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}

	public ParamList getParams() {
		return params;
	}

	public String getFailureStateVaule() {
		return failureStateVaule;
	}

	public void setFailureStateVaule(String failureStateVaule) {
		this.failureStateVaule = failureStateVaule;
	}
	
}
