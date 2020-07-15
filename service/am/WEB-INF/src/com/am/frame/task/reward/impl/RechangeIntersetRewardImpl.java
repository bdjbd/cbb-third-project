package com.am.frame.task.reward.impl;

import java.util.ArrayList;
import java.util.List;

import com.am.frame.task.instance.entity.InstanceMember;
import com.am.mall.beans.order.MemberOrder;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年3月17日
 * @version 
 * 说明:<br />
 * 充值分利奖励 
 */
public class RechangeIntersetRewardImpl extends ConsumerIntersetRewardImpl{

	
	/**
	 * 为会员执行分利
	 * @param db
	 * @param instanceMember
	 * @throws JDBCException 
	 */
	@Override
	protected void updateMemberConsumerInterset(DB db,
			List<InstanceMember> instMemberSet,MemberOrder order) throws JDBCException {
		//会员获取的分利额=订单的总售价*充值分利比例；
		String updateConsumerScoreSQL="UPDATE am_Member SET "
				+ " rebate_act=COALESCE(rebate_act,0)+ ?*? "
				+ " WHERE id=? ";
		
		List<String[]> updateParams=new ArrayList<String[]>();
		
		if(!Checker.isEmpty(instMemberSet)&&order!=null){
			for(int i=0;i<instMemberSet.size();i++){
				InstanceMember item=instMemberSet.get(i);
				updateParams.add(new String[]{
						order.getTotalPrice()+"",//订单总价格
						item.packageRatiol,//充值分利比例
						item.memberId//会员id
						});
			}
		}
		
		db.executeBatch(updateConsumerScoreSQL, 
				updateParams,
				new int[]{Type.DOUBLE,Type.DOUBLE,Type.VARCHAR});
	}
	
}
