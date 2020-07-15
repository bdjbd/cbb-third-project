package com.am.mall.reward.rule;

import com.am.frame.member.MemberManager;
import com.am.mall.beans.order.MemberOrder;
import com.am.mall.commodity.CommodityManager;
import com.am.mall.order.OrderManager;
import com.am.mall.reward.IRewardRule;
import com.am.mall.reward.RewardRuleParam;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月20日
 * @version 
 * 说明:<br />
 * 购买人交易成功后，该规则依据购买人购买金额计算相应积分，并增加购买人积分值；
 * 参数：现金兑换积分比例 :0.001，取值0-1;
 */
public class PurchaserIntegral implements IRewardRule{
	
	private static final String EXCHANGE_RATIO_NAME="现金兑换积分比例";
	
	@Override
	public double execute(RewardRuleParam rrp) {
		
		DB db=null;
		try{
			
			db=DBFactory.newDB();
			
			MemberOrder order=new OrderManager().getMemberOrderById(rrp.orderId, db);
			MapList map=CommodityManager.getInstance().getCommodityRewardRuleOfCommodityById(order.getCommodityID(),db);
			
			if(!Checker.isEmpty(map)){
				Row row=map.getRow(0);
				
				//现金兑换积分比例 ： 0.001，
				String params=row.get("globalparam");
				// 处理参数，获取比积分比例
				int score=getSocre(params,order.getSalePrice());
				//给会员添加积分
				MemberManager memberManager=new MemberManager();
				memberManager.updateMemberScore(score,order.getMemberID());
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
		
		return 0;
	}

	
	private int getSocre(String param, double salePrice) {
		int score=0;
		
		if(param!=null&&param.contains(EXCHANGE_RATIO_NAME)){
			
			String[] params=param.split(";");
			
			double ratio=Double.valueOf(params[0].split(":")[1]);
			
			score=(int)(salePrice*ratio);
		}
		
		if(score<0){
			score=0;
		}
		
		return score;
	}

}
