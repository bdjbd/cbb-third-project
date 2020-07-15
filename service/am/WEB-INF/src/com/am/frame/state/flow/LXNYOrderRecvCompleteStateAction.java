package com.am.frame.state.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.frame.state.OrderFlowParam;
import com.am.frame.state.serverAction.OrderCompleteStateAction;
import com.am.frame.systemAccount.SystemAccountClass;
import com.am.frame.transactions.virement.VirementManager;
import com.am.mall.order.OrderManager;
import com.fastunit.MapList;
import com.fastunit.Var;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;

public class LXNYOrderRecvCompleteStateAction extends OrderCompleteStateAction {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(OrderFlowParam ofp) {
		logger.info("理性农业订单完成收货，");
		DB db=null;
		
		try{
			db=DBFactory.newDB();
			
			String memberId="";
			String memberNanme="";
			String commodityname="";
			String distServiceMemberId="";
			
			//食品安全费用到食品安全帐户
			double foodFreeRatio=Var.getDouble("COMMODITY_RECV_FOOLD_RATIO", 0);
			
			OrderManager orderManager=new OrderManager();
			MapList meberOrderMap=orderManager.getMemberOrderMapById(ofp.orderId, db);
			
			//更新订单完成时间
			String Update_Order_completedate_SQL = "UPDATE mall_memberorder SET completedate=now() WHERE id='"+ofp.orderId+"'";
			db.execute(Update_Order_completedate_SQL);
			
			
			
			MemberManager mm=new MemberManager();
			
			
			double orderPrice=0;
			
			if(!Checker.isEmpty(meberOrderMap)){
				orderPrice=meberOrderMap.getRow(0).getDouble("ftotalprice", 0);
				memberId=meberOrderMap.getRow(0).get("memberid");
				commodityname=meberOrderMap.getRow(0).get("commodityname");
				
				MapList memberMap=mm.getMemberById(memberId, db);
				if(!Checker.isEmpty(memberMap)){
					memberNanme=memberMap.getRow(0).get("membername");
				}
				
			}
			
			//食品安全账号存入比例
			double foodFree=foodFreeRatio*orderPrice;
			
			String iremakers="购买"+commodityname+"，食品安全账户入账。";
			
			VirementManager vm=new VirementManager();
			
			vm.execute(db, "",memberId ,"",
					SystemAccountClass.FOOD_SAFETY_TRACING_ACCOUNT, foodFree+"", iremakers, "", "", false);
			
			//总联合社食品安全账号进账
			iremakers=memberNanme+"购买"+commodityname+",食品追溯安全账户入账。";
			vm.execute(db, "","org" ,"",
					SystemAccountClass.GROUP_FOOD_SAFETY_TRACING_ACCOUNT, foodFree+"", iremakers, "", "", false);
			
			
			
			//服务人员获取订单费用
			//查询订单的配送人员
			String querySQL="SELECT  cd.area_dc_member_id,od.orderState,cd.area_dc_member_id,spec.SpecImages "+                      	
							"   FROM mall_MemberOrder AS od                   "+                   	
							"   LEFT JOIN mall_CommodityDistribution AS cd ON od.id=cd.orderid 	  "+
							"   LEFT JOIN mall_store AS s ON cd.acceptorderid=s.id      "+         	
							"   LEFT JOIN am_member AS m ON od.memberid=m.id  "+
							"   LEFT JOIN mall_CommoditySpecifications AS spec ON spec.id=od.SpecID  "+          	
							"   WHERE  od.id=? ";
			
			MapList orderDistMap=db.query(querySQL,ofp.orderId, Type.VARCHAR);
			
			if(!Checker.isEmpty(orderDistMap)){
				distServiceMemberId=orderDistMap.getRow(0).get("area_dc_member_id");
			}
			if(!Checker.isEmpty(distServiceMemberId)){	
				//服务人员获取订单费用比例
				double serviceFreeRatio=Var.getDouble("ORDER_SERVICE_RATIO", 0);
				double serviceFree=serviceFreeRatio*orderPrice;
				iremakers="配送"+commodityname+"，获得服务费。";
				
				vm.execute(db, "",distServiceMemberId ,"",
						SystemAccountClass.CASH_ACCOUNT, serviceFree+"", iremakers, "", "", false);
			}
			
			//2,更新社员得消费金额
			String updateSQL="UPDATE am_member_distribution_map "
					+ " SET monetary=COALESCE(monetary,0)+? WHERE sub_member_id=? ";
			
			db.execute(updateSQL,new String[]{
					VirementManager.changeY2F(orderPrice)+"",memberId
			},new int[]{Type.DECIMAL,Type.VARCHAR});
			
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
		
		return super.execute(ofp);
	}

	
	
}
