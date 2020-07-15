package com.am.frame.task.reward.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.badge.AMBadgeManager;
import com.am.frame.task.instance.MemberAuthorityBadgeTask;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.am.frame.task.task.TaskEngine;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月15日
 * @version 
 * 说明:<br />
 * 奖励徽章
 */
public class RewardBadge implements ITaskReward {
	
	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	public boolean execute(RunTaskParams param){
		DB db=null;
		try{
			db=DBFactory.newDB();
			
			List<String> memberIds=param.getTargetMemberList();
			
			//企业徽章code
			String entBadgeCode=param.getRewParams().getJSONObject("rewardParams").getString("entBadgeCode");
			
			logger.info("会员奖励徽章 entBadgeCode："+entBadgeCode);
			
			AMBadgeManager badgeManager=new AMBadgeManager();
			
//			EnterpriseBadge entBadge=badgeManager.initBadgeTemplateByEntId(db, entBadgeCode);
//			BadgeTemplate badgeTemp=badgeManager.initBadgeTemplateById(db, entBadge.getBadgeTemplateID());
//			
//			String badgeClassPath=badgeTemp.getClassPath();
//			
//			BadgeImpl badgeImpl=null;
//			
//			if(!Checker.isEmpty(badgeClassPath)){
//				badgeImpl=(BadgeImpl) Class.forName(badgeClassPath).newInstance();
//				badgeImpl.initByEntBadgeCode(db, entBadgeCode);
//				
//			}
			
			//迭代奖励会员，进行奖励
			for(int i=0;i<memberIds.size();i++){
				
				String memberId=memberIds.get(i);
				
				//奖励企业徽章
				logger.info("为会员增加徽章 code："+entBadgeCode+"\t社员ID："+memberId);
				badgeManager.addBadgeByEntBadgeCode(db, entBadgeCode, memberId);
			}
			
			
			//理性农业业务 如果社员的等级发生变化，要触发直接邀请他的社员的徽章任务
			processLXNYInversit(db,param,entBadgeCode);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(db!=null){
				try {
					db.close();
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 检查触发当前社员的任务状态
	 * @param db
	 * @param param
	 * @param entBadgeCode  企业徽章编码
	 * @throws JDBCException
	 */
	private void processLXNYInversit(DB db, RunTaskParams param,String entBadgeCode) throws JDBCException {
		
		//奖励徽章的社员
		List<String> memberIds=param.getTargetMemberList();
		
		//查询别奖励这的直接上级，通过邀请码使用者账号查询的
		String querySQL="SELECT inc.am_MemberId "
				+ " FROM am_member AS m "
				+ " LEFT JOIN am_MemberInvitationCode AS inc ON m.register_inv_code_id=inc.id "
				+ " WHERE m.id=? "
				+ " AND inc.am_memberid IS NOT NULL ";
		
		for(int i=0;i<memberIds.size();i++){
			String currentMemberID=memberIds.get(i);
			MapList map=db.query(querySQL,currentMemberID,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				
				//任务目标社员ID
				String targetMember=map.getRow(0).get("am_memberid");
				
				if(!Checker.isEmpty(targetMember)){
					logger.info("社员等级发送变化，执行上级社员任务");
					
					//理性农业  会员权限徽章任务，具体权限参考需求 社员分类划分
					TaskEngine taskEngine=TaskEngine.getInstance();
					RunTaskParams params=new RunTaskParams();
					params.setTaskCode(MemberAuthorityBadgeTask.TASK_ECODE); //会员权限徽章任务
					
					//设置任务的触发点
					params.pushParam(MemberAuthorityBadgeTask.TRIGGER_POINT,MemberAuthorityBadgeTask.INVITE_MEMBER_NUMBER);
					
					//设置上级社员的等级
					params.pushParam(MemberAuthorityBadgeTask.INVITE_MEMBER_NUMBER,entBadgeCode);
					
					params.setMemberId(targetMember);
					taskEngine.executTask(params);
				}
			}
		}
		
	}

}
