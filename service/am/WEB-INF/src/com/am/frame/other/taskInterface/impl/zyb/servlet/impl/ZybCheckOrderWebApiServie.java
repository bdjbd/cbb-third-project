package com.am.frame.other.taskInterface.impl.zyb.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.am.qmyx.orders.OrdersCommissionCalculationImpl;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;

/**
 * @author YueBin
 * @create 2016年6月28日
 * @version 
 * 说明:<br />
 *智游宝 票据核销接口
 *
 *
 第三方订单号	order_no	String	
第三方子订单号	sub_order_no	String	
状态	status	String	check:检票
检票数量	checkNum 	String	
退票数量	returnNum 	String	
总数量	total 	String	
签名	sign	String	
私钥	privateKey	String	
需要对接方提供回调 URL
回调参数名例如order_no第三方订单号 sub_order_no 第三方子订单号
参数说明：
status : check状态:检票
checkNum 检票数量
returnNum 退票数量
total 总数量
我们系统会以订单完结时，回调通知。
检票完成：
url?order_no={orderNO}&status=check&sub_order_no=11111111111&checkNum=1&returnNum=5&total=10&checkTime=URLEncoder.encode(checkTime,”UTF-8”)&sign=md5(order_no={orderNO}{privateKey})

 
 
 */
public class ZybCheckOrderWebApiServie extends ZybAbstraceWebApiService {

	@Override
	protected String processBusess(DB db, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String orderNo=request.getParameter("order_no");//第三方订单号	order_no	String	
		String subOrderNo=request.getParameter("sub_order_no");//第三方子订单号	sub_order_no	String	
		String status=request.getParameter("status");//状态	status	String	check:检票
		String checkNum=request.getParameter("checkNum");//检票数量	checkNum 	String	
		String returnNum=request.getParameter("returnNum");//退票数量	returnNum 	String	
		String total=request.getParameter("total");//总数量	total 	String	
		String sign=request.getParameter("sign");//签名	sign	String	
		String privateKey=request.getParameter("privateKey");//私钥	privateKey	String	
		
		//1,检查状态字段
		if("check".equalsIgnoreCase(status)){
			//检票
			//2更新检票数量以及更新退表数量
			String updateSQL="UPDATE mall_MemberOrder SET used_number=COALESCE(used_number,0)+?,"
					+ " return_num=COALESCE(return_num,0)+?,effective_num=COALESCE(effective_num,0)-?,completedate=now()"
					+ ",b2b_check_in_time=now() WHERE id=? ";
			
			
			
			db.execute(updateSQL,new String[]{
					checkNum,returnNum,checkNum,orderNo
			},new int[]{
					Type.INTEGER,Type.INTEGER,Type.INTEGER,Type.VARCHAR
			});
		}
		
		return "success";
	}

}
