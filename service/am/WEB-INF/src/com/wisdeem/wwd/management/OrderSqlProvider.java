package com.wisdeem.wwd.management;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class OrderSqlProvider implements SqlProvider{

	/**
	 * 商城订单记录查询条件
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getSql(ActionContext ac) {
		Row query = FastUnit.getQueryRow(ac, "wwd", "ws_order.query");
		String orgid=ac.getVisitor().getUser().getOrgId();
		
		String sql = "select a.data_status as datastatus,a.order_code as order_code,a.member_code as membercode," +
				"b.comdity_class_id as comdityclassid,c.nickname as nickname,a.recv_province as recv_province,a.recv_city as recv_city," +
				"a.recv_area as recv_area,d.class_name as class_name,* from "
				+ " WS_ORDER a left join WS_COMMODITY_NAME b "
				+ " on a.comdity_id=b.comdity_id "
				+ " left join WS_MEMBER c " 
				+" on a.member_code=c.member_code "
				+ " left join WS_COMMODITY d "
				+ " on b.comdity_class_id=d.comdy_class_id "
				+ " where lower(a.orgid) = '"+orgid+"' "
				+ " AND a.ordertype=1 OR a.ordertype IS NULL ";

		//订单编号
		String order_code="";
		//商品名称
		String name ="";
		//商品分类
		String comdity_class_id="";
		//昵称
		String nickname="";
		//订单状态
		String data_status="";
		//收货地址
		String recv_province="";
		String recv_city="";
		String recv_area="";

		if(query!=null){
			  order_code = query.get("order_code");
			  name = query.get("name");
			  comdity_class_id=query.get("comdityclassid");
			  nickname = query.get("nickname");
			  data_status=query.get("datastatus");
			  recv_province = query.get("recv_province");
			  recv_city = query.get("recv_city");
			  recv_area = query.get("recv_area");
		}
		if(!Checker.isEmpty(order_code)){
			sql+=" and a.order_code like '%"+order_code+"%'";
		}if(!Checker.isEmpty(name)){
			sql+=" and b.name like '%"+name+"%'";
		}if(!Checker.isEmpty(comdity_class_id)){//商品分类
			//根据当前的分类id，查询是否有子分类
			String quSQL="select * from WS_COMMODITY where comdy_class_id="+Integer.parseInt(comdity_class_id)+" ";
			DB db;
			try {
				db = DBFactory.getDB();
				MapList list = db.query(quSQL);
				String c_code = list.getRow(0).get("c_code");//子id
				if(c_code!=null||c_code!=""){
					sql+=" and d.c_code like '"+c_code+"%' ";
				}
				
			} catch (JDBCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}if(!Checker.isEmpty(nickname)){
			sql+=" and c.nickname like '%"+nickname+"%'";
		}if(!Checker.isEmpty(data_status)){
			sql+=" and a.data_status = "+Integer.parseInt(data_status)+" ";
		}if(!Checker.isEmpty(recv_province)){
			sql+=" and a.recv_province = "+Integer.parseInt(recv_province)+" ";
		}if(!Checker.isEmpty(recv_city)){
			sql+=" and a.recv_city = "+Integer.parseInt(recv_city)+" ";
		}if(!Checker.isEmpty(recv_area)){
			sql+=" and a.recv_area = "+Integer.parseInt(recv_area)+" ";
		}
		sql+=" AND ORDERTYPE!=1   order by a.create_time desc";
		//System.out.println(sql);
		return sql;
	}

}
