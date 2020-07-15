package com.am.frame.task.instance;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.badge.impl.AuthorityLevelBadgeParams;
import com.am.frame.member.MemberManager;
import com.am.frame.task.instance.entity.AuthorBadgeTaskTarget;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.AbstractTask;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年6月13日
 * @version 
 * 说明:<br />
 * 社员权限徽章任务，理性农业权限管理，徽章控制权限。
 * 任务触发点:
 * 1,用户注册;  USER_REGISTER
 * 2,信用保证金充值;CREDIT_MARGIN_RECHANGE
 * 3,社区冷柜开通押金;COMUNT_FREEZER_DEPOSIT
 * 4,消费账号充值;CONSUMER_RECHANGE
 * 5,帮借农户;HELP_FARMER
 * 6,投资农业项目;INVST_PROJECT_MEONY
 * 7,邀请的社员等级发生变化; EXECUT_TASK
 */
public class MemberAuthorityBadgeTask extends AbstractTask {
	
	private String memberType="";
	
	public final static String LEVEL_WAIT="LEVEL_WAIT";
	/**爱心红卡社员**/
	public final static String LEVEL_1_1="LEVEL_1_1";
	/**爱心蓝卡社员**/
	public final static String LEVEL_1_2="LEVEL_1_2";
	/**爱心绿卡社员**/
	public final static String LEVEL_1_3="LEVEL_1_3";
	/**爱心银卡社员***/
	public final static String LEVEL_1_4="LEVEL_1_4";
	/**爱心金卡社员**/
	public final static String LEVEL_1_5="LEVEL_1_5";
	
	/**生产红卡社员**/
	public final static String LEVEL_2_1="LEVEL_2_1";
	/**生产蓝卡社员**/
	public final static String LEVEL_2_2="LEVEL_2_2";
	/**生产绿卡社员**/
	public final static String LEVEL_2_3="LEVEL_2_3";
	/**生产银卡社员**/
	public final static String LEVEL_2_4="LEVEL_2_4";
	/**生产金卡社员**/
	public final static String LEVEL_2_5="LEVEL_2_5";
	


	/**奖励徽章编码**/
	public final static String EBADGE_CODE="EBADGE_CODE";
	
	/**任务对应的企业徽章编码**/
	public final static String BADGE_TASK_ENT_CODE="Badge.MemberAuthorityLevel";
	
	/**社员类型  1=消费者社员;2=单位消费者社员;3=生产者社员 **/
	public final static String MEMBER_TYPE="MemberAuthorityBadgeTask.MEMBER_TYPE";
	/**任务出触发点KEY**/
	public final static String TRIGGER_POINT="MemberAuthorityBadgeTask.TRIGGER_POINT";
	
	/**用户注册  lxny ok**/
	public final static String USER_REGISTER="MemberAuthorityBadgeTask.USER_REGISTER";
	
	/**信用保证金充值;CREDIT_MARGIN_RECHANGE  值 押金金额 单位元**/
	public final static String CREDIT_MARGIN_RECHANGE="MemberAuthorityBadgeTask.CREDIT_MARGIN_RECHANGE";
	
	/**社区冷柜开通押金;COMUNT_FREEZER_DEPOSIT 值 押金金额 单位元***/
	public final static String COMUNT_FREEZER_DEPOSIT="MemberAuthorityBadgeTask.COMUNT_FREEZER_DEPOSIT";
	
	/**消费账号充值;CONSUMER_RECHANGE 值 充值金额 单位元**/
	public final static String CONSUMER_RECHANGE="MemberAuthorityBadgeTask.CONSUMER_RECHANGE";
	
	/**帮借农户;HELP_FARMER 值，帮扶数量**/
	public final static String HELP_FARMER="MemberAuthorityBadgeTask.HELP_FARMER";
	
	/**投资农业项目;INVST_PROJECT_MEONY 值，帮扶金额 单位元**/
	public final static String INVST_PROJECT_MEONY="MemberAuthorityBadgeTask.INVST_PROJECT_MEONY";
	
	/**
	 * 邀请的社员等级发生变化;  INVITE_MEMBER_NUMBER
	 * {"HALT_READ":0,"READ":0,"BLUE":0,"GREEN":0,"SILBER":0}
	 * **/
	public final static String INVITE_MEMBER_NUMBER="MemberAuthorityBadgeTask.INVITE_MEMBER_NUMBER";
	
	/**
	 * 任务参数 ,任务社员参数
	 */
	public final static String TASK_PARAMS_INVITE_MEMBER_NUMBER="INVITE_MEMBER_NUMBER";
	
	/**企业任务编码**/
	public static final String TASK_ECODE="MEMBER_AUTHORITY_BADGE_TASK";

	
	public static Map<String,String> memberLevelMap=new HashMap<String,String>();	
	
	
	static{
		memberLevelMap.put("LEVEL_WAIT","观望社员");//	
		
		memberLevelMap.put("LEVEL_1_1","爱心红卡社员");//	
		memberLevelMap.put("LEVEL_1_2","爱心蓝卡社员");//
		memberLevelMap.put("LEVEL_1_3","爱心绿卡社员");//	
		memberLevelMap.put("LEVEL_1_4","爱心银卡社员");//	
		memberLevelMap.put("LEVEL_1_5","爱心金卡社员");//	
		
		memberLevelMap.put("LEVEL_2_1","生产红卡社员");//	
		memberLevelMap.put("LEVEL_2_2","生产蓝卡社员");//
		memberLevelMap.put("LEVEL_2_3","生产绿卡社员");//	
		memberLevelMap.put("LEVEL_2_4","生产银卡社员");//	
		memberLevelMap.put("LEVEL_2_5","生产金卡社员");//	


	}
	
	@Override
	public boolean updateUserTaskData(RunTaskParams runTaskParams, DB db)
			throws Exception {
		
		String querySQL="SELECT * FROM am_member WHERE id=? ";
		MapList map=db.query(querySQL,runTaskParams.getMemberId(),Type.VARCHAR);
		memberType=map.getRow(0).get("member_type");
		
		
		//获取任务触发点Value
		String taskTiggerPoint=runTaskParams.getParams(TRIGGER_POINT);
		
		//处理不同触发点触发活动的业务逻辑
		boolean reuslt=processTask(taskTiggerPoint,db,runTaskParams);
		
		return reuslt;
	}
	
	/**
	 * 根据不同的任务触发点，处理不同的任务
	 * @param taskTiggerPoint
	 * @param db
	 * @param runTaskParams
	 * @throws JDBCException 
	 * @throws JSONException 
	 */
	private boolean  processTask(String taskTiggerPoint, DB db,
			RunTaskParams runTaskParams) throws Exception {
		//任务目标模版
		JSONObject taskObject=null;
		
		if(this.taskparames!=null){
		 taskObject=this.taskparames.getTaskObject();
		}
		
		//用户当前任务参数,不同的触发点，有不同的计算,任务会更新此对象
		if(this.taskparames==null){
			logger.error("执行任务失败,任务名称:"+this.taskName+"\n任务信息："+this.toString());
			return false;
		}
		
		AuthorBadgeTaskTarget userTaskExec=new AuthorBadgeTaskTarget(this.taskparames.getTaskExecution());
		
		if("3".equals(memberType)){
			//生产者无法帮扶生产者，此处直接返回true
			//更新任务字段中的数据
			updateUserTaskParams("生产者无需帮扶","HELP_FARMER");
		}
		
		
		//是否需要执行奖励
		boolean result=false;
		//触发任务的社员ID
		String memberId=runTaskParams.getMemberId();
		
		//会员徽章管理类
//		AMBadgeManager badgeManager = new AMBadgeManager();

		//用户注册
		if(USER_REGISTER.equalsIgnoreCase(taskTiggerPoint)){
			//用户注册   用户注册触发此任务为 为会员分配权限徽章
//			badgeManager.addBadgeByEntBadgeCode(db,BADGE_TASK_ENT_CODE,memberId);
//			MapList userBadgeMap=badgeManager.getUserBadgetMapList(db,memberId,BADGE_TASK_ENT_CODE);
//			AuthorityLevelBadgeParams authBadge=getUserAutherBadge(db, memberId, badgeManager);
//			authBadge.setMemberType(memberType);
//			//更新会员徽章参数
//			String badgeParams=authBadge.toJson().toString();
//			badgeManager.updateUserBadgeParams(db,userBadgeMap.getRow(0).get("id"),badgeParams);
			// 用户注册，执行奖励，奖励徽章编号为 LEVEL_WAIT
			
			//奖励参数
			JSONObject rewParam=new JSONObject();
			
//			{"rewName":"奖励徽章","rewardParams":{"entBadgeCode":"Badge.MemberAuthorityLevel"}}
			JSONObject rewParams=new JSONObject();
			rewParam.put("rewName", "奖励徽章");
			rewParams.put("entBadgeCode",LEVEL_WAIT);
			rewParam.put("rewardParams",rewParams);
//			{"rewardParams":{"score":1000},"rewName":"奖励积分"}
//			runTaskParams.setRewParams(rewParam);
			this.taskparames.getRewardParams().add(rewParam);
			runTaskParams.addRewardTargetMember(runTaskParams.getMemberId());
			
		}
		
//		 * 2,信用保证金充值;CREDIT_MARGIN_RECHANGE
		if(CREDIT_MARGIN_RECHANGE.equalsIgnoreCase(taskTiggerPoint)){
			//获取充值任务参数，任务出发参数
			String taskParams=runTaskParams.getParams(CREDIT_MARGIN_RECHANGE);
			float old=Float.parseFloat(taskParams);
			float nowCreditMeony=Float.parseFloat(userTaskExec.getCreditMarginMeony());
			userTaskExec.setCreditMarginMeony((old+nowCreditMeony)+"");
			
			//更新任务字段中的数据
			updateUserTaskParams((old+nowCreditMeony)+"","CREDIT_MARGIN_MEONY");
			
		}
//		 * 3,社区冷柜开通押金;COMUNT_FREEZER_DEPOSIT
		if(COMUNT_FREEZER_DEPOSIT.equalsIgnoreCase(taskTiggerPoint)){
			//获取充值任务参数
			float old=runTaskParams.getParams(COMUNT_FREEZER_DEPOSIT);
//			float old=Float.parseFloat(taskParams);
			float nowCreditMeony=Float.parseFloat(userTaskExec.getComuntFreezerDeposit());
			userTaskExec.setComuntFreezerDeposit((old+nowCreditMeony)+"");
			
			//更新任务字段中的数据
			updateUserTaskParams((old+nowCreditMeony)+"","COMUNT_FREEZER_DEPOSIT");
			
			
		}
//		 * 4,消费账号充值;CONSUMER_RECHANGE
		if(CONSUMER_RECHANGE.equalsIgnoreCase(taskTiggerPoint)){
			//获取充值任务参数
			String taskParams=runTaskParams.getParams(CONSUMER_RECHANGE);
			float old=Float.parseFloat(taskParams);
			float nowCreditMeony=Float.parseFloat(userTaskExec.getConsumerRechange());
			userTaskExec.setConsumerRechange((old+nowCreditMeony)+"");
			
			//更新任务字段中的数据
			updateUserTaskParams((old+nowCreditMeony)+"","CONSUMER_RECHANGE");
			
		}
//		 * 5,帮借农户;HELP_FARMER
		if(HELP_FARMER.equalsIgnoreCase(taskTiggerPoint)){
			//获取充值任务参数
			String taskParams=runTaskParams.getParams(HELP_FARMER);
			int old=Integer.parseInt(taskParams);
			int nowCreditMeony=Integer.parseInt(userTaskExec.getHelpFarmer());
			userTaskExec.setHelpFarmer((old+nowCreditMeony)+"");
			
			//更新任务字段中的数据
			updateUserTaskParams((old+nowCreditMeony)+"","HELP_FARMER");
			
		}
//		 * 6,投资农业项目;INVST_PROJECT_MEONY
		if(INVST_PROJECT_MEONY.equalsIgnoreCase(taskTiggerPoint)){
			//获取充值任务参数
			float old=runTaskParams.getParams(INVST_PROJECT_MEONY);
			float nowCreditMeony=Float.parseFloat(userTaskExec.getInvstProjectMeony());
			userTaskExec.setInvstProjectMeony((old+nowCreditMeony)+"");
			
			//更新任务字段中的数据
			updateUserTaskParams((old+nowCreditMeony)+"","INVST_PROJECT_MEONY");
		}
		
//		 * 7,邀请的社员等级发生变化; EXECUT_TASK
		if(INVITE_MEMBER_NUMBER.equalsIgnoreCase(taskTiggerPoint)){
			
			//企业徽章编码  "LEVEL_1_1 ....."
			String taskParams=runTaskParams.getParams(INVITE_MEMBER_NUMBER);
			
			//{"HALT_READ":0,"READ":0,"BLUE":0,"GREEN":0,"SILBER":0}
			
			//获取当前社员的此类型的会数量，判断类型
			if(LEVEL_1_1.equals(taskParams)){
				
				int newValue=Integer.parseInt(userTaskExec.getHaltRead())+1;
				userTaskExec.setHaltRead(newValue+"");
				//更新任务字段中的数据
				updateUserTaskParams(newValue+"","HALT_READ");
			}
			
			if(LEVEL_2_1.equals(taskParams)){
				int newValue=Integer.parseInt(userTaskExec.getRead())+1;
				userTaskExec.setBlue(newValue+"");
				//更新任务字段中的数据
				updateUserTaskParams(newValue+"","READ");
			}
			if(LEVEL_2_2.equals(taskParams)){
				int newValue=Integer.parseInt(userTaskExec.getBlue())+1;
				userTaskExec.setBlue(newValue+"");
				//更新任务字段中的数据
				updateUserTaskParams(newValue+"","BLUE");
			}
			if(LEVEL_1_3.equals(taskParams)){
				int newValue=Integer.parseInt(userTaskExec.getGreen())+1;
				userTaskExec.setBlue(newValue+"");
				//更新任务字段中的数据
				updateUserTaskParams(newValue+"","GREEN");
			}
			if(LEVEL_1_4.equals(taskParams)){
				int newValue=Integer.parseInt(userTaskExec.getSilber())+1;
				userTaskExec.setBlue(newValue+"");
				//更新任务字段中的数据
				updateUserTaskParams(newValue+"","SILBER");
			}
			
		}//邀请的社员等级发生变化; EXECUT_TASK  END
		
		
		//检查是否达到目标等级，如果达到目标登记，则进行奖励
		//获取用户奖励徽章的名称；
		String rewardBadgeStr=null;
		if(taskObject!=null){
			rewardBadgeStr=processUserTaskTarget(db,taskObject,userTaskExec,memberId);
		}
		
		
		//如果当前社员已经拥有此类型的徽章，则不进行奖励
		String querySQL="SELECT ubg.id FROM mall_UserBadge AS ubg "+
				 " LEFT JOIN mall_EnterpriseBadge AS ebg ON ubg.enterprisebadgeid=ebg.id "+
				 " WHERE ebg.ent_badge_code='"+rewardBadgeStr+"' AND ubg.memberId='"+runTaskParams.getMemberId()+"'";
		
		MapList userBdgMap=db.query(querySQL);
		
		if(Checker.isEmpty(userBdgMap)&&rewardBadgeStr!=null){
			result=true;
			JSONObject rewParam=new JSONObject();
//			{"rewName":"奖励徽章","rewardParams":{"entBadgeCode":"Badge.MemberAuthorityLevel"}}
			
			logger.info("会员推广任务，奖励徽章:"+rewardBadgeStr);
			
			JSONObject rewParams=new JSONObject();
			
			rewParam.put("rewName", "奖励徽章");
			rewParams.put("entBadgeCode",rewardBadgeStr);
			rewParam.put("rewardParams",rewParams);

			
			this.taskparames.getRewardParams().add(rewParam);
			runTaskParams.addRewardTargetMember(runTaskParams.getMemberId());
			
			//更新会员当前任务参数
			userTaskExec.setCurrentLevel(rewardBadgeStr);
			
			//更新任务字段中的数据
			updateUserTaskParams(rewardBadgeStr,"CURRENT_LEVEL");
		}
		
		return result;
	}
	
	
	/**
	 * 计算当前用户达到的任务奖励级别,返回徽章编号
	 * @param db
	 * @param taskObject  任务目标集合
	 * @param userTaskExec  用户当前任务情况
	 * @return 徽章编号
	 * @throws JSONException 
	 */
	private String processUserTaskTarget(DB db,JSONObject taskObject,
			AuthorBadgeTaskTarget userTaskExec,String memberId) throws Exception {
		
		String rewBadge=null;
		//任务徽章key集合
		JSONArray badegSet=taskObject.getJSONArray("BADEG_SET");
		
		MapList memberMap=new MemberManager().getMemberById(memberId, db);
		
		if(!Checker.isEmpty(memberMap)){
			
			//1=消费者社员;2=单位消费者社员;3=生产者社员
			String memberType=memberMap.getRow(0).get("member_type");
			
			for(int i=0;i<badegSet.length();i++){
				//奖励徽章名
				String key=badegSet.getString(i);
				
				//检查社员类型
				if("3".equals(memberType)&&key.contains("LEVEL_1")){
					//如果是生产者社员，则跳过_1_徽章
					continue;
				}
				if(("1".equals(memberType)||"2".equals(memberType))&&key.contains("LEVEL_2")){
					//如果是消费者社员，则跳过生产者徽章
					continue;
				}
				
				
				//任务目标徽章
				AuthorBadgeTaskTarget taskTarget=new AuthorBadgeTaskTarget(taskObject.getJSONObject(key));
				
				if(taskTarget.checkTarget(userTaskExec,memberType)){
					rewBadge=key;
				}
			}
		}
		
		return rewBadge;
	}


	/**
	 * 获取会员权限徽章
	 * @param db
	 * @param memberId
	 * @param badgeManager
	 * @throws JDBCException
	 * @throws JSONException
	 */
	private AuthorityLevelBadgeParams getUserAutherBadge(DB db, String memberId,
			AMBadgeManager badgeManager) throws JDBCException, JSONException {
		AuthorityLevelBadgeParams authBadge=null;
		//根据社员类型设置社员徽章的数据
		MapList userBadgeMap=badgeManager.getUserBadgetMapList(db,memberId,BADGE_TASK_ENT_CODE);
		if(!Checker.isEmpty(userBadgeMap)){
			//社员徽章参数
			String badgeParame=userBadgeMap.getRow(0).get("badgeparame");
			//初始化社员徽章
			JSONObject badgeParameJSON=new JSONObject(badgeParame);
			authBadge=new AuthorityLevelBadgeParams(badgeParameJSON);
		}
		
		return authBadge;
	}
	
	@Override
	public String toString() {
		
		String taskStr="name:"+this.taskName+"\t classPath:"+this.taskClassPath+"\tid:"+this.id
				+"\t endID:"+this.entertaskid+"\tmemberID:"+this.getMemberId();
		
		return taskStr;
	}
	
	//am_badge_task
	@Override
	public String getTaskHtmlView(String taskID){
		String result=super.getTaskHtmlView(taskID);
		
		AuthorBadgeTaskTarget userTaskExec=new AuthorBadgeTaskTarget(this.taskparames.getTaskExecution());
		
		String currentLevel=userTaskExec.getCurrentLevel();
		
		String currentLevelName=memberLevelMap.get(currentLevel);
		
		result=result.replaceAll("\\$am_badge_task\\{CURRENT_CURRENT_LEVEL\\}",currentLevelName);
		
		return result;
	}

}
