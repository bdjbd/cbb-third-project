package com.wisdeem.wwd.validator;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;
import com.fastunit.util.Checker;

public class TokenValidator implements Validator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
		try {
			DB db = DBFactory.getDB();
			String orgid = ac.getVisitor().getUser().getOrgId();
			String public_id = ac.getRequestParameter("ws_public_accounts.form.public_id");
			String token = ac.getRequestParameter("ws_public_accounts.form.token");
			String sql = "";
			if (public_id != "") {// 修改
				sql = "select * from ws_public_accounts where token='" + token
						+ "' and public_id!=" + public_id + " and orgid='"+orgid+"' ";
			} else {// 新增
				sql = "select * from ws_public_accounts where orgid='"+orgid+"' and token='" + token
						+ "'";
			}
			MapList mapList = db.query(sql);
			if (mapList.size() > 0) {
				return "token已存在";
			}
		} catch (JDBCException e) {
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
	public void testDemo(){
		try {
			DB db=DBFactory.getDB();
			String sql="SELECT c.cid,c.cinfo,d.nid,d.title,substring(content,0,20) AS abstract "+
					"	FROM newscategory AS c RIGHT JOIN newsdetail AS d                     "+
					"	ON c.cid=d.cid                                                        "+
					"	WHERE c.orgid='org' AND c.cid=4 limit 10 offset 0*10                  ";
			MapList map=db.query(sql);
			if(!Checker.isEmpty(map)){
				for(int i=0;i<map.size();i++){
					Row row=map.getRow(i);
					System.out.println(row.get("cid"));
				}
			}
		} catch (JDBCException e) {
			e.printStackTrace();
		}
	}

}
