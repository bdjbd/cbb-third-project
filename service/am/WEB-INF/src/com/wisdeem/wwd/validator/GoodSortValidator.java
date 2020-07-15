package com.wisdeem.wwd.validator;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

//分类编号验证
public class GoodSortValidator implements Validator {

	private static final long serialVersionUID = 1063194547207297486L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		// 验证只能是字母和数字
		String regx = "[a-zA-Z0-9]+";
		value = value.trim();
		boolean is = value.matches(regx);

		String codeSQL = "";
		String orgid = ac.getVisitor().getUser().getOrgId();
		String suf = ac.getRequestParameter("ws_commodity_edit.suf");
		String cCode = ac.getRequestParameter("ws_commodity_edit.c_code_s");
		String c_code = suf + "-" + cCode;
		String comdy_class_id = ac.getRequestParameter("ws_commodity_edit.comdy_class_id");
		if (comdy_class_id.equals("") || comdy_class_id == "") {// 新增
			codeSQL = "select count(*) from ws_commodity where orgid='" + orgid
					+ "' and c_code='" + c_code + "'";

		} else {// 修改
			codeSQL = "select count(*) from ws_commodity where orgid='" + orgid
					+ "' and c_code='" + c_code + "' and comdy_class_id!="
					+ comdy_class_id + " ";
		}

		try {

			DB db = DBFactory.getDB();
			MapList list = db.query(codeSQL);
			int str = Integer.parseInt(list.getRow(0).get(0));
			if (str != 0) {
				return "已存在，请重新输入！";//分类编号
			}

			if (!is) {
				return "只能是字母和数字";
			}

		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}

}
