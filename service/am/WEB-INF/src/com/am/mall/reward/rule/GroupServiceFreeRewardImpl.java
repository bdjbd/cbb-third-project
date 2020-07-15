package com.am.mall.reward.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.mall.order.OrderManager;
import com.am.mall.reward.IRewardRule;
import com.am.mall.reward.RewardRuleParam;
import com.am.mall.services.MallService;
import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * 商品服务费奖励规则。分配说明如下：
 * 1,根据当前下单人所在的区县为主，向服务中心抽取服务费，
 * 		服务中心为区县服务中心，市服务中心，省服务中心，以前总服务中心。
 * 2,如果对应的服务中心不存在，则现将数据保存到服务费记录缓存表中，待服务中心创建完成后，
 * 		再向其服务中心转账。
 * @author yuebin
 *
 */
public class GroupServiceFreeRewardImpl implements IRewardRule {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public double execute(RewardRuleParam rrp) {
		//订单购买会员ID
		String memberId=rrp.memberId;
		//购买商品的订单ID
		String orderId=rrp.orderId;
		
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			double orderPrice=0;
			String memberName="";
			//省
			String province="";
			//市
			String city="";
			//区县
			String zone="";
			//购买商品名称
			String commodityName="";
			
			OrderManager ordeManager=new OrderManager();
			
			
			MemberManager mm=new MemberManager();
			MapList memberMap=mm.getMemberById(memberId, db);
			if(!Checker.isEmpty(memberMap)){
				memberName=memberMap.getRow(0).get("membername");
				province=memberMap.getRow(0).get("province");
				city=memberMap.getRow(0).get("city");
				zone=memberMap.getRow(0).get("zzone");
			}
			
			OrderManager orderManager=new OrderManager();
			MapList meberOrderMap=orderManager.getMemberOrderMapById(orderId, db);
			
			if(!Checker.isEmpty(meberOrderMap)){
				//订单价格，单位元，小数
				orderPrice=meberOrderMap.getRow(0).getDouble("ftotalprice", 0);
				commodityName=meberOrderMap.getRow(0).get("commodityname");
			}
			
			
			//执行服务费分配
			MallService ms=new MallService();
			ms.reward(db,orderPrice,province,city,zone, commodityName);
			logger.info("执行购买商品服务费分配："+commodityName+"  orderPrice:"+orderPrice);
			
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
		
		return 0;
	}


}
