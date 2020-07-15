package com.am.frame.badge.entity;

import com.fastunit.Row;

/**
 * @author YueBin
 * @create 2016年4月15日
 * @version 
 * 说明:<br />
 * 企业徽章
 */
public class EnterpriseBadge {

	private String id;//                   VARCHAR(36)          not null,
	private String badgeTemplateID ;//     VARCHAR(36)          null,
	private String badgeParame;//       VARCHAR(500)         null,
	private String badgeNote;//        VARCHAR(2000)        null,
	private String orgID;//        VARCHAR(50)          null,
	private String badgeName;//       VARCHAR(50)          null,
	private String badgeIconPath;//
	
	public EnterpriseBadge(){}
	
	public EnterpriseBadge(Row row){
		this.id=row.get("id");
		this.badgeTemplateID=row.get("badgetemplateid");
		this.badgeParame=row.get("badgeparame");
		this.badgeNote=row.get("badgenote");
		this.orgID=row.get("orgid");
		this.badgeName=row.get("badgename");
		this.badgeIconPath=row.get("badgeiconpath");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBadgeTemplateID() {
		return badgeTemplateID;
	}

	public void setBadgeTemplateID(String badgeTemplateID) {
		this.badgeTemplateID = badgeTemplateID;
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

	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public String getBadgeName() {
		return badgeName;
	}

	public void setBadgeName(String badgeName) {
		this.badgeName = badgeName;
	}

	public String getBadgeIconPath() {
		return badgeIconPath;
	}

	public void setBadgeIconPath(String badgeIconPath) {
		this.badgeIconPath = badgeIconPath;
	}
	
	
}
