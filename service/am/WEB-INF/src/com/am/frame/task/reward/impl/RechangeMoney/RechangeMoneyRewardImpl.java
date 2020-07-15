package com.am.frame.task.reward.impl.RechangeMoney;

import java.util.UUID;

import com.am.frame.task.params.RunTaskParams;
import com.am.frame.task.task.ITaskReward;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;

/** 
 * @author  wz  
 * @descriptions 
 * @date 创建时间：2016年4月7日 下午12:18:19 
 * @version 1.0   
 */
public class RechangeMoneyRewardImpl implements ITaskReward{

	@Override
	public boolean execute(RunTaskParams param) {
		
		DB db;
		try {
			
			db = DBFactory.newDB();
			
			String orderId=param.getMemberOrderId();
			
			String querOrderSQL="SELECT * FROM am_Member WHERE id=? ";
			
			MapList orderMap=db.query(querOrderSQL,orderId,Type.VARCHAR);
			//更新任务参数 更新奖励次数
			param.pushParam("alreadyRechange", Integer.parseInt(param.getParams("alreadyRechange").toString())+1);
			
			//添加积分记录表数据
			String usql = "insert into am_score_detail (id,member_id,s_type,score_value,remark,create_time,business) "
					+ " values('"+UUID.randomUUID()+"','"+param.getMemberId()+"','0','"+param.getRewParams().getString("rewardsPoints")+"','','new Data()','')";
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		return false;
	}

}
