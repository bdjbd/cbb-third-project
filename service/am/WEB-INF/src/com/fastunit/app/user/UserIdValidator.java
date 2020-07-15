package com.fastunit.app.user;

import java.util.HashMap;
import java.util.Map;

import com.fastunit.LangUtil;
import com.fastunit.app.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

/**
 * 用于验证用户编号、角色编号、机构编号、用户组编号、管理员编号。
 */
public class UserIdValidator implements Validator {

	private static final Map<String, String> keywords = new HashMap<String, String>();

	static {
		// 日志中未登录用户使用guest做用户编号和用户名称，为避免混淆，禁止注册用户使用guest做用户名。
		keywords.put("guest", null);
	}

	private static final Map<String, String> valid_characters = new HashMap<String, String>();

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		// userid为必填字段，不必检查空
		if (Checker.isEmpty(value)) {
			return null;
		}
		// 1.不能以"."开头
		// 数字!Checker.isInteger(firstChar)
		String firstChar = value.substring(0, 1);
		boolean pass = !firstChar.equals(".");
		// 2.不能包含非法字符
		if (pass) {
			for (int i = 0; i < value.length(); i++) {
				String character = value.substring(i, i + 1);
				if (!valid_characters.containsKey(character)) {
					pass = false;
					break;
				}
			}
		}
		// 3.不能包含系统关键词
		if (pass && keywords.containsKey(value)) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.USERID_KEYWORD, value);
		}
		return pass ? null : LangUtil.get(ac, AdmLang.DOMAIN,
				AdmLang.USERID_INVALID);
	}

	@Override
	public String validate(ActionContext ac, String from, String to,
			String expression) {
		return null;
	}

	static {
		valid_characters.put("_", null);
		valid_characters.put(".", null);
		valid_characters.put("0", null);
		valid_characters.put("1", null);
		valid_characters.put("2", null);
		valid_characters.put("3", null);
		valid_characters.put("4", null);
		valid_characters.put("5", null);
		valid_characters.put("6", null);
		valid_characters.put("7", null);
		valid_characters.put("8", null);
		valid_characters.put("9", null);
		valid_characters.put("a", null);
		valid_characters.put("b", null);
		valid_characters.put("c", null);
		valid_characters.put("d", null);
		valid_characters.put("e", null);
		valid_characters.put("f", null);
		valid_characters.put("g", null);
		valid_characters.put("h", null);
		valid_characters.put("i", null);
		valid_characters.put("j", null);
		valid_characters.put("k", null);
		valid_characters.put("l", null);
		valid_characters.put("m", null);
		valid_characters.put("n", null);
		valid_characters.put("o", null);
		valid_characters.put("p", null);
		valid_characters.put("q", null);
		valid_characters.put("r", null);
		valid_characters.put("s", null);
		valid_characters.put("t", null);
		valid_characters.put("u", null);
		valid_characters.put("v", null);
		valid_characters.put("w", null);
		valid_characters.put("x", null);
		valid_characters.put("y", null);
		valid_characters.put("z", null);
		valid_characters.put("A", null);
		valid_characters.put("B", null);
		valid_characters.put("C", null);
		valid_characters.put("D", null);
		valid_characters.put("E", null);
		valid_characters.put("F", null);
		valid_characters.put("G", null);
		valid_characters.put("H", null);
		valid_characters.put("I", null);
		valid_characters.put("J", null);
		valid_characters.put("K", null);
		valid_characters.put("L", null);
		valid_characters.put("M", null);
		valid_characters.put("N", null);
		valid_characters.put("O", null);
		valid_characters.put("P", null);
		valid_characters.put("Q", null);
		valid_characters.put("R", null);
		valid_characters.put("S", null);
		valid_characters.put("T", null);
		valid_characters.put("U", null);
		valid_characters.put("V", null);
		valid_characters.put("W", null);
		valid_characters.put("X", null);
		valid_characters.put("Y", null);
		valid_characters.put("Z", null);
	}
}
