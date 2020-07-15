package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/** * @author  作者：yangdong
 * @date 创建时间：2016年3月28日 下午3:56:25
 * @version 添加充值支付清单
 * @param memberid:会员id,paydatatime:充值时间,paycode:支付编码,paymoney支付金额
 */
public class AddRecordPayment implements IWebApiService{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		//生成UUID
		String id = request.getParameter("id");
		//获取参数
		String memberid = request.getParameter("memberid");
		String paycode = "c@" + memberid + "@" +id;
		double paymoney = Double.parseDouble(request.getParameter("paymoney"));
		int paysource = Integer.parseInt(request.getParameter("paysource"));
		String paycontent = request.getParameter("paycontent");
		String amorders = request.getParameter("amorders");
		String accountId = request.getParameter("accountId");
		
		StringBuilder  addRecordPaymentSQL = new StringBuilder();
		addRecordPaymentSQL.append(" INSERT INTO mall_memberpayment  ");
		addRecordPaymentSQL.append(" (id,payment_account_id,memberid,paycode,paymoney,paycontent,paysource,paydatetime,amorders) ");
		addRecordPaymentSQL.append(" VALUES ");
		addRecordPaymentSQL.append(" ('"+id+"','"+accountId+"','"+memberid+"','"+paycode+"',"+paymoney+",'"+paycontent+"',"+paysource+",now(),'"+amorders+"') ");
		
		
		DB db = null;
		 int res = 0;
		try {
			 db = DBFactory.newDB();
			 res = db.execute(addRecordPaymentSQL.toString());
		} catch (JDBCException e) {
			e.printStackTrace();
		} finally{
			try {
				db.close();
			} catch (JDBCException e) {
				e.printStackTrace();
			}
		}
		
		if(res==0){
			return "{\"CODE\" : \"1\",\"MSG\" : \"支付失败\"}";
		}else{
			return "{\"CODE\" : \"0\",\"MSG\" : \"支付成功\"}";
		}
		
	}
	
}
