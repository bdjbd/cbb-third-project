package com.am.mall.order.distnbution;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;
/**
 * 
 * 订单接单查询SqlProvider
 * */

public class DistnbuSqlProvider implements SqlProvider{

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String orgid=ac.getVisitor().getUser().getOrgId();
		
		String sql=
				"SELECT distinct od.*,m.membername,cd.area_dc_member_id,s.*                           "+
				"	FROM mall_MemberOrder AS od                                    "+
				"	LEFT JOIN mall_CommodityDistribution AS cd ON od.id=cd.orderid "+
				"	LEFT JOIN mall_store AS s ON cd.acceptorderid=s.id             "+
				"   LEFT JOIN mall_distribution_center AS dc ON dc.orgid=cd.acceptorderid "+
				"	LEFT JOIN am_member AS m ON od.memberid=m.id                   "+
				"	WHERE dc.orgid LIKE '"+orgid+"%'   ";
				
				
		//查询行
		Row query = FastUnit.getQueryRow(ac, "am_bdp", "mall_commodityorder.query");
		if(query!=null){
			
			String ordercode=query.get("ordercode");
			String membername=query.get("membername");
			//商品名称
			String name=query.get("name");
			String contact=query.get("contact");
			
			if(!Checker.isEmpty(ordercode)){
				sql+=" AND od.ordercode like '%"+ordercode+"%' ";
			}
			if(!Checker.isEmpty(membername)){
				sql+=" AND m.membername like '%"+membername+"%'";
			}
			if(!Checker.isEmpty(name)){
				sql+=" AND s.commodityname like '%"+name+"%' ";
			}
			if(!Checker.isEmpty(contact)){
				sql+=" AND s.contact like '%"+contact+"%'";
			}
		}

		sql+=" AND od.orderstate IN ('30','401','402') "
				+ " ORDER BY disttime DESC ";
		
		return sql;
	}
	

}
