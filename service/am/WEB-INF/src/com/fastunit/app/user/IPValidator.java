package com.fastunit.app.user;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.LangUtil;
import com.fastunit.app.AdmLang;
import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;
import com.fastunit.util.StringUtil;

public class IPValidator implements Validator {
	private static final List<String> valid_characters = new ArrayList<String>();

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		if (Checker.isEmpty(value)) {
			return null;
		}
		String[] ips = StringUtil.split(value, ";");
		for (int i = 0; i < ips.length; i++) {
			String error = checkIP(ac, ips[i]);
			if (!Checker.isEmpty(error)) {
				return error;
			}
		}
		return null;
	}

	private String checkIP(ActionContext ac, String ip) {
		// 1、IP格式错误，正确的格式是xxx.xxx.xxx.xxx
		String[] octet = StringUtil.split(ip, "\\.");
		if (octet.length != 4) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_FORMAT);
		}
		// 2.IP段判断
		for (int i = 0; i < octet.length; i++) {
			String error = checkOctet(ac, octet[i]);
			if (!Checker.isEmpty(error)) {
				return error;
			}
		}
		return null;
	}

	private String checkOctet(ActionContext ac, String octet) {
		// 1、只能包含数字、字符“.”、“*”、“-”
		if (!valid(octet)) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_CHAR);
		}
		// 2、同一区段内，不能同时包含“*”和“-”
		if (octet.indexOf("*") >= 0 && octet.indexOf("-") >= 0) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_MARK);
		}
		// 3、“-”不能出现在首、尾
		if (octet.startsWith("-") || octet.endsWith("-")) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_SIDE);
		}
		// 4、同一区段内只能包含一个“-”
		String[] numbers = StringUtil.split(octet, "-");
		if (numbers.length > 2) {
			return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_RANGE);
		}
		// 5、数字范围应在0～255之间
		for (int i = 0; i < numbers.length; i++) {
			String number = StringUtil.replace(numbers[i], "*", "1");
			if (Integer.parseInt(number) > 255) {
				return LangUtil.get(ac, AdmLang.DOMAIN, AdmLang.VALIDATION_IP_NUMBER);
			}
		}
		return null;
	}

	public static boolean valid(String id) {
		for (int i = 0; i < id.length(); i++) {
			String character = id.substring(i, i + 1);
			if (!valid_characters.contains(character)) {
				return false;
			}
		}
		return true;
	}

	static {
		valid_characters.add("-");
		valid_characters.add("*");
		valid_characters.add("0");
		valid_characters.add("1");
		valid_characters.add("2");
		valid_characters.add("3");
		valid_characters.add("4");
		valid_characters.add("5");
		valid_characters.add("6");
		valid_characters.add("7");
		valid_characters.add("8");
		valid_characters.add("9");
	}

	@Override
	public String validate(ActionContext ac, String from, String to,
			String expression) {
		return null;
	}

}
