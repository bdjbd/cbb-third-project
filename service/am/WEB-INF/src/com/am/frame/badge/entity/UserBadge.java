package com.am.frame.badge.entity;

import com.fastunit.Row;

/**
 * @author YueBin
 * @create 2016年4月15日
 * @version 
 * 说明:<br />
 * 用户徽章 
 */
public class UserBadge {

	private String  id;//                   VARCHAR(36)          not null,
	private String  enterpriseBadgeID ;//   VARCHAR(36)          null,
	private String  memberId     ;//        VARCHAR(36)          null,
	private String  badgeParame ;//
	
	public UserBadge(){}
	
	public UserBadge(Row row){
		this.id=row.get("id");
		this.enterpriseBadgeID=row.get("enterprisebadgeid");
		this.memberId=row.get("memberid");
		this.badgeParame=row.get("badgeparame");
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEnterpriseBadgeID() {
		return enterpriseBadgeID;
	}
	public void setEnterpriseBadgeID(String enterpriseBadgeID) {
		this.enterpriseBadgeID = enterpriseBadgeID;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getBadgeParame() {
		return badgeParame;
	}
	public void setBadgeParame(String badgeParame) {
		this.badgeParame = badgeParame;
	}
	
}
