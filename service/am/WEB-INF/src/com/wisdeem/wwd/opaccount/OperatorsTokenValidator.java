package com.wisdeem.wwd.opaccount;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

public class OperatorsTokenValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		try {
			DB db = DBFactory.getDB();
			String public_id = ac.getRequestParameter("ws_operators_account.form.public_id");
			String token = ac.getRequestParameter("ws_operators_account.form.token");
			String sql = "";
			if (public_id != "") {// 修改
				sql = "select * from WS_OPERATORS_ACCOUNT where token='" + token
						+ "' and public_id!=" + public_id + "";
			} else {// 新增
				sql = "select * from WS_OPERATORS_ACCOUNT where token='" + token
						+ "'";
			}
			MapList mapList = db.query(sql);
			if (mapList.size() > 0) {
				return "token已存在";
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
