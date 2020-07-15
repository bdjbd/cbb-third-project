package com.wisdeem.wwd.validator;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
/**
 * 小数位数验证：总位数为9位，小数占2位
 * 
 * @author LYS 2013-11-18
 */
public class DecimalValidator implements Validator {

	private static final long serialVersionUID = 1063194547207297486L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		// 总位数为8位，小数占2位的正则表达式
		// ^[0-9]+\\.{0,1}[0-9]{0,2}$
		String strRegex = "^\\d{1,7}(\\.\\d{1,2})?$";
		boolean is = value.matches(strRegex);

		if (!is) {
			return "整数部分最多为7位，小数部分最多为2位";
		}
		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}

}
