package com.am.frame.badge.impl;

import com.am.frame.badge.AbstraceBadge;

/**
 * @author YueBin
 * @create 2016年6月13日
 * @version 
 * 说明:<br />
 * 社员权限徽章
 * 徽章参数如下：<br>
{
"memberType":"社员类型，1=消费者社员；2=单位消费者社员；3=生产者社员",
"level":"社员等级，消费社员等级：LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5
 生产社员等级：LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5",
"level_name":"社员等级名称：爱心红卡社员,爱心蓝卡社员,爱心绿卡社员,爱心银卡社员,爱心金卡社员；
生产红卡社员,生产蓝卡社员,生产绿卡社员,生产银卡社员,生产金卡社员",
"TASK_CONDITION":{
"RESISTER":true,--是否注册
"CREDIT_MARGIN_MEONY":"0",--信用保证金充值金额
"COMUNT_FREEZER_DEPOSIT":"0",--社区冷柜押金
"CONSUMER_RECHANGE":"0",--消费帐号充值金额
"HELP_FARMER":"0",--帮借农户
"INVST_PROJECT_MEONY":"0",--投资项目股金总额（元）
"INVITE_MEMBER_NUMBER":[--邀请社员
{
"HALT_READ":0--5爱心红
},
{
"READ":0--5红
},
{
"BLUE":0--9蓝
},
{
"GREEN":0--5绿
},
{
"SILBER":0--5银
}
]
},
"AuthorityPoint":{--权限
"ACCESS_ROUTE":[ --可访问路由
{
"SANC_COMMODITY":true --查看商品
},
{
"BUY_COMMODITY":false --购买商品
},
{
"CUSTOM_RECHANGE":true --消费帐号充值
},
{
"FULL_TIME_PROFESSIONAL":false  --专职营养师
}
],
"GET_OOD":false,-- 获得经营分红权
"OOD_RATIO":"0"--获得经营分红比例
},
"GET_INVST_PROJECTBONUS":false, 获得股份分红
"GET_INVITE_FREE":false 邀请下级社员获得服务费
}
 */
public class MemberAuthorityLevelBadge extends AbstraceBadge {

	private String memberType;//:"社员类型，1=消费者社员；2=单位消费者社员；3=生产者社员",
	private String level;//"社员等级，消费社员等级：LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5 
						//生产社员等级：LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5",
	private String level_name;//"社员等级名称：爱心红卡社员,爱心蓝卡社员,爱心绿卡社员,爱心银卡社员,爱心金卡社员；
					//生产红卡社员,生产蓝卡社员,生产绿卡社员,生产银卡社员,生产金卡社员",
	
//	"TASK_CONDITION":{
	private String  RESISTER ;//RESISTER":true,--是否注册
	private String CREDIT_MARGIN_MEONY;//":"0",--信用保证金充值金额
	private String COMUNT_FREEZER_DEPOSIT;//:"0",--社区冷柜押金
	private String CONSUMER_RECHANGE;//:"0",--消费帐号充值金额
	private String HELP_FARMER;//:"0",--帮借农户
	private String INVST_PROJECT_MEONY;//:"0",--投资项目股金总额（元）
	private String INVITE_MEMBER_NUMBER;//":[--邀请社员
	private String HALT_READ;//:0--5爱心红
	private String READ;//:0--5红
	private String BLUE;//:0--9蓝
	private String GREEN;//:0--5绿
	private String SILBER;//:0--5银
	private String SANC_COMMODITY;//:true --查看商品
	private String BUY_COMMODITY;//false --购买商品
	private String CUSTOM_RECHANGE;//:true --消费帐号充值
	private String FULL_TIME_PROFESSIONAL;//:false  --专职营养师
	private String GET_OOD;//:false,-- 获得经营分红权
	private String OOD_RATIO;//:"0"--获得经营分红比例
	private String GET_INVST_PROJECTBONUS;//:false, 获得股份分红
	private String GET_INVITE_FREE;//:false 邀请下级社员获得服务费
	public String getMemberType() {
		return memberType;
	}
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}
	public String getRESISTER() {
		return RESISTER;
	}
	public void setRESISTER(String rESISTER) {
		RESISTER = rESISTER;
	}
	public String getCREDIT_MARGIN_MEONY() {
		return CREDIT_MARGIN_MEONY;
	}
	public void setCREDIT_MARGIN_MEONY(String cREDIT_MARGIN_MEONY) {
		CREDIT_MARGIN_MEONY = cREDIT_MARGIN_MEONY;
	}
	public String getCOMUNT_FREEZER_DEPOSIT() {
		return COMUNT_FREEZER_DEPOSIT;
	}
	public void setCOMUNT_FREEZER_DEPOSIT(String cOMUNT_FREEZER_DEPOSIT) {
		COMUNT_FREEZER_DEPOSIT = cOMUNT_FREEZER_DEPOSIT;
	}
	public String getCONSUMER_RECHANGE() {
		return CONSUMER_RECHANGE;
	}
	public void setCONSUMER_RECHANGE(String cONSUMER_RECHANGE) {
		CONSUMER_RECHANGE = cONSUMER_RECHANGE;
	}
	public String getHELP_FARMER() {
		return HELP_FARMER;
	}
	public void setHELP_FARMER(String hELP_FARMER) {
		HELP_FARMER = hELP_FARMER;
	}
	public String getINVST_PROJECT_MEONY() {
		return INVST_PROJECT_MEONY;
	}
	public void setINVST_PROJECT_MEONY(String iNVST_PROJECT_MEONY) {
		INVST_PROJECT_MEONY = iNVST_PROJECT_MEONY;
	}
	public String getINVITE_MEMBER_NUMBER() {
		return INVITE_MEMBER_NUMBER;
	}
	public void setINVITE_MEMBER_NUMBER(String iNVITE_MEMBER_NUMBER) {
		INVITE_MEMBER_NUMBER = iNVITE_MEMBER_NUMBER;
	}
	public String getHALT_READ() {
		return HALT_READ;
	}
	public void setHALT_READ(String hALT_READ) {
		HALT_READ = hALT_READ;
	}
	public String getREAD() {
		return READ;
	}
	public void setREAD(String rEAD) {
		READ = rEAD;
	}
	public String getBLUE() {
		return BLUE;
	}
	public void setBLUE(String bLUE) {
		BLUE = bLUE;
	}
	public String getGREEN() {
		return GREEN;
	}
	public void setGREEN(String gREEN) {
		GREEN = gREEN;
	}
	public String getSILBER() {
		return SILBER;
	}
	public void setSILBER(String sILBER) {
		SILBER = sILBER;
	}
	public String getSANC_COMMODITY() {
		return SANC_COMMODITY;
	}
	public void setSANC_COMMODITY(String sANC_COMMODITY) {
		SANC_COMMODITY = sANC_COMMODITY;
	}
	public String getBUY_COMMODITY() {
		return BUY_COMMODITY;
	}
	public void setBUY_COMMODITY(String bUY_COMMODITY) {
		BUY_COMMODITY = bUY_COMMODITY;
	}
	public String getCUSTOM_RECHANGE() {
		return CUSTOM_RECHANGE;
	}
	public void setCUSTOM_RECHANGE(String cUSTOM_RECHANGE) {
		CUSTOM_RECHANGE = cUSTOM_RECHANGE;
	}
	public String getFULL_TIME_PROFESSIONAL() {
		return FULL_TIME_PROFESSIONAL;
	}
	public void setFULL_TIME_PROFESSIONAL(String fULL_TIME_PROFESSIONAL) {
		FULL_TIME_PROFESSIONAL = fULL_TIME_PROFESSIONAL;
	}
	public String getGET_OOD() {
		return GET_OOD;
	}
	public void setGET_OOD(String gET_OOD) {
		GET_OOD = gET_OOD;
	}
	public String getOOD_RATIO() {
		return OOD_RATIO;
	}
	public void setOOD_RATIO(String oOD_RATIO) {
		OOD_RATIO = oOD_RATIO;
	}
	public String getGET_INVST_PROJECTBONUS() {
		return GET_INVST_PROJECTBONUS;
	}
	public void setGET_INVST_PROJECTBONUS(String gET_INVST_PROJECTBONUS) {
		GET_INVST_PROJECTBONUS = gET_INVST_PROJECTBONUS;
	}
	public String getGET_INVITE_FREE() {
		return GET_INVITE_FREE;
	}
	public void setGET_INVITE_FREE(String gET_INVITE_FREE) {
		GET_INVITE_FREE = gET_INVITE_FREE;
	}
	
}
