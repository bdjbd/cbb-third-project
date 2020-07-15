package com.am.frame.task.reward.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.bonus.implementationclass.BonusJobclassImpl;
import com.am.frame.member.MemberManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.am.frame.transactions.detail.AfterDetailBean;
import com.am.frame.transactions.detail.TransactionDetail;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.MapList;
import com.fastunit.Row;
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
 */
public class PopularizeRewardCashRuleImplV02 implements ITaskReward {

//	奖励次数：is_reward，0=新注册用户，1=老用户
//	完成任务完成时
//	1、判断is_reward=1，则验证最大邀请人数是否超出，未超出则更新已邀请人数并进行奖励
//	2、判断is_reward=0，则直接进行奖励
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public boolean execute(RunTaskParams param) {
		
		if(param!=null){
			
			DB db=null;
			
			try{
				
				db=DBFactory.newDB();
				
				//任务触发社员，社员ID
				String memberId=param.getMemberId();
				
				
				
				MemberManager mm=new MemberManager();
				
				//任务触发社员MapList
				MapList taskMemberMap=mm.getMemberById(memberId, db);
				
				//奖励次数：is_reward，0=新注册用户，1=老用户
				String isReward=null;
				if(!Checker.isEmpty(taskMemberMap)){
					isReward=taskMemberMap.getRow(0).get("is_reward");
				}
				
				//查询当前社员的上级（member_id），上级的等级(level)，已将上级的奖励金额(rewaed_cash)
				StringBuilder querySQL=new StringBuilder();
				//此SQL 查询当前人数的上三层社员
				querySQL.append("SELECT mmap.member_id,mmap.sub_member_id,cd.level,cd.level_name,cd.rewaed_cash  ");
				querySQL.append("	,m.membername ");
				querySQL.append("	FROM am_member_distribution_map AS mmap                                        ");
				querySQL.append("	LEFT JOIN consumer_dividend AS cd ON mmap.level=cd.level                       ");
				querySQL.append("   LEFT JOIN am_member AS m ON m.id=mmap.sub_member_id ");
				querySQL.append("	WHERE sub_member_id=?                    ");
				querySQL.append("   AND cd.status=1                          ");
				querySQL.append("	ORDER BY cd.level                        ");
				
				
				logger.info("推广奖励现金，奖励规则,任务触发社员ID："+memberId);
				
				MapList upMemberMap=db.query(querySQL.toString(),memberId, Type.VARCHAR);
				
				String  querySQLStr="SELECT people_num,surplus_num FROM volunteers_record "
						+ " WHERE member_id=? ORDER BY create_time DESC";
				
				if(!Checker.isEmpty(upMemberMap)){
					String remarks="";
					
					for(int i=0;i<upMemberMap.size();i++){
						
						Row row=upMemberMap.getRow(i);
						
						//奖励社员id   第一层为直接上级，以此类推分别为第二层，第三层。
						String rewardMemberId=row.get("member_id");
						
						//邀请人数验证，值验证直接上级的邀请人数
						if(i==0)
						{
							//奖励次数：is_reward，0=新注册用户，1=老用户
							if("1".equals(isReward)){
								//检查需要奖励的人是否购买了邀请数量，如果已经邀请数量小于可邀请数量，则奖励，否则不奖励
								MapList rewardMap=db.query(querySQLStr, rewardMemberId,Type.VARCHAR);
								if(!Checker.isEmpty(rewardMap)){
									//可邀请人数
									double people_num=rewardMap.getRow(0).getDouble("people_num", 0);
									//已邀请人数
									double surplus_num=rewardMap.getRow(0).getDouble("surplus_num", 0);
									
									if(people_num>=surplus_num){//
										logger.info("社员："+rewardMemberId+"，可邀请人数："+people_num+"，已邀请人数："+surplus_num
												+"，已无可用可邀请人数，无法奖励。");
										
										//如果直接上级可邀请人数不够，则其它上级也不奖励
										return false;
									}
								}
							}
							
							//邀请人数更新
							updateMemberSurplusNum(memberId,isReward,db);
							
						}//直接上级验证 end
						
						
						
						//可邀请人数大于已邀请人数，奖励
						//奖励金额，单位元
						long rewaedCash=row.getLong("rewaed_cash",0);
						//社员层级
						String level=row.get("level");
						
						remarks=row.get("level_name")+row.get("membername")+"完成"+param.getTaskName()
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
		}
		
		return false;
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
				Type.VARCHAR,Type.VARCHAR
		});
	}


	private void addTransRecord(DB db,long priceMoney,TransactionDetail trdl,
			AfterDetailBean bdb,String accountCode,String remark,String memberId) throws JDBCException{
		String uuid=UUID.randomUUID().toString();
		
		//1=支出，只要是支出均是负数
		//2=收入，只要是收入均是正数
		String tradeType = "2";
		
		//账号类型
		String AccountType = BonusJobclassImpl.getInstance().returnAccountTypeId(accountCode,db);
		
		//账号id
		String Accountid = BonusJobclassImpl.getInstance().returnAccountinfoId(AccountType, memberId, db);
		
		bdb.setTableRow(bdb.getTranTable().addInsertRow());
		bdb.setId(uuid);
		
		bdb.setAccount_id(Accountid);
		bdb.setMember_id(memberId);
		bdb.setRmarks(remark);
		bdb.setTrade_total_money(priceMoney);
		bdb.setCounter_fee(0);
		bdb.setSa_class_id(AccountType);
		bdb.setTrade_type(tradeType);
		bdb.setTrade_state("1");
		
		trdl.earningActions(db, bdb);
	}
	

}
