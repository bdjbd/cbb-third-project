package com.wisdeem.wwd.sendmes;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;

public class TitleValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		
		//不包含 "},{",'},{',"
		String type = ac.getRequestParameter("ws_fsendmess.form.msg_type");
		String reg1="\"}";
		String reg2="{\"";
		String reg3="'}";
		String reg4="{'";
		String reg5="\"";
		if("6".equals(type)){
			String title = ac.getRequestParameter("ws_fsendmess.form.title").trim();
			if("".equals(title)){
				return "不能为空";
			}
			if(title.contains(reg1)){
				return "不能包含\"}";
			}
			if(title.contains(reg2)){
				return "不能包含{\"";
			}
			if(title.contains(reg3)){
				return "不能包含'}";
			}
			if(title.contains(reg4)){
				return "不能包含{'";
			}
			if(title.contains(reg5)){
				return "不能包含\"";
			}
		}

		return null;
		
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}
}

