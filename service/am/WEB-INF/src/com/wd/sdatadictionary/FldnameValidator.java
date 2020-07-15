package com.wd.sdatadictionary;

import com.fastunit.context.ActionContext;
import com.fastunit.support.Validator;
//列表上主表名称的唯一性约束
public class FldnameValidator implements Validator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String validate(ActionContext ac, String value, String exp,
			int rowindex) {	
		String[] names= ac.getRequestParameters("sdatadictionaryhz.list.fldname");		
		 for(int i=0; i<names.length;i++){		 
				 if(value.equals(names[i])&& i!=rowindex){					 
					return  "数据字典名称不能重复";				
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
