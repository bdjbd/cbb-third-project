package com.cdms.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;

public class IpValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext arg0, String value, String arg2, int arg3) {
		String regex = "^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$";
		Pattern p = Pattern.compile(regex);
		Matcher m=p.matcher(value);
		if(!m.matches()&&value==null){
			return "IP格式不正确";
		}
		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
