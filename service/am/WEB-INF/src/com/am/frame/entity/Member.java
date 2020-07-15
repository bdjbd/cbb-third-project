package com.am.frame.entity;

import com.fastunit.Row;

/**
 * @author YueBin
 * @create 2016年3月17日
 * @version 
 * 说明:<br />
 * 会员beans
 */
public class Member {

	private String id;
	private String loginAccount;
	private String loginPassword;
	private String phone;
	private String memberName;
	private String memberBirthday;
	private String memberSex;
	private String eMail;
	private String registrationDate;
	private String identityCardNumber;
	private String memberAddress;
	private String orgCode;
	private String upID;
	private String bdPushUserID;
	private String bdPushChannelID;
	private int    score;
	private String openID;
	private String wxnickName;
	private String wxHeadImg;
	private String bdPushdevicetype;
	private long   promotionCode;
	private double cash;      
	private String createTime;      
	private int mrole;
	private int isAuth;
	private String remark;    
	private long creditMarginAct;
	private long identityCapitalAct;
	private long bonusAct;
	private long rebateAct;
	private long consumerAct;
	private long consumerUnit;
	private long foodSafetyAct;
	private long creditCard;
	private long consumerCapitalAct;
	private String  villageName;
	private String heirName;
	private String heirPhone;
	private String heirIdcardCode;
	private String  province;
	private String  city;
	private String  zzone;

	public Member(){}
	
	public Member(Row row){
		if(row!=null){
			id=row.get("id");
			loginAccount=row.get("loginaccount");
			loginPassword=row.get("loginpassword");
			phone=row.get("phone");
			memberName=row.get("membername");
			memberBirthday=row.get("memberbirthday");
			memberSex=row.get("membersex");
			eMail=row.get("email");
			registrationDate=row.get("registrationdate");
			identityCardNumber=row.get("identitycardnumber");
			memberAddress=row.get("memberaddress");
			orgCode=row.get("orgcode");
			upID=row.get("upid");
			bdPushUserID=row.get("bdpushuserid");
			bdPushChannelID=row.get("bdpushchannelid");
			score=row.getInt("score",0);
			openID=row.get("openid");
			wxnickName=row.get("wxnickname");
			wxHeadImg=row.get("wxheadimg");
			bdPushdevicetype=row.get("bdpushdevicetype");
			promotionCode=row.getLong("promotioncode",0);
			cash=row.getDouble("cash",0.00);      
			createTime=row.get("create_time");      
			mrole=row.getInt("mrole",0);
			isAuth=row.getInt("is_auth",0);
			remark=row.get("remark");    
			creditMarginAct=row.getLong("credit_margin_act",0);
			identityCapitalAct=row.getLong("identity_capital_act",0);
			bonusAct=row.getLong("bonus_act",0);
			rebateAct=row.getLong("rebate_act",0);
			consumerAct=row.getLong("consumer_act",0);
			consumerUnit=row.getLong("consumer_unit",0);
			foodSafetyAct=row.getLong("food_safety_act",0);
			creditCard=row.getLong("credit_card",0);
			consumerCapitalAct=row.getLong("consumer_capital_act",0);
			villageName=row.get("village_name");
			heirName=row.get("heir_name");
			heirPhone=row.get("heir_phone");
			heirIdcardCode=row.get("heir_idcard_code");
			province=row.get("province");
			city=row.get("city");
			zzone=row.get("zzone");
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberBirthday() {
		return memberBirthday;
	}

	public void setMemberBirthday(String memberBirthday) {
		this.memberBirthday = memberBirthday;
	}

	public String getMemberSex() {
		return memberSex;
	}

	public void setMemberSex(String memberSex) {
		this.memberSex = memberSex;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getIdentityCardNumber() {
		return identityCardNumber;
	}

	public void setIdentityCardNumber(String identityCardNumber) {
		this.identityCardNumber = identityCardNumber;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getUpID() {
		return upID;
	}

	public void setUpID(String upID) {
		this.upID = upID;
	}

	public String getBdPushUserID() {
		return bdPushUserID;
	}

	public void setBdPushUserID(String bdPushUserID) {
		this.bdPushUserID = bdPushUserID;
	}

	public String getBdPushChannelID() {
		return bdPushChannelID;
	}

	public void setBdPushChannelID(String bdPushChannelID) {
		this.bdPushChannelID = bdPushChannelID;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getOpenID() {
		return openID;
	}

	public void setOpenID(String openID) {
		this.openID = openID;
	}

	public String getWxnickName() {
		return wxnickName;
	}

	public void setWxnickName(String wxnickName) {
		this.wxnickName = wxnickName;
	}

	public String getWxHeadImg() {
		return wxHeadImg;
	}

	public void setWxHeadImg(String wxHeadImg) {
		this.wxHeadImg = wxHeadImg;
	}

	public String getBdPushdevicetype() {
		return bdPushdevicetype;
	}

	public void setBdPushdevicetype(String bdPushdevicetype) {
		this.bdPushdevicetype = bdPushdevicetype;
	}

	public long getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(long promotionCode) {
		this.promotionCode = promotionCode;
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getMrole() {
		return mrole;
	}

	public void setMrole(int mrole) {
		this.mrole = mrole;
	}

	public int getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(int isAuth) {
		this.isAuth = isAuth;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getCreditMarginAct() {
		return creditMarginAct;
	}

	public void setCreditMarginAct(long creditMarginAct) {
		this.creditMarginAct = creditMarginAct;
	}

	public long getIdentityCapitalAct() {
		return identityCapitalAct;
	}

	public void setIdentityCapitalAct(long identityCapitalAct) {
		this.identityCapitalAct = identityCapitalAct;
	}

	public long getBonusAct() {
		return bonusAct;
	}

	public void setBonusAct(long bonusAct) {
		this.bonusAct = bonusAct;
	}

	public long getRebateAct() {
		return rebateAct;
	}

	public void setRebateAct(long rebateAct) {
		this.rebateAct = rebateAct;
	}

	public long getConsumerAct() {
		return consumerAct;
	}

	public void setConsumerAct(long consumerAct) {
		this.consumerAct = consumerAct;
	}

	public long getConsumerUnit() {
		return consumerUnit;
	}

	public void setConsumerUnit(long consumerUnit) {
		this.consumerUnit = consumerUnit;
	}

	public long getFoodSafetyAct() {
		return foodSafetyAct;
	}

	public void setFoodSafetyAct(long foodSafetyAct) {
		this.foodSafetyAct = foodSafetyAct;
	}

	public long getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(long creditCard) {
		this.creditCard = creditCard;
	}

	public long getConsumerCapitalAct() {
		return consumerCapitalAct;
	}

	public void setConsumerCapitalAct(long consumerCapitalAct) {
		this.consumerCapitalAct = consumerCapitalAct;
	}

	public String getVillageName() {
		return villageName;
	}

	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	public String getHeirName() {
		return heirName;
	}

	public void setHeirName(String heirName) {
		this.heirName = heirName;
	}

	public String getHeirPhone() {
		return heirPhone;
	}

	public void setHeirPhone(String heirPhone) {
		this.heirPhone = heirPhone;
	}

	public String getHeirIdcardCode() {
		return heirIdcardCode;
	}

	public void setHeirIdcardCode(String heirIdcardCode) {
		this.heirIdcardCode = heirIdcardCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZzone() {
		return zzone;
	}

	public void setZzone(String zzone) {
		this.zzone = zzone;
	}
	
}
