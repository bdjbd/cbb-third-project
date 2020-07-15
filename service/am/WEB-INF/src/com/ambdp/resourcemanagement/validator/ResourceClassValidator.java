package com.ambdp.resourcemanagement.validator;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Type;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.support.Validator;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年5月6日 上午10:03:12
 * @version 资源分类CODE验证器
 */
public class ResourceClassValidator implements Validator{

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
			String result=null;
			DB db = null;
			
			String state = ac.getRequestParameter("am_datatype.form.mm");
			
			if(state.equals("a"))
			{
				//获取值
				String queryCodeSQL = "SELECT count(*) FROM mjyc_resourceclass WHERE code = ? ";
				try {
					db = DBFactory.newDB();
					MapList list = db.query(queryCodeSQL,value.trim(),Type.VARCHAR);
					if(Integer.parseInt(list.getRow(0).get(0))>0)
					{
						result="CODE编码已存在";
					}else{
						result=null;
					}
					
				} catch (JDBCException e) {
					e.printStackTrace();
				}finally
				{
					if(db!=null)
					{
						try {
							db.close();
						} catch (JDBCException e) {
							e.printStackTrace();
						}
					}
				}
			}
			
		return result;
	}

	@Override
	public String validate(ActionContext ac, String from, String to,
			String expression) {
		 
		return null;
	}
}
