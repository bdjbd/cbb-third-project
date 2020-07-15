package com.am.frame.badge.entity;

import com.fastunit.Row;

/**
 * @author YueBin
 * @create 2016年4月15日
 * @version 
 * 说明:<br />
 * 徽章模版
 */
public class BadgeTemplate {
	private String id;//                   VARCHAR(36)          not null,
	private String badgeName;//   VARCHAR(50)          null,
	private String badgeParame;// VARCHAR(500)         null,
	private String badgeNote;//VARCHAR(2000)        null,
	private String createDate;//TIMESTAMP WITH TIME ZONE null,
	private String badgeIconPath;//VARCHAR(200)         null,
	private String badgeState;//VARCHAR(2)           null,
	private String badgeCode;//VARCHAR(20)          null,
	private String class_path;// 
	
	public BadgeTemplate(){}
	
	public BadgeTemplate(Row row){
		this.id=row.get("id");
		this.badgeName=row.get("badgename");
		this.badgeParame=row.get("badgeparame");
		this.createDate=row.get("createdate");
		this.badgeIconPath=row.get("badgeiconpath");
		this.badgeState=row.get("badgestate");
		this.badgeCode=row.get("badgecode");
		this.class_path=row.get("class_path");
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBadgeName() {
		return badgeName;
	}
	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}
	public String getBadgeParame() {
		return badgeParame;
	}
	public void setBadgeParame(String badgeParame) {
		this.badgeParame = badgeParame;
	}
	public String getBadgeNote() {
		return badgeNote;
	}
	public void setBadgeNote(String badgeNote) {
		this.badgeNote = badgeNote;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getBadgeIconPath() {
		return badgeIconPath;
	}
	public void setBadgeIconPath(String badgeIconPath) {
		this.badgeIconPath = badgeIconPath;
	}
	public String getBadgeState() {
		return badgeState;
	}
	public void setBadgeState(String badgeState) {
		this.badgeState = badgeState;
	}
	public String getBadgeCode() {
		return badgeCode;
	}
	public void setBadgeCode(String badgeCode) {
		this.badgeCode = badgeCode;
	}
	public String getClassPath() {
		return class_path;
	}
	public void setClassPath(String class_path) {
		this.class_path = class_path;
	}
	
	

}
