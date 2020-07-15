package com.am.mall.reward;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月20日
 * @version 
 * 说明:<br />
 * 奖励规则管理类
 */
public class RewardManager {

	private static RewardManager rewardManager;
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	private RewardManager(){}
	
	public static RewardManager getInstance(){
		if(rewardManager==null){
			rewardManager=new RewardManager();
		}
		return rewardManager;
	}
	
	
	
	/**
	 * 根据ID获取奖励规则信息<br />
	 * 字段：<br />
	 *  id,name,explain,classpath,globalparam
	 * @param rewId  奖励规则ID
	 * @return  com.fastunit.Row  
	 */
	public Row getRewardManagerById(String rewId){
		Row row=null;
		try{
		
			DB db=DBFactory.getDB();
			
			String findRewSQL="SELECT * FROM mall_RewardRuleSetup WHERE id=? ";
			
			MapList map=db.query(findRewSQL,rewId,Type.VARCHAR);
			
			if(!Checker.isEmpty(map)){
				row=map.getRow(0);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return row;
	}
	
	
	/**
	 * 根据流程ID执行奖励规则
	 * @param rewrdId
	 * @return
	 */
	public boolean executeRewardById(String rewrdId){
		boolean result=false;
		return result;
	}
	
	/**
	 * 根据订单ID执行商品奖励规则
	 * @param 订单ID
	 * @return
	 */
	public boolean executeRewardByOrderId(String orderId){
		boolean result=false;
		
		DB db=null;
		
		try{
			
			db=DBFactory.newDB();
			
			MemberOrder order=new OrderManager().getMemberOrderById(orderId, db);
			
			//获取商品奖励信息
			MapList map=CommodityManager.getInstance().getCommodityRewardRuleById(order.getCommodityID(),db);
			
			//执行奖励
			if(!Checker.isEmpty(map)){
				logger.info(order.getMemberID()+"购买的商品"+order.getCommodityName()+"进行商品规则！");
				
				for(int i=0;i<map.size();i++){
					String clazzPath=map.getRow(i).get("classpath");
					
					if(!Checker.isEmpty(clazzPath)){
						IRewardRule reward=(IRewardRule)Class.forName(clazzPath).newInstance();
						
						RewardRuleParam rrp=new RewardRuleParam();
						rrp.memberId=order.getMemberID();
						rrp.orderId=order.getId();
						
						reward.execute(rrp);
					}
				}
			}else{
				logger.info(order.getMemberID()+"购买的商品"+order.getCommodityName()+"没有对应的奖励规则！");
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(db!=null){
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}	
			}
		}
		
		return result;
	}
	
}