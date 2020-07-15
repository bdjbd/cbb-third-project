package com.am.cro.OrderSettlement;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

/**
 * @author 张少飞
 * @create 2017/7/25
 * @version 
 * 说明:汽车公社 订单结算列表  只显示当前机构及下属机构、支付状态=未支付  PayState='0'  、订单状态=已分配  OrderState in('1'、'2'、'3') 的订单列表
 * 订单查询
 */
public class OrderSettlementListSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		
		Row queryRow=FastUnit.getQueryRow(ac,"am_bdp","cro_carrepairorder2.query");
		String orgid = ac.getVisitor().getUser().getOrgId();//当前机构
		StringBuilder querySQL=new StringBuilder();
				
		querySQL.append(" select to_char(orderdate,'yyyy-MM-DD hh24:mi:ss') as orderdate, "
				+ " to_char(createdate,'yyyy-MM-DD hh24:mi:ss') as createdate1, "
				+ " * from cro_CarRepairOrder cro,am_member am");
		querySQL.append(" where cro.memberid=am.id and cro.orderstate in('1','2','3')");	
		
		querySQL.append("and cro.ORGCODE like '"+orgid+"%'");
		
		if(queryRow!=null){
			//机构查询
			if (!Checker.isEmpty(queryRow.get("orgcode1"))) 
			{
				querySQL.append("and cro.orgcode like '"+queryRow.get("orgcode1")+"%'");
			}
			
			//会员姓名
			String memberid_q = queryRow.get("memberid_q");
			if(!Checker.isEmpty(memberid_q)){
				querySQL.append("and cro.memberid in (select id from am_member where membername like '%"+queryRow.get("memberid_q")+"%')");
			}
			
			//预约日期 start
			String orderdate_Start=queryRow.get("orderdate_q");
			//预约日期 end
			String orderdate_End=queryRow.get("orderdate_q.t");
			if(!Checker.isEmpty(orderdate_Start)&&!Checker.isEmpty(orderdate_End)){
				querySQL.append(" AND (cro.orderdate>=to_date('"+orderdate_Start+"','yyyy-mm-dd') "
						+ " AND cro.orderdate<= to_date('"+orderdate_End+"','yyyy-mm-dd'))");
			}
			
			//预约时间(模糊查询)
			String ordertime_q = queryRow.get("ordertime_q");
			if(!Checker.isEmpty(ordertime_q)){
				querySQL.append(" and cro.ordertime like '%"+ordertime_q+"%' ");
			}
			
			//建立时间 start
			String createdate_Start=queryRow.get("createdate_q");
			//建立时间 end
			String createdate_End=queryRow.get("createdate_q.t");
			if(!Checker.isEmpty(createdate_Start)&&!Checker.isEmpty(createdate_End)){
				querySQL.append(" AND (cro.createdate>=to_date('"+createdate_Start+"','yyyy-mm-dd') "
						+ " AND cro.createdate<= to_date('"+createdate_End+"','yyyy-mm-dd'))");
			}
			
			//订单状态(精确查询)
			String orderstate_q = queryRow.get("orderstate_q");
			if(!Checker.isEmpty(orderstate_q)){
				querySQL.append(" and cro.orderstate= '"+orderstate_q+"' ");
			}
			
			//预约类型(精确查询)
			String repair_class_q = queryRow.get("repair_class_q");
			if(!Checker.isEmpty(repair_class_q)){
				querySQL.append(" and cro.repair_class= '"+repair_class_q+"' ");
			}
		}
		
		querySQL.append(" order by createdate desc ");
		System.err.println("订单待结算列表>>>"+querySQL);
		return querySQL.toString();
	}

}
