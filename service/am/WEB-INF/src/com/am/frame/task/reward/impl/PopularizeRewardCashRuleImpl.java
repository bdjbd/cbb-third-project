package com.am.frame.task.reward.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年8月3日
 * @version 
 * 说明:<br />
 * 推广奖励现金，奖励规则<br />
 * 奖励规则为：<br />
 * 根据社员等级不同,奖励金额不同，社员等级设置金额在后台配置，字段为：CONSUMER_DIVIDEND.REWAED_CASH,单位分  <br />
 * 
 * 注意：此功能获取的为当前任务触发的上级社员
 * 
 * @author 修改 wz 2017年01月12日12:26:22
 * 
 */
public class PopularizeRewardCashRuleImpl implements ITaskReward {

//	奖励次数：is_reward，0=新注册用户，1=老用户
//	完成任务完成时
//	1、判断is_reward=1，则验证最大邀请人数是否超出，未超出则更新已邀请人数并进行奖励
//	2、判断is_reward=0，则直接进行奖励
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean execute(RunTaskParams param) 
	{
		DB db = null;
		//任务触发社员，社员ID
		String memberId=param.getMemberId();
		//任务名称
		String runTaskName = param.getTaskName();
		
		boolean flag = false;
		
		//查询完成任务用户的上级用户所购买的邀请人数
		MapList vlist = null;
		//查询志愿者的等级信息
		MapList list  = null;
		//查询会员类型 1 老用户 0 新用户
		MapList memberInfoList = null;
		
		//1 老用户 0 新用户
		String is_reward = "0";
		
		int people_num = 0;
		int surplus_num = 0;
		
		String ssql ="";
		//直系上层会员id
		String upid = "";
		String membername = "";
		try
		{
			db = DBFactory.getDB();
			
			//查询会员类型 1 老用户 0 新用户
			ssql = "select * from am_member where id = '"+memberId+"'";
			memberInfoList = db.query(ssql);
			
			//志愿者层级信息
			ssql = "select * from consumer_dividend order by level";
			vlist = db.query(ssql);
			
			//查询会员层级关系
			ssql ="select * from am_member_distribution_map "
			    + " where "
			    + " sub_member_id = '"+memberId+"' order by level";
			MapList memberList = db.query(ssql);
			
			//赋值 是否是新用户或老用户
			if(!Checker.isEmpty(memberInfoList))
			{
				is_reward =memberInfoList.getRow(0).get("is_reward");
				membername =  memberInfoList.getRow(0).get("membername");
			}
			
			if(!Checker.isEmpty(memberList))
			{
				//赋值 直系上层memberID
				upid= memberList.getRow(0).get("member_id");
				
				//查询志愿者购买的等级数据
				ssql = "SELECT people_num,surplus_num FROM volunteers_record "
				+ " WHERE member_id='"+upid+"' ORDER BY create_time DESC";
				list = db.query(ssql);
				
				if(!Checker.isEmpty(list))
				{
					//赋值 可邀请人数
					people_num = list.getRow(0).getInt("people_num",0);
					//赋值 已邀请人数
					surplus_num = list.getRow(0).getInt("surplus_num",0);
					
					ssql = "select count(*) from am_member_distribution_map where member_id = '"+upid+"' and level = '1' and invitation_status = '1'";
					MapList countMap = db.query(ssql);
					
					//判断当前用户已邀请人数是否大于
					if(people_num - surplus_num >= countMap.getRow(0).getInt("count", 0))
					{
						//更新上下层关系
						updateRelationSql(db,upid,memberId);
						//邀请人数更新
						updateMemberSurplusNum(upid,is_reward,db);
					}
					
					//循环操作奖励
					for (int i = 1; i <= vlist.size(); i++) 
					{
						memberId = centReward(db,runTaskName,memberId,i+"",is_reward,membername);
					}
					
					
					
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(db!=null)
			{
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
	 * 判断老用户 是否执行奖励
	 * @param db
	 * @param runTaskParams
	 * @param sub_member_id
	 * @param level
	 * @param type 1 老用户 0 新用户
	 */
	private String centReward(DB db,String runTaskParams,String member_id,String level,String type,String membername) throws Exception
	{
		//根据下级会员ID查询直系上层会员ID
		String sql = "select * from am_member_distribution_map "
				   + " where sub_member_id = '"+member_id+"' and level = '1'";
		MapList list = db.query(sql);
		
		String upid = "";
		
		if("1".equals(type))
		{
			updateUpMemberRelation(db,member_id);
		}
		
		if(!Checker.isEmpty(list))
		{
			String invitation_status =  list.getRow(0).get("invitation_status");
			//上下级关系是否为true 为true 则执行奖励
			if("1".equals(invitation_status))
			{
				runReward(db,list.getRow(0).get("member_id"),level+"",runTaskParams,membername);
			}
			upid  = list.getRow(0).get("member_id");
		}
		
		return upid;
	}
	
	/**
	 * 执行奖励方法
	 * @param rewardMemberId 奖励用户id
	 * @param level 等级
	 * @param runTaskParams 任务名称
	 */
	private void runReward(DB db,String rewardMemberId,String level,String runTaskParams,String membername) throws Exception
	{
		//查询志愿者的等级信息
		String sql ="select * from consumer_dividend where level = '"+level+"'";
		
		
		MapList list = db.query(sql);
		sql = "select * from am_member where id ='"+rewardMemberId+"'";
		MapList memberList = db.query(sql);
		String remarks = "";
		
		if(!Checker.isEmpty(list))
		{
			//可邀请人数大于已邀请人数，奖励
			//奖励金额，单位元
			long rewaedCash=list.getRow(0).getLong("rewaed_cash",0);

			remarks=list.getRow(0).get("level_name")+membername+"完成"+runTaskParams
					+",奖励现金："+rewaedCash/100+"元。";
			
			logger.info("奖励社员id:"+rewardMemberId+
					"\t奖励金额，单位元:"+rewaedCash+
					"\t社员层级:"+level);
			logger.info(remarks);
			
			VirementManager vm=new VirementManager();
			
			double virementNumber=rewaedCash/100;
			
			vm.execute(db, "", rewardMemberId,"", SystemAccountClass.VOLUNTEER_ACCOUNT, virementNumber+"", remarks, "", "",false);
			
		}
		
	}
	
	/**
	 * 执行更新上级关系操作
	 * @param sub_member_id
	 */
	private void updateUpMemberRelation(DB db,String sub_member_id) throws Exception
	{
		//根据下级会员ID查询直系上层会员ID
		String sql = "select * from am_member_distribution_map "
				   + " where sub_member_id = '"+sub_member_id+"' and level = '1'";
		
		MapList upList = db.query(sql);
		
		String invitation_status = "";
		String memberId = "";
		
		if(!Checker.isEmpty(upList))
		{
			//当前会员与上层会员的关系
			invitation_status = upList.getRow(0).get("invitation_status");
			memberId = upList.getRow(0).get("member_id");
			
			
			//判断当前会员与上层会员的关系
			if("1".equals(invitation_status))
			{
				updateDownMemberRelation(db,sub_member_id,memberId);
				updateUpMemberRelation(db,memberId);
				
			}
			
		}
	}
	
	/**
	 * 执行下级用户关系更新操作
	 * @param member_id 上级会员ID
	 */
	private void updateDownMemberRelation(DB db,String SubMemeberID,String memberId ) throws Exception
	{
		String sql = "select * from am_member_distribution_map "
				   + " where member_id = '"+SubMemeberID+"' order by level = '1'"; 
		
		MapList downList = db.query(sql);
		String invitation_status = "";
		String sub_member_id = "";
		
		if(!Checker.isEmpty(downList))
		{
			
			for (int i = 0; i < downList.size(); i++) 
			{
				invitation_status = downList.getRow(i).get("invitation_status");
				sub_member_id = downList.getRow(i).get("sub_member_id");
				
				if("1".equals(invitation_status))
				{
					logger.info("更新下级会员状态");
					updateRelationSql(db,memberId,downList.getRow(i).get("sub_member_id"));
					updateDownMemberRelation(db,downList.getRow(i).get("sub_member_id"),memberId);
				}
			}
		}
	}
	
//	/**
//	 * 更新会员依赖关系
//	 * @param member_id 上级会员id
//	 * @param sub_member_id 下级会员ID
//	 */
//	private void updateRelationSql(DB db,String member_id) throws Exception
//	{
//		String usql = "update am_member_distribution_map set invitation_status = '1'"
//					+ " where sub_member_id = '"+member_id+"' ";
//		
//		db.execute(usql);
//	}
	
	/**
	 * 更新会员依赖关系
	 * @param member_id 上级会员id
	 * @param sub_member_id 下级会员ID
	 */
	private void updateRelationSql(DB db,String member_id,String sub_member_id) throws Exception
	{
		String usql = "update am_member_distribution_map set invitation_status = '1'"
					+ " where member_id = '"+member_id+"' "
					+ " and sub_member_id = '"+sub_member_id+"'";
		
		db.execute(usql);
	}
	
	
	/**
	 * 更新邀请用户的可邀请人数
	 * @param memberId  会员ID
	 * @param times 已邀请次数
	 * @param db
	 * @throws JDBCException 
	 */
	private void updateMemberSurplusNum(String memberId,String times, DB db) throws JDBCException {
		String updateSQL="UPDATE volunteers_record SET surplus_num=COALESCE(surplus_num,0)+? "
				+ " WHERE member_id=?";
		db.execute(updateSQL, new String[]{
				times,memberId
		}, new int[]{
				Type.INTEGER,Type.VARCHAR
		});
	}
	
}
