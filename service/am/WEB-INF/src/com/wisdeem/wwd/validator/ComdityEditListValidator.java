package com.wisdeem.wwd.validator;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

public class ComdityEditListValidator  implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		String orgid = ac.getVisitor().getUser().getOrgId();
		String comdity_class_id = ac.getRequestParameter("ws_commodity_name.form1.comdity_class_id");
		try {
			if ("1".equals(comdity_class_id)) {
				return "请选择有效的商品分类";
			}
			String sql = "select * from ws_commodity where parent_id = "
					+ comdity_class_id + "";
			DB db = DBFactory.getDB();
			MapList list = db.query(sql);
			if (list.size() > 0) {
				return "请选择有效的商品分类";
			}

		} catch (JDBCException e) {
			e.printStackTrace();
		}

			
		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		return null;
	}
}
