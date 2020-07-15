package com.wisdeem.wwd.news.newsdetail;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

public class AddNewsDeatilVaildator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		String nid=ac.getRequestParameter("newsdetail.form.nid");
		String checkSQL="SELECT * FROM newsDetail WHERE newscode='"+value+"'";
		if(nid!=null&&!"".equals(nid.trim())){
			checkSQL="SELECT * FROM newsDetail WHERE newscode='"+value+"' AND nid!="+nid;
		}
		try {
			if(!Checker.isEmpty(DBFactory.getDB().query(checkSQL))){
				return "信息编号"+value+"已存在！";
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
