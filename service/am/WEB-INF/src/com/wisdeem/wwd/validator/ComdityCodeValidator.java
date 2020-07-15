package com.wisdeem.wwd.validator;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

public class ComdityCodeValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		String strRegex = "[a-zA-Z0-9]+";
		boolean is = value.matches(strRegex);
		
		String sql;
		String comdity_id = ac.getRequestParameter("ws_commodity_name.form.comdity_id");
		String orgid=ac.getVisitor().getUser().getOrgId();
		if(comdity_id.equals("")||comdity_id==""){
		    sql="select count(*) from WS_COMMODITY_NAME where orgid='"+orgid+"' and comdity_code='"+value+"' ";
		}else{
		    sql="select count(*) from WS_COMMODITY_NAME where orgid='"+orgid+"' and comdity_code='"+value+"' and comdity_id!="+comdity_id+" ";
		}
	    try {
			DB db = DBFactory.getDB();
		  
			MapList list = db.query(sql);
			int str=Integer.parseInt(list.getRow(0).get(0));
			if(str!=0){
			  return "已存在，请重新输入！";
			}
			if (!is) {
				return "只能是数字和字母！";
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String validate(ActionContext arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}
}
