package com.wd.sdatadictionary;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;
//form表单上主表名称的唯一性约束
public class FldnameFormValidator implements Validator{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String validate(ActionContext ac, String value, String values,
			int index) {
		String message="数据名称不能重复";
		String fldname=ac.getRequestParameter("sdatadictionaryhz.form.fldname");
		//String id1=ac.getRequestParameter("sdatadictionaryhz.form.dictionaryid");
		String id=ac.getSessionAttribute("wd_blj.sdatadictionaryhz.form.dictionaryid","1");
		String sql="select * from SDATADICTIONARYHZ where fldname ='"+ fldname +"'and dictionaryid <> '"+id+"'";
		DB db;
		try {
			db = DBFactory.getDB();
			MapList names =db.query(sql);
			if(names.size()>0){
				return message;	
			}
		} catch (JDBCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
