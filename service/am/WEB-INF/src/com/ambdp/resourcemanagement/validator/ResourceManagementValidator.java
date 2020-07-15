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
 * @date 创建时间：2016年5月6日 上午10:18:41
 * @version 资源管理CODE验证器
 */
public class ResourceManagementValidator implements Validator{

	@Override
	public String validate(ActionContext ac, String value, String expression,
			int rowIndex) {
			String result=null;
			DB db = null;
			//获取页面状态
			String state = ac.getRequestParameter("mjyc_resourcemanagement.form.mm");
			
			if(state.equals("a"))
			{
				//获取值
				String queryCodeSQL = "SELECT count(*) FROM mjyc_resourcemanagement WHERE code = ? ";
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
