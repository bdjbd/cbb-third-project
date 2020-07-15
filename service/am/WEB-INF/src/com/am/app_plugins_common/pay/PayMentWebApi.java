package com.am.app_plugins_common.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.am.app_plugins_common.service.PaymentService;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.p2p.service.IWebApiService;

/**
 * 通用支付类
 * @author mac
 *
 *商品支付时参数：<code>
 * memberId:d69ceff9-086b-4d13-b05b-d8cc84731bdf  //交易社员ID
 * inAccountCode: //入账账号编号
 * outAccountCode:CONSUMER_ACCOUNT   //出账账号编号
 * pay_id:4F730553132C49B2AEB4D65C4C054DDF//支付ID
 * pay_money:2  //支付金额 单位元
 * pay_type:1   //支付类型  1 支付 2 充值
 * account_type:2  //账户类型  1:系统账户 ; 2:支付宝 ;3:微信; 4:银联
 * business:{   //业务参数集合
 *			"payment_id":"4F730553132C49B2AEB4D65C4C054DDF",
 *			"memberid":"d69ceff9-086b-4d13-b05b-d8cc84731bdf",
 *			"orders":"88AAFF8CA9E2436F86D59E97ABFF9AE0,",    //orderID 订单ID
 *			"paymoney":2,
 *			"success_call_back":"com.am.frame.order.process.OrderBusinessCallBack"
 *		   }
 * inremakes:,一级绿豆   //入账备注信息
 * outremakes:,一级绿豆  //出账备注信息
 * platform:1   //平台  平台类型  1；移动端  2 pc端
 * openid:   //微信openid
 * commodityName:一级绿豆   //商品名称
 * </code>
 **/
public class PayMentWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		
		/**
		 * 用户id
		 */
		String memberId = request.getParameter("memberId");
		/**
		 * 入账账户code
		 */
		String outAccountCode = request.getParameter("outAccountCode");
		/**
		 * 出账账户code
		 */
		String inAccountCode = request.getParameter("inAccountCode");
		/**
		 * 订单号
		 */
		String pay_id = request.getParameter("pay_id");
		/**
		 * 支付金额  单位元
		 */
		String pay_money = request.getParameter("pay_money");
		/**
		 * 支付类型  1 支付 2 充值
		 */
		String pay_type = request.getParameter("pay_type");
		/**
		 * 账户类型  1 系统账户 2 支付宝 3 微信 4 银联
		 */
		String account_type = request.getParameter("account_type");
		/**
		 * 业务参数 
		 */
		String business = request.getParameter("business");
		/**
		 * 入账描述
		 */
		String inremakes = request.getParameter("inremakes");
		/**
		 * 出账描述
		 */
		String outremakes = request.getParameter("outremakes");
		
		
		/**
		 * 平台类型  1 移动端  2 pc端
		 */
		String platform = request.getParameter("platform");
		
		String commodityName=request.getParameter("commodityName");
		
		String attchStr=request.getParameter("attchStr");
		
		String openid=request.getParameter("openid");
		
		JSONObject pprepayObj = null;
		
		//支付返回结果
		JSONObject resultJson  = new JSONObject();
		DB db = null;
		
		JSONObject paramsObj = null;
		//引入支付Service
		PaymentService ps=new PaymentService();
		
		try{
			db=DBFactory.newDB();
			//支付操作方法
			resultJson =ps.amBdpPayment(request,
					response,
					memberId,
					outAccountCode,
					pay_id,
					pay_money,
					account_type,
					business,
					outremakes,
					inAccountCode,
					pay_type,
					inremakes,
					commodityName,
					attchStr, 
					openid,
					db);
			
//			resultJson = ps.amBdpPayment(request, 
//					response, 
//					memberId, 
//					outAccountCode,
//					pay_id,
//					pay_money,
//					account_type,
//					business,
//					outremakes,
//					inAccountCode,
//					pay_type,
//					inremakes, 
//					request.getParameter("commodityName"),
//					request.getParameter("attchStr"), 
//					request.getParameter("openid"),
//					db);
//			resultJson = ps.amBdpPayment(
//					request,
//					response,
//					memberId,
//					outAccountCode,
//					pay_id,
//					pay_money,
//					account_type,
//					business,
//					outremakes,
//					db);
			
			db.close();
		}catch(Exception e){
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
		
		
		return resultJson.toString();
	}


}
