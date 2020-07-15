package com.p2p.business;

import com.fastunit.FastUnit;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class DispatcherSqlProvider implements SqlProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		String orgid=ac.getVisitor().getUser().getOrgId();
		//ws_orderw.query
		Row query = FastUnit.getQueryRow(ac, "wwd", "ws_orderw.query");
		
		String sql=" SELECT od.data_status AS odstatus,*,dr.MEMBER_CODE AS drMember "
				+ " ,od.CREATE_TIME AS ctr   "
				+ " FROM p2p_DispatchRecod AS dr "
				+ " LEFT JOIN ws_order AS od ON dr.order_code=od.order_code "
				+ " LEFT JOIN ws_member AS mem ON od.member_code=mem.member_code "
				+ " LEFT JOIN ws_commodity_name AS wcn ON wcn.comdity_id=od.comdity_id "
				+ " WHERE ordertype=1 AND wcn.type=3   AND wcn.orgid='"+orgid+"' ";
		
		if(query!=null){
			/**
			 * 收货人 订单状态 接单人 下单时间区间  默认当天
			 */
			//订单编号  order_code
			String orderCode=query.get("order_code");
			//接单状态  orstatus
			String orstatus=query.get("orstatus");
			//收货人姓名   recvname
			String recvName=query.get("recvname");
			//收货人电话  recvphone
			String recvPhone=query.get("recvphone");
			//订单状态  order_status
			String orderStatus=query.get("order_status");
			//接单人   drmembercode
			String drmemberCode=query.get("drmembercode");
			//下单时间   order_create_start_time  order_create_end_time 
			String orderStartTime=query.get("order_create_start_time");
			String orderEndTime=query.get("order_create_end_time");
			
			if(!Checker.isEmpty(orderCode)){
				sql+=" AND od.order_code like '%"+orderCode+"%'  ";
			}
			if(!Checker.isEmpty(orstatus)){
				sql+=" AND dr.ORStatus="+orstatus;
			}
			if(!Checker.isEmpty(recvName)){
				sql+=" AND od.recv_name like '%"+recvName+"%' ";
			}
			if(!Checker.isEmpty(recvPhone)){
				sql+=" AND od.recv_phone like '%"+recvPhone+"%' ";
			}
			if(!Checker.isEmpty(orderStatus)){
				sql+=" AND od.data_status="+orderStatus;
			}
			if(!Checker.isEmpty(drmemberCode)){
//				sql+=" AND dr.member_code like '"+drmemberCode+"'";
				sql+="  AND dr.member_code in "
						+ "(SELECT member_code FROM ws_member WHERE mem_name like '%"+drmemberCode+"%')";
				
			}
			if(!Checker.isEmpty(orderStartTime)){
				sql+="  AND od.create_time >to_timestamp('"+orderStartTime+" 00:00:00','yyyy-mm-dd HH24:MI:SS') ";
				if(!Checker.isEmpty(orderEndTime)){
					sql+=" AND od.create_time < to_timestamp('"+orderEndTime+" 23:59:59','yyyy-mm-dd HH24:MI:SS')  ";
				}else{
					sql+=" AND od.create_time < to_timestamp('"+orderStartTime+" 23:59:59','yyyy-mm-dd HH24:MI:SS')  ";
				}
			}
		}
		
		sql+=" ORDER by od.create_time DESC";
		return sql;
	}

}
