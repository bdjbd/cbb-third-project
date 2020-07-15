package com.am.frame.webapi.addres;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;


/**
 *@author wangxi
 *@create 2016年3月31日
 *@version
 *说明：我的地址----设置默认地址
 */
public class UpdateDefaultAddr implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		   try {
			db = DBFactory.newDB();
		String id=request.getParameter("addrid");
		String defaultaddr=request.getParameter("defaultaddr");
		String memberid=request.getParameter("memberid");
		System.out.print("我的地址"+id+defaultaddr+memberid);
		
				//判断是不是默认地址
				if("true".equals(defaultaddr)){
					isDefaultAddr(id,memberid,db);
				}else{
					notDefaultAddr(id,memberid,db);
				}
			} catch (JDBCException e) {
				e.printStackTrace();
			}finally{
				if(db!=null){
					try {
						db.close();
					} catch (JDBCException e) {
						e.printStackTrace();
					}
				}
			}
		
		
		return null;
	}
	/**
	 * 不是默认地址
	 * @param id 地址ID
	 * @param memberid  会员ID
	 * @param db
	 * @throws JDBCException 
	 */
	private void notDefaultAddr(String id, String memberid, DB db) throws JDBCException {
		String updateNotDefaultAddrSQl="UPDATE am_addres SET defaultaddr='0'  WHERE  id='"+id+"' ";
		db.execute(updateNotDefaultAddrSQl);
		
	}
	/**
	 * 是默认地址
	 * @param id 地址ID
	 * @param memberid  会员ID
	 * @param db
	 * @throws JDBCException 
	 */
	private void isDefaultAddr(String id, String memberid, DB db) throws JDBCException {
		//设置本会员下面的地址全部不是默认地址
		String updateDefaultAddrZeroSQl="UPDATE am_addres SET defaultaddr='0' WHERE  am_memberid='"+memberid+"' ";
		db.execute(updateDefaultAddrZeroSQl);
		//设置此地址ID为默认地址
		String updateISDefaultAddrSQl="UPDATE am_addres SET defaultaddr='1' WHERE  id='"+id+"' ";
		db.execute(updateISDefaultAddrSQl);
	}

}
