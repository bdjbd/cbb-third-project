package com.am.mall.order.distnbution;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年4月30日
 * @version 
 * 说明:<br />
 * 配送中心接单sqlProvider
 */
public class DCDistributionSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
//		String sql=" SELECT distinct od.*,dc.gap_name,m.membername ,dc.orgid AS dcorgid  "+              
//				" FROM mall_MemberOrder AS od                         "+           
//				" LEFT JOIN mall_CommodityDistribution AS cd ON od.id=cd.orderid  "+
//				"  LEFT JOIN mall_distribution_center AS dc ON dc.orgid=cd.acceptorderid "+
//				"	LEFT JOIN am_member AS m ON od.memberid=m.id                   "+
//				"  WHERE dc.orgid LIKE '"+ac.getVisitor().getUser().getOrgId()+"%' ";
		
		String orgid=ac.getVisitor().getUser().getOrgId();
		
//		String sql="SELECT distinct dc.gap_name,od.*,m.membername,cd.area_dc_member_id,s.*                           "+
//				"	FROM mall_MemberOrder AS od                                    "+
//				"	LEFT JOIN mall_CommodityDistribution AS cd ON od.id=cd.orderid "+
//				"	LEFT JOIN mall_store AS s ON cd.acceptorderid=s.id             "+
//				"   LEFT JOIN mall_distribution_center AS dc ON dc.orgid=cd.acceptorderid "+
//				"	LEFT JOIN am_member AS m ON od.memberid=m.id                   "+
//				"	WHERE dc.orgid LIKE '"+orgid+"%'  "+
//				"   AND cd.acceptorderid <>'org' ";
		String sql="SELECT *,get_membername_by_id(memberid) AS member_name FROM dc_info WHERE cd_accept_order_id LIKE '"+orgid+"%'  ";
		
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp","dc_mall_commoditydistribution.query");
		
		if(queryRow!=null){
			//订单编号
			String ordercode=queryRow.get("ordercodeq");
			if(!Checker.isEmpty(ordercode)){
				sql+=" AND ordercode LIKE '%"+ordercode+"%'";
			}
			
			//分配时间
			String disttimeStrt=queryRow.get("disttimeq");
			String disttimeEnd=queryRow.get("disttimeq.t");
			if(!Checker.isEmpty(disttimeStrt)&&!Checker.isEmpty(disttimeEnd)){
				sql+=" AND disttime>=to_date('"+disttimeStrt+"','yyyy-MM-dd HH24:MI:ss')  ";
				sql+=" AND disttime<to_date('"+disttimeEnd+"','yyyy-MM-dd HH24:MI:ss')  ";
			}
			
			//商品名称
			String commodity_nameq=queryRow.get("commodity_nameq");
			if(!Checker.isEmpty(commodity_nameq)){
				
				sql+=" AND commodityname LIKE '%"+commodity_nameq+"%'  ";
			}
			//收货人
			String recv_nameq=queryRow.get("recv_nameq");
			if(!Checker.isEmpty(recv_nameq)){
				sql+=" AND contact LIKE '%"+recv_nameq+"%'  ";
			}
		}
		
		sql+=" ORDER BY createdate DESC ";
		
		
		return sql;
	}

}
