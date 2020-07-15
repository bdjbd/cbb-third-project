package com.wd.sdatadictionary;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;

public class FldvaluecodeValidator implements Validator {

//数据项名称新增时数据编号不能重复及不能含有非法字符的验证
	
	private static final long serialVersionUID = 1L;

	// 子表数据项名称的唯一性约束
	@Override
	public String validate(ActionContext ac, String value, String exp, int index) {
		String[] codes = ac
				.getRequestParameters("sdatadictionarymx.list.fldvaluename");
		for (int i = 0; i < codes.length; i++) {
			if (value.equals(codes[i]) && i != index) {
				return "数据项名称不能重复";
			}
			if (value.indexOf("<") != -1 ) {
				return "数据项名称不能包含<>/";
			}
			if (value.indexOf(">") != -1 ) {
				return "数据项名称不能包含<>/";
			}
			if (value.indexOf("/") != -1 ) {
				return "数据项名称不能包含<>/";
			}
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
