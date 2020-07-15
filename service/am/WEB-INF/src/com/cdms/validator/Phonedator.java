package com.cdms.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

public class Phonedator implements Validator{
	private Logger logger=LoggerFactory.getLogger(com.am.frame.order.SubmitOrderWebApi.class);

	/**
	 * 手机号码验证
	 */
	private static final long serialVersionUID = 1L;
	public String validate(ActionContext arg0, String value, String arg2, int arg3) {
		String regex = "[1]\\d{10}";
		Pattern p = Pattern.compile(regex);
		logger.info("---------"+value);
		logger.info("+++++++++"+p);
		if(!Checker.isEmpty(value)){
			Matcher m=p.matcher(value);
			if(m.matches()==false){
				return "手机号码格式不正确";
			}
		}
		
		return null;
	}

	public String validate(ActionContext arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
