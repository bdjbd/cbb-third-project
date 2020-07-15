package com.am.mall.order.evaluate;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/*
 * 商品订单评价查询
 * 
 * */
@SuppressWarnings("serial")
public class CommodityOrdereEvaluation implements SqlProvider{

	@Override
	public String getSql(ActionContext ac) {
		String sql = "SELECT am.membername,mm.ordercode,mc.* FROM "
				+ "mall_commodityorderevaluation AS mc "
				+ "LEFT JOIN mall_MemberOrder AS mm ON mc.OrderID = mm.id "
				+ "LEFT JOIN am_Member AS am ON mc.MemberID = am.id "
				+ "WHERE mm.memberid = mc.memberid";
		
		Row query = FastUnit.getQueryRow(ac, "am_bdp", "mall_commodityorderevaluation.query");
		if(query != null){
			String ordercode = query.get("ordercode");
			String membername = query.get("membername");
			
			if(!Checker.isEmpty(ordercode)){
				sql += " AND mm.ordercode LIKE '%"+ordercode+"%' ";
			}
			if(!Checker.isEmpty(membername)){
				sql += " AND am.membername LIKE '%"+membername+"%' ";
			}
			
		}
		sql += " ORDER BY mc.evaldatetime1 DESC ";
		
		return sql;
	}

}
