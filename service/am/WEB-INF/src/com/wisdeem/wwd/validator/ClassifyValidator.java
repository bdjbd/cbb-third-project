package com.wisdeem.wwd.validator;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
/**
 * 编号验证：只能是字母和数字
 * 
 * @author LYS 2013-11-18
 */
public class ClassifyValidator implements Validator {

	private static final long serialVersionUID = 1063194547207297486L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		//验证只能是字母和数字
		String regx = "[a-zA-Z0-9]+";
		boolean is = value.matches(regx);

		if (!is) {
			return "只能是字母和数字";
		}
		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}

}
