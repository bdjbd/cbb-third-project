package com.am.mall.order.ordermanager;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class OrderManagerSqlProvider implements SqlProvider{

	
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		Row query = FastUnit.getQueryRow(ac, "am_bdp", "mall_memberorder.query");
		
		String sql = "SELECT to_char(mm.totalprice,'9999999990D99') AS totalprices, "
				+" to_char(mm.createdate,'yyyy-mm-dd HH24:MM:ss') AS createdates,"
				+" to_char(mm.paymentdate,'yyyy-mm-dd HH24:MM:ss') AS paymentdates,"
				+ " mm.*,am.membername, " 
				+ " ms.store_name AS msStoreName,mm.mall_class "
				+ " FROM mall_memberorder AS mm "
				+ " LEFT JOIN am_member AS am ON mm.MemberID = am.id "
				+ " LEFT JOIN mall_commodity AS mc ON mm.commodityid=mc.id "
				+ " LEFT JOIN mall_store AS ms ON ms.id=mc.store "
				+ " WHERE orderstate NOT IN ('1','2','10') "
				+ " AND mm.OrgCode LIKE '"+orgid+"%' ";
				
		if(query != null){
			String membername = query.get("membername_q");
			
			//商品名称
			String commodityname_q = query.get("commodityname_q");
			if(!Checker.isEmpty(commodityname_q)){
				sql += " AND lower(mm.commodityname) LIKE '%"+commodityname_q.toLowerCase()+"%'";
			}
			//联系人
			String contact_q = query.get("contact_q");
			if(!Checker.isEmpty(contact_q)){
				sql += " AND mm.contact LIKE '%"+contact_q+"%'";
			}
			//联系电话
			String contactphone_q = query.get("contactphone_q");
			if(!Checker.isEmpty(contactphone_q)){
				sql += " AND mm.contactphone LIKE '%"+contactphone_q+"%'";
			}
			//订单状态
			String orderstate_q = query.get("orderstate_q");
			if(!Checker.isEmpty(orderstate_q)){
				sql += " AND mm.orderstate LIKE '"+orderstate_q+"'";
			}
			//下单时间，开始
			String create_time_q = query.get("createdate_q");
			//下单时间，结束
			String create_time_qt = query.get("createdate_q.t");
			if(!Checker.isEmpty(create_time_q)&&!Checker.isEmpty(create_time_qt)){
				sql += " AND mm.createdate>='"+create_time_q+"' AND mm.createdate<='"+create_time_qt+"'";
			}
			
			//支付时间开始
			String paymentdate_q = query.get("paymentdate_q");
			//支付时间结束
			String paymentdate_qt = query.get("paymentdate_q.t");
			if(!Checker.isEmpty(paymentdate_q)&&!Checker.isEmpty(paymentdate_qt)){
				sql += " AND mm.paymentdate>='"+paymentdate_q+"' AND mm.paymentdate<='"+paymentdate_qt+"'";
			}
			
			//订单编号
			String ordercode_q=query.get("ordercode_q");
			if(!Checker.isEmpty(ordercode_q)){
				sql += " AND mm.ordercode LIKE '%"+ordercode_q+"%' ";
			}
			
			
			if(!Checker.isEmpty(membername)){
				sql += " AND am.membername LIKE '%"+membername+"%'";
			}
			
			//店铺名称
			String stroeName=query.get("mall_stroe_q");
			if(!Checker.isEmpty(stroeName)){
				sql+=" AND lower(ms.store_name) LIKE lower('%"+stroeName+"%')  ";
			}
			
			//商城分类
			String mallClass=query.get("mall_class_q");
			if(!Checker.isEmpty(mallClass)){
				sql+=" AND mm.mall_class ='"+mallClass+"' ";
			}
					
		}
		
		sql += " ORDER BY createdate DESC";
		
		return sql;
	}

}
