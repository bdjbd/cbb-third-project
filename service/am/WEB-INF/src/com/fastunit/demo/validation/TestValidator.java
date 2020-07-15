package com.fastunit.demo.validation;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

/**
 * 验证器范例，依据“整数”的值做关联验证：“整数”有值时要大于其值。
 * 
 * @author jim
 */
public class TestValidator implements Validator {

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		// 不检查空
		if (Checker.isEmpty(value)) {
			return null;
		}
		// 获得同一行（依据rowIndex） “整数”的值
		String intValue = ac.getRequestParameter(rowIndex, "validator.list.int");

		// “整数”有值时要大于其值
		if (!Checker.isEmpty(intValue)
				&& Double.parseDouble(value) <= Double.parseDouble(intValue)) {
			return "应大于'" + intValue + "'";
		}
		return null;
	}

	@Override
	public String validate(ActionContext ac, String from, String to,
			String expression) {
		return null;
	}

}
