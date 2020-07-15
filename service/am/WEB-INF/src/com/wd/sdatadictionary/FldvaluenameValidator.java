package com.wd.sdatadictionary;

import com.fastunit.context.ActionContext;

import com.fastunit.support.Validator;

//数据字典新增时数据编号不能重复及不能含有非法字符的验证

@SuppressWarnings("serial")
public class FldvaluenameValidator implements Validator {

	@Override
	public String validate(ActionContext ac, String value, String exp,
			int rowindex) {
		String[] codes = ac
				.getRequestParameters("sdatadictionarymx.list.fldvaluecode");
		for (int i = 0; i < codes.length; i++) {
			if (value.trim().equals(codes[i].trim()) && i != rowindex) {
				return "数据编号不能重复！";
			}
			if (value.indexOf("<") != -1) {
				return "数据编号不能包含<>/";
			}
			if (value.indexOf(">") != -1) {
				return "数据项名称不能包含<>/";
			}
			if (value.indexOf("/") != -1) {
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
