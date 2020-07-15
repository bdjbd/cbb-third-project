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
 * 状态流程定义
 */
public class StateFlowSetup implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String code;
	private String name;
	private String explain;
	private String tableName;
	private String stateFieldName;
	private String keyName;
	private String createDate;
	private String creater;
	
	public StateFlowSetup(){
	}
	
	public StateFlowSetup(MapList map){
		try{
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				
				this.id=row.get("id");
				this.code=row.get("code");
				this.name=row.get("name");
				this.explain=row.get("explain");
				this.tableName=row.get("tablename");
				this.stateFieldName=row.get("statefieldname");
				this.keyName=row.get("keyname");
				this.createDate=row.get("createdate");
				this.creater=row.get("creater");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public StateFlowSetup(Row row){
		try{
			if(row!=null){
				
				this.id=row.get("id");
				this.code=row.get("code");
				this.name=row.get("name");
				this.explain=row.get("explain");
				this.tableName=row.get("tablename");
				this.stateFieldName=row.get("statefieldname");
				this.keyName=row.get("keyname");
				this.createDate=row.get("createdate");
				this.creater=row.get("creater");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getId() {
		return id;
	}
	public void setI(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getStateFieldName() {
		return stateFieldName;
	}
	public void setStateFieldName(String stateFieldName) {
		this.stateFieldName = stateFieldName;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	
}
