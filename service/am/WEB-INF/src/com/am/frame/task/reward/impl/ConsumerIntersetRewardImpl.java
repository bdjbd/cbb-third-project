package com.am.frame.task.reward.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.task.instance.ConsumerInterestTask;
import com.am.frame.task.instance.entity.InstanceMember;
import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.am.mall.beans.order.MemberOrder;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月16日
 * @version 
 * 说明:<br />
 * 消费分利奖励
 * 分利奖励数据由分利配置中配置，任务参数不做奖励分值的管理
 */
public class ConsumerIntersetRewardImpl implements ITaskReward {

	@Override
	public boolean execute(RunTaskParams param) {
		
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			
			//1,获取分利会员信息
			//2,进行分利
			List<InstanceMember> instMemberSet=param.getParams(
					ConsumerInterestTask.CONSUMER_INSTEREST_MEMBERSET);
			
			String orderId=param.getMemberOrderId();
			
			String querOrderSQL="SELECT * FROM mall_MemberOrder WHERE id=? ";
			
			MapList orderMap=db.query(querOrderSQL,orderId,Type.VARCHAR);
			
			MemberOrder order=null;
			
			if(!Checker.isEmpty(orderMap)){
				order=new MemberOrder(orderMap.getRow(0));
			}
			
			//更新会员的分利金额
			updateMemberConsumerInterset(db,instMemberSet,order);
			
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
	 * 为会员执行分利
	 * @param db
	 * @param instanceMember
	 * @throws JDBCException 
	 */
	protected void updateMemberConsumerInterset(DB db,
			List<InstanceMember> instMemberSet,MemberOrder order) throws JDBCException {
		
		//会员获取的分利额=订单的总售价*消费分利比例；
		String  updateConsumerScoreSQL="UPDATE mall_account_info SET "
				+ " total_amount=COALESCE(total_amount,0)+?,"
				+ " balance=COALESCE(balance,0)+? WHERE member_orgid_id=? "
				+ " AND a_class_id=? ";
		
		//获取分销账号ID
		String aClasssId="";
		MapList classIdMap=db.query("SELECT id FROM mall_system_account_class "
				+ " WHERE sa_code='"+SystemAccountClass.BONUS_ACCOUNT+"' ");
		if(!Checker.isEmpty(classIdMap)){
			aClasssId=classIdMap.getRow(0).get("id");
		};
		
		//分销账户 分销获取的分利额
		String rebateAct="0";
		//更新用户账号金额SQL
		List<String[]> updateParams=new ArrayList<String[]>();
		
		
		//交易记录参数
		List<String[]> insertParams=new ArrayList<String[]>();
		String insertRecordSQL="INSERT INTO mall_trade_detail( "+
            " id, member_id,sa_class_id, account_id, trade_time, trade_total_money, counter_fee, "+
            " rmarks, create_time,business_id)"+
            " VALUES (?, ?,?,(SELECT id FROM mall_account_info "
            + "		WHERE member_orgid_id=? "
            + "    AND  a_class_id='"+aClasssId+"'),"+
            " now(), ?, 0, "+
            "  ?, now(),?) ";
		
		if(!Checker.isEmpty(instMemberSet)&&order!=null){
			for(int i=0;i<instMemberSet.size();i++){
				InstanceMember item=instMemberSet.get(i);
				
				//获取分销分利比例
				double ratiol=getRatiol(item.instRatiol);
				
				//消费获取的分利金额 单位未分
				rebateAct=((long)Math.floor(order.getTotalPrice()*ratiol*100))+"";
				
				updateParams.add(new String[]{
						rebateAct,rebateAct,
						item.memberId,//会员id
						aClasssId//帐号分类id
						});
				
				//交易记录
				insertParams.add(new String[]{
						UUID.randomUUID().toString(),
						item.memberId,aClasssId,item.memberId,rebateAct,
						"会员购买商品，获取分利。",order.getId()
				});
			}
		}
		
		db.executeBatch(updateConsumerScoreSQL, 
				updateParams,
				new int[]{Type.INTEGER,Type.INTEGER,Type.VARCHAR,Type.VARCHAR});
		
		db.executeBatch(insertRecordSQL, insertParams,
				new int[]{
					Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,Type.VARCHAR,
					Type.INTEGER,Type.VARCHAR,Type.VARCHAR
		});
	}


	/**
	 * 获取分销比例
	 * @param instRatiol  分销比例
	 * @return
	 */
	private double getRatiol(String instRatiol){
		double result=0;
		if(!Checker.isEmpty(instRatiol)){
			if(instRatiol.contains("%")){
				result=Double.parseDouble(instRatiol.subSequence(0, 
						instRatiol.indexOf("%")).toString());
				result=result/100;
			}else{
				result=Double.parseDouble(instRatiol);
			}
		}
		return result;
	}

}
