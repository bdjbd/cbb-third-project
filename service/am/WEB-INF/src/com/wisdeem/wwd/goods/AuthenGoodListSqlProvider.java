package com.wisdeem.wwd.goods;

import com.fastunit.FastUnit;
import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.SqlProvider;
import com.fastunit.util.Checker;

public class AuthenGoodListSqlProvider implements SqlProvider {

	/**
	 * 商品维护查询条件
	 */
	private static final long serialVersionUID = 1L;
	private static final String sessiontTag="p2p_shop_authen.list_comdytype";

	@Override
	public String getSql(ActionContext ac) {
		//p2p_shop_authen.query
		Row query = FastUnit.getQueryRow(ac, "wwd", "p2p_shop_authen.query");
		String orgid = ac.getVisitor().getUser().getOrgId();
		
		String comdytype=ac.getRequestParameter("comdytype"); 
		
		if(comdytype==null||"".equals(comdytype)){
			comdytype=ac.getSessionAttribute(sessiontTag,"");
		}else{
			ac.setSessionAttribute(sessiontTag, comdytype);
		}
		
		
		
		String sql = "select a.data_status as datastatus,a.comdity_class_id as comdityclassid,"
				+ "* from WS_COMMODITY_NAME a left join WS_COMMODITY b on a.comdity_class_id=b.comdy_class_id  "
				+ "where lower(a.orgid) = '" + orgid + "'  AND b.comdytype="+comdytype;

		// 商品编号
		String comdity_code = "";
		// 商品名称
		String name = "";
		// 商品分类
		String comdity_class_id = "";
		// 订单状态
		String data_status = "";

		if (query != null) {
			comdity_code = query.get("comdity_code");
			name = query.get("name");
			comdity_class_id = query.get("comdityclassid");
			data_status = query.get("datastatus");
			
			if (!Checker.isEmpty(comdity_code)) {
				sql += " and a.comdity_code like '%" + comdity_code + "%'";
			}
			if (!Checker.isEmpty(name)) {
				sql += " and a.name like '%" + name + "%'";
			}
			if (!Checker.isEmpty(comdity_class_id)) {// 商品分类
				// 根据当前的分类id，查询是否有子分类
				String quSQL = "select * from WS_COMMODITY where comdy_class_id="
						+ Integer.parseInt(comdity_class_id) + " ";
				try {
					DB db = DBFactory.getDB();
					MapList list = db.query(quSQL);
					String c_code = list.getRow(0).get("c_code");// 子id
					if (c_code != null || c_code != "") {
						sql += " and b.c_code like '" + c_code + "%' ";
					}
				} catch (JDBCException e) {
					e.printStackTrace();
				}
			}
			if (!Checker.isEmpty(data_status)) {
				sql += " and a.data_status = " + Integer.parseInt(data_status)+ " ";
			}
			
		}
		sql += "  order by a.comdity_id desc";
		return sql;
	}
}
