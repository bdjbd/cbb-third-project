package com.am.more.kopp_train;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.MapList;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 *@author wangxi
 *@create 2016年5月17日
 *@version
 *说明：内容修改使用次数
 */
public class UpdateRecordsAccessIWebApi implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		DB db=null;
		try {
			db = DBFactory.newDB();
		//内容id
		String Id = request.getParameter("id");
		//会员id
		String memberid = request.getParameter("memberid");
		
		String selSql="SELECT * FROM mall_purchase_records WHERE pc_id='"+Id+"' AND member_id='"+memberid+"' ";
		
			MapList selMap = db.query(selSql);
			if(!Checker.isEmpty(selMap)){
				//使用次数
				String access = selMap.getRow(0).get("access");
				if(!"-1".equals(access)){
					String updateBrowseSql="UPDATE mall_purchase_records SET access=(access-1) WHERE pc_id='"+Id+"' AND member_id='"+memberid+"' ";
					db.execute(updateBrowseSql);
				}
			}
		} catch (JDBCException e1) {
			e1.printStackTrace();
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

}
