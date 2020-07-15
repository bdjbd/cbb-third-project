package com.am.mall.order.stateFlow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.mall.order.IFlowState;
import com.am.mall.order.OrderFlowParams;
import com.am.mall.reward.RewardManager;
import com.fastunit.jdbc.DB;

/**
 * @author Mike
 * @create 2014年11月20日
 * @version 
 * 说明:<br />
 * 说明：订单已服务未评价
 * 7=订单完成
 * 下一状态值：8
 * 处理过程：更新状态值
 * 0、	调用该商品相应奖励规则
 * 
 */
public class OrderCompleteStateAction implements IFlowState {

	private static Logger logger=LoggerFactory.getLogger("com.am.mall.order.stateFlow.OrderCompleteStateAction");
	
	@Override
	public String execute(OrderFlowParams param) {
		DB db=null;
		try{
			//执行默认状态，既跳转到下一个环节
			logger.info("订单完成完成动作执行Action");
			DefaultFlowStateAction defaultFlowAction=new DefaultFlowStateAction();
			defaultFlowAction.stepping(param);
			
			//执行奖励规则
			executeCommoditReward(db,param);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	
	private void executeCommoditReward(DB db,OrderFlowParams param) {
		
		logger.info("执行订单完成商品奖励规则");
		RewardManager.getInstance().executeRewardByOrderId(param.orderId);
		
		//检查当前会员的所有上级，判断是否启用了消费分利，如果启用了，\
		//则执行每个上级的消费分利任务
		executeConsumerIntersetReward(db,param);
	}

	
	
	/**
	 * 检查当前会员的所有上级，判断是否启用了消费分利，如果启用了，
	 * 则执行每个上级的消费分利任务
	 * @param db DB
	 * @param param OrderFlowParams
	 */
	private void executeConsumerIntersetReward(DB db, OrderFlowParams param) {
	}


	private void executeConsumerIntersetReward(OrderFlowParams param) {
		
	}

}
