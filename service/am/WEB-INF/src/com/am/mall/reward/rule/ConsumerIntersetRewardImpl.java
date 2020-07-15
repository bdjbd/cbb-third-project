package com.am.mall.reward.rule;

import com.am.frame.member.MemberManager;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.am.mall.order.OrderManager;
import com.am.mall.reward.IRewardRule;
import com.am.mall.reward.RewardRuleParam;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;


/**
 * 商品消费分利奖励
 * @author yuebin
 *1，根据分利设置，对上级进行分利
 *
 */
public class ConsumerIntersetRewardImpl implements IRewardRule {

	@Override
	public double execute(RewardRuleParam rrp) {
		
		//1,查询订单信息
		//2,查询上三级社员
		//3,按照分利比例对上级进行分利
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			String memberId=rrp.memberId;
			String memberName="";
			String orderId=rrp.orderId;
			
			MemberManager mm=new MemberManager();
			
			MapList memberMap=mm.getMemberById(memberId, db);
			if(!Checker.isEmpty(memberMap)){
				memberName=memberMap.getRow(0).get("membername");
			}
			
			OrderManager orderManager=new OrderManager();
			MapList meberOrderMap=orderManager.getMemberOrderMapById(orderId, db);
			
			double orderPrice=0;
			
			if(!Checker.isEmpty(meberOrderMap)){
				orderPrice=meberOrderMap.getRow(0).getDouble("ftotalprice", 0);
			}
			
			//查询上三级社员
			String querySQL="SELECT trim(to_char("+orderPrice+"*cd.consume_ratio,'9999999990D99')) AS consum,cd.consume_ratio,  "+
							"  md.member_id  "+
							"  FROM consumer_dividend AS cd   "+
							"  LEFT JOIN am_member_distribution_map AS md ON cd.level=md.level  "+
							"  WHERE md.sub_member_id=? AND md.invitation_status='1' "+
							"  ORDER BY md.level  "+
							"  LIMIT 3  ";
			MapList distMap=db.query(querySQL,memberId,Type.VARCHAR);
			
			if(!Checker.isEmpty(distMap)){
				
				VirementManager vm=new VirementManager();
				String remarks=memberName+"购买商品，获取分利。";
				for(int i=0;i<distMap.size();i++){
					Row row=distMap.getRow(i);
					
					vm.execute(db, "",row.get("member_id") ,"",
							SystemAccountClass.VOLUNTEER_ACCOUNT, row.get("consum"), remarks, "", "", false);
				}
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
		
		return 0;
	}

}
