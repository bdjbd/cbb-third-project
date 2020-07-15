package com.cdms.caseAdministration;

import com.am.frame.webapi.db.DBManager;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

public class ValidateCaseNum implements Validator {

	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression, int rowIndex) {

		String id = ac.getRequestParameter("cdms_case_edit.form.id");

		System.out.println("=============================");
		System.out.println(value);
		System.out.println(id);
		System.out.println("=============================");

		DBManager db = new DBManager();
		// 如果取到的id不是空，則是編輯，否則是新增
		if (!Checker.isEmpty(id)) {
			// 編輯時，案件编号不为空时，案件编号可以重复
			if (!Checker.isEmpty(value)) {
				String sql = "select id from cdms_case where case_number !='" + value + "' and id = '" + id + "'";
				MapList mapList = db.query(sql);
				if (mapList.size() > 0) {
					return "不能重复";
				}
			}
		} else {
			// 新增時，案件编号不为空时，案件编号不能重复
			if (!Checker.isEmpty(value)) {
				String sql = "select id from cdms_case where case_number='" + value + "'";
				MapList mapList = db.query(sql);
				if (mapList.size() > 0) {
					return "不能重复";
				}
			}
		}

		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2, String arg3) {
		return null;
	}

}
