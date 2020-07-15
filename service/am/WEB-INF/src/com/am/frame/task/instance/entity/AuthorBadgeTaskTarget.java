package com.am.frame.task.instance.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.task.task.AbstractTask;

/**
 * @author YueBin
 * @create 2016年6月15日
 * @version 
 * 说明:<br />
 * 社员徽章任务任务目标
 */
public class AuthorBadgeTaskTarget {
	
	public AuthorBadgeTaskTarget(){}
	
	/****
	 * LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,
			LEVEL_1_5,LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5
	 */
	/**观望社员**/
	public static String LEVEL_WAIT="LEVEL_WAIT";
	/**爱心红卡社员**/
	public static String LEVEL_1_1="LEVEL_1_1";//
	/**爱心蓝卡社员**/
	public static String LEVEL_1_2="LEVEL_1_2";//
	/**爱心绿卡社员**/
	public static String LEVEL_1_3="LEVEL_1_3";//
	/**爱心银卡社员**/
	public static String LEVEL_1_4="LEVEL_1_4";//
	/**爱心金卡社员**/
	public static String LEVEL_1_5="LEVEL_1_5";//
	
	
	/**生产红卡社员**/
	public static String LEVEL_2_1="LEVEL_2_1";//
	/**生产蓝卡社员**/
	public static String LEVEL_2_2="LEVEL_2_2";//
	/**生产绿卡社员**/
	public static String LEVEL_2_3="LEVEL_2_3";//
	/**生产银卡社员**/
	public static String LEVEL_2_4="LEVEL_2_4";//
	/**生产金卡社员**/
	public static String LEVEL_2_5="LEVEL_2_5";//
	
	/**
	 * params JSON
	 * 格式：<br>
{
"memberType":"1=消费者社员;2=单位消费者社员;3=生产者社员",<br>
"Reward":"LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,
	LEVEL_1_5,LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5",<br>
"RESISTER":true,<br>
"CREDIT_MARGIN_MEONY":0,<br>
"COMUNT_FREEZER_DEPOSIT":0,<br>
"CONSUMER_RECHANGE":0,<br>
"HELP_FARMER":0,<br>
"INVST_PROJECT_MEONY":0.00,<br>
"INVITE_MEMBER_NUMBER":[<br>
	{"HALT_READ":0},{"READ":0},{"BLUE":0},{"GREEN":0},{"SILBER":0}<br>
	]<br>
}<br>
	 * @param params
	 */
	public AuthorBadgeTaskTarget(JSONObject params){
		if(params!=null){
			try {
//				this.memberType=params.getString("memberType");
//				this.reward=params.getString("Reward");
				
				this.resister=params.getBoolean("RESISTER");
				
				if(params!=null&&params.has(AbstractTask.Task_Current_Process)){
					JSONObject taskCurrentProcess=params.getJSONObject(AbstractTask.Task_Current_Process);
					
					//"信用保证金"
					this.creditMarginMeony=taskCurrentProcess.getJSONObject("CREDIT_MARGIN_MEONY").getString("VALUE");
					//社区冷柜押金
					this.comuntFreezerDeposit=taskCurrentProcess.getJSONObject("COMUNT_FREEZER_DEPOSIT").getString("VALUE");
					//消费账号充值
					this.consumerRechange=taskCurrentProcess.getJSONObject("CONSUMER_RECHANGE").getString("VALUE");
					//帮借农户
					this.helpFarmer=taskCurrentProcess.getJSONObject("HELP_FARMER").getString("VALUE");
					//"投资农业项目"
					this.invstProjectMeony=taskCurrentProcess.getJSONObject("INVST_PROJECT_MEONY").getString("VALUE");
					
					//生产红卡社员数量
					this.read=taskCurrentProcess.getJSONObject("READ").getString("VALUE");
					//生产蓝卡社员数量
					this.blue=taskCurrentProcess.getJSONObject("BLUE").getString("VALUE");//
					//生产绿卡社员数量
					this.green=taskCurrentProcess.getJSONObject("GREEN").getString("VALUE");
					//生产银卡社员数量
					this.silber=taskCurrentProcess.getJSONObject("SILBER").getString("VALUE");
					
					//爱心红社员数量
					this.haltRead=taskCurrentProcess.getJSONObject("HALT_READ").getString("VALUE");
					
					//社员等级
					this.currentLevel=taskCurrentProcess.getJSONObject("CURRENT_LEVEL").getString("VALUE");
					
				}else{
					this.creditMarginMeony=params.getString("CREDIT_MARGIN_MEONY");//	"CREDIT_MARGIN_MEONY":100,
					this.read=params.getString("READ");//	"READ":0,
					this.invstProjectMeony=params.getString("INVST_PROJECT_MEONY");//	"INVST_PROJECT_MEONY":0,
					this.silber=params.getString("SILBER");//	"SILBER":0,
					this.comuntFreezerDeposit=params.getString("COMUNT_FREEZER_DEPOSIT");//	"COMUNT_FREEZER_DEPOSIT":1000,
					this.blue=params.getString("BLUE");//	"BLUE":0,
					this.haltRead=params.getString("HALT_READ");//	"HALT_READ":0,
					this.helpFarmer=params.getString("HELP_FARMER");//	"HELP_FARMER":1,
					this.green=params.getString("GREEN");//	"GREEN":0,
					this.consumerRechange=params.getString("CONSUMER_RECHANGE");//	"CONSUMER_RECHANGE":0
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	//社员类型
//	private String memberType="1";//1=消费者社员;2=单位消费者社员;3=生产者社员",
	//奖励徽章企业编码
//	private String reward;//LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5,
						//LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5",
	//是否注册
	private boolean resister=true;//RESISTER;//true,
	//信用保证金（元）
	private String creditMarginMeony="0";//0,
	//社区冷柜押金
	private String comuntFreezerDeposit="0";//0,
	//消费账户充值(元)
	private String consumerRechange="0";//0,
	//帮借农户
	private String helpFarmer="0";//0,
	//投资项目股金总额（元）
	private String invstProjectMeony="0";//0.00,
	//邀请社员
	
	//生产红卡社员数量
	private String read="0";
	//生产蓝卡社员数量
	private String blue="0";//
	//生产绿卡社员数量
	private String green="0";
	//生产银卡社员数量
	private String silber="0";
	
	//爱心红社员数量
	private String haltRead="0";
	
	//社员等级
	private String currentLevel="LEVEL_WAIT";
	
	
	
	
	
	/**
	 * 获取社员类型  1=消费者社员;2=单位消费者社员;3=生产者社员",
	 * @return
	 */
//	public String getMemberType() {
//		return memberType;
//	}
	/**
	 * 
	 * @param memberType 1=消费者社员;2=单位消费者社员;3=生产者社员",
	 */
//	public void setMemberType(String memberType) {
//		this.memberType = memberType;
//	}
	/**
	 * 奖励  LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5,
	 * 	LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5
	 * @return
	 */
//	public String getReward() {
//		return reward;
//	}
	
	/**
	 * 奖励  LEVEL_WAIT,LEVEL_1_1,LEVEL_1_2,LEVEL_1_3,LEVEL_1_4,LEVEL_1_5,
	 * 	LEVEL_2_1,LEVEL_2_2,LEVEL_2_3,LEVEL_2_4,LEVEL_2_5
	 * @return
	 */
//	public void setReward(String reward) {
//		this.reward = reward;
//	}
//	
	public boolean isResister() {
		return resister;
	}

	public void setResister(boolean resister) {
		this.resister = resister;
	}


	/**
	 * 信用保证金（元）
	 * @return
	 */
	public String getCreditMarginMeony() {
		return creditMarginMeony;
	}
	/**
	 * 信用保证金（元）
	 * @param creditMarginMeony
	 */
	public void setCreditMarginMeony(String creditMarginMeony) {
		this.creditMarginMeony = creditMarginMeony;
	}
	/**
	 * 社区冷柜押金
	 * @return
	 */
	public String getComuntFreezerDeposit() {
		return comuntFreezerDeposit;
	}
	/**
	 * 社区冷柜押金
	 * @param comuntFreezerDeposit
	 */
	public void setComuntFreezerDeposit(String comuntFreezerDeposit) {
		this.comuntFreezerDeposit = comuntFreezerDeposit;
	}
	/**
	 * 消费账户充值(元)
	 * @return
	 */
	public String getConsumerRechange() {
		return consumerRechange;
	}
	/**
	 * 消费账户充值(元)
	 * @param consumerRechange
	 */
	public void setConsumerRechange(String consumerRechange) {
		this.consumerRechange = consumerRechange;
	}
	
	/**
	 * 帮借农户
	 * @return
	 */
	public String getHelpFarmer() {
		return helpFarmer;
	}
	/**
	 * 帮借农户
	 * @param helpFarmer
	 */
	public void setHelpFarmer(String helpFarmer) {
		this.helpFarmer = helpFarmer;
	}
	/**
	 * 投资项目股金总额（元）
	 * @return
	 */
	public String getInvstProjectMeony() {
		return invstProjectMeony;
	}
	/**
	 * 投资项目股金总额（元）
	 * @param invstProjectMeony
	 */
	public void setInvstProjectMeony(String invstProjectMeony) {
		this.invstProjectMeony = invstProjectMeony;
	}
	
	
	/**
	 * 比较目标是否与当前目标相同，如果大于等于，则返回true
	 * @param userTaskExec  当前状态
	 * @return
	 * @throws JSONException 
	 */
	public boolean checkTarget(AuthorBadgeTaskTarget userTaskExec,String memberType) throws JSONException {
		
		/**
		 * 任务是否完成对比项目
		 *  "RESISTER":true,
			"CREDIT_MARGIN_MEONY":0,
			"COMUNT_FREEZER_DEPOSIT":0,
			"CONSUMER_RECHANGE":0,
			"HELP_FARMER":0,
			"INVST_PROJECT_MEONY":0.00,
			"HALT_READ":0,
			"READ":0,
			"BLUE":0,
			"GREEN":0,
			"SILBER":0
		**/
		boolean result=true;
		
		//是否注册检查 1
		if(this.resister!=userTaskExec.resister){
			result=false;
		}
		
		//信用保证金（元）  2
		//任务目标值  
		float taskValue=Float.parseFloat(this.creditMarginMeony);
		//用户值
		float uValue=Float.parseFloat(userTaskExec.getCreditMarginMeony());
		if(uValue<taskValue){
			result=false;
		}
		
		//任务目标值   社区冷柜押金 3
		taskValue=Float.parseFloat(this.comuntFreezerDeposit);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getComuntFreezerDeposit());
		if(uValue<taskValue){
			result=false;
		}
		
		//任务目标值  消费账号充值  4
		taskValue=Float.parseFloat(this.consumerRechange);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getConsumerRechange());
		if(uValue<taskValue){
			result=false;
		}
		
		//任务目标值  帮扶农户  5
		if("3".equals(memberType)){
			//生产者直接完成任务
			result=true;
		}else{
			taskValue=Float.parseFloat(this.helpFarmer);
			//用户值
			uValue=Float.parseFloat(userTaskExec.getHelpFarmer());
			if(uValue<taskValue){
				result=false;
			}
		}
		
		
		//任务目标值  投资农业项目  6
		taskValue=Float.parseFloat(this.invstProjectMeony);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getInvstProjectMeony());
		if(uValue<taskValue){
			result=false;
		}
		
//		"HALT_READ":0,
		//任务值  红心会员数量
		taskValue=Float.parseFloat(this.haltRead);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getHaltRead());
		if(uValue<taskValue){
			result=false;
		}
		
		//"READ":0,
		//任务值  生产红会员数量
		taskValue=Float.parseFloat(this.read);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getRead());
		if(uValue<taskValue){
			result=false;
		}
		
//		"BLUE":0,  生产蓝
		taskValue=Float.parseFloat(this.blue);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getBlue());
		if(uValue<taskValue){
			result=false;
		}
//		"GREEN":0, 生产绿
		taskValue=Float.parseFloat(this.green);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getGreen());
		if(uValue<taskValue){
			result=false;
		}
		
//		"SILBER":0  生产银
		taskValue=Float.parseFloat(this.silber);
		//用户值
		uValue=Float.parseFloat(userTaskExec.getSilber());
		if(uValue<taskValue){
			result=false;
		}
		
		return result;
	}

	/**生产红卡社员数量**/
	public String getRead() {
		return read;
	}

	/**生产红卡社员数量**/
	public void setRead(String read) {
		this.read = read;
	}


	public String getBlue() {
		return blue;
	}


	public void setBlue(String blue) {
		this.blue = blue;
	}


	public String getGreen() {
		return green;
	}


	public void setGreen(String green) {
		this.green = green;
	}


	public String getSilber() {
		return silber;
	}


	public void setSilber(String silber) {
		this.silber = silber;
	}

	
	/**
	 * 爱心红社员数量
	 * @return
	 */
	public String getHaltRead() {
		return haltRead;
	}


	public void setHaltRead(String haltRead) {
		this.haltRead = haltRead;
	}

	public String getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	
	
	
}
