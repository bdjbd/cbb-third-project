package com.wisdeem.wwd.sendmes;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;

public class DescriptionValidator  implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		
		//不包含 "},{",'},{'
		String type = ac.getRequestParameter("ws_fsendmess.form.msg_type");
		String reg1="\"}";
		String reg2="{\"";
		String reg3="'}";
		String reg4="{'";
		if("6".equals(type)){
			String description = ac.getRequestParameter("ws_fsendmess.form.description").trim();
			if("".equals(description)){
				return "不能为空";
			}
			if(description.contains(reg1)){
				return "不能包含\"}";
			}
			if(description.contains(reg2)){
				return "不能包含{\"";
			}
			if(description.contains(reg3)){
				return "不能包含'}";
			}
			if(description.contains(reg4)){
				return "不能包含{'";
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


