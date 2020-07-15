package com.wisdeem.wwd.validator;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

public class CommodityNameValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		String sql = "";
		String orgid = ac.getVisitor().getUser().getOrgId();
		// 父节点的Id
		String parent_id = ac
				.getRequestParameter("ws_commodity_edit.parent_id");
		String class_name = ac
				.getRequestParameter("ws_commodity_edit.class_name");
		String comdy_class_id = ac
				.getRequestParameter("ws_commodity_edit.comdy_class_id");
		if (comdy_class_id == "") {// 新增
			sql = "select class_name from ws_commodity where parent_id='"
					+ parent_id + "' and class_name='" + class_name
					+ "' and orgid='" + orgid + "'";
		} else {
			sql = "select class_name from ws_commodity where parent_id='"
				+ parent_id + "' and class_name='" + class_name
				+ "' and orgid='" + orgid + "' and comdy_class_id!="+comdy_class_id+"";
		}
		try {
			DB db = DBFactory.getDB();
			MapList list = db.query(sql);
			if (list.size() > 0) {
				return "已存在，请重新输入！";
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
