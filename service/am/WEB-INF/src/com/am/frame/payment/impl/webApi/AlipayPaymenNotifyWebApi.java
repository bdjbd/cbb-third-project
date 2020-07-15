package com.am.frame.payment.impl.webApi;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.payment.RefundManager;
import com.am.frame.transactions.callback.BusinessCallBack;
import com.am.frame.transactions.virement.VirementManager;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.exception.JDBCException;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年7月2日
 * @version Os
 * 说明:<br />
 * 此接口主要用于支付宝手机支付功能。
 * 支付宝支付 回调接口，
 * 1，更新支付完成时间
 * 2，修改业务完成状态
 * 3，回调业务接口
 *http://notify.java.jpxx.org/index.jsp?discount=0.00&payment_type=1
 *&subject=测试&trade_no=2013082244524842&
 *buyer_email=dlwdgl@gmail.com&gmt_create=2013-08-22 14:45:23
 *&notify_type=trade_status_sync&quantity=1
 *&out_trade_no=082215222612710&seller_id=2088501624816263
 *&notify_time=2013-08-22 14:45:24&body=测试测试&
 *trade_status=TRADE_SUCCESS&is_total_fee_adjust=N&total_fee=1.00&
 *gmt_payment=2013-08-22 14:45:24&seller_email=xxx@alipay.com&price=1.00&
 *buyer_id=2088602315385429&notify_id=64ce1b6ab92d00ede0ee56ade98fdf2f4c&use_coupon=N&
 *sign_type=RSA&sign=1glihU9DPWee+UJ82u3+mw3Bdnr9u01at0M/xJnPsGuHh+JA5bk3zbWaoWhU6GmLab3dIM4JNdktTcEUI9/FBGhgfLO39BKX/eBCFQ3bXAmIZn4l26fiwoO613BptT44GTEtnPiQ6+tnLsGlVSrFZaLB9FVhrGfipH2SWJcnwYs= 
 * 
 */
public class AlipayPaymenNotifyWebApi implements IWebApiService {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String reuslt=processAlipayTrans(request, response);
		
		return reuslt;
	}
	
	
	/**
	 * 成功返回 success 失败返回  fail
	 * @param request
	 * @param response
	 * @return
	 */
	public String processAlipayTrans(HttpServletRequest request,
			HttpServletResponse response){
		
		String reuslt="";
		String requestMsg="";
		
		//商户订单号  系统中为支付单号
		String out_trade_no ="";
		
		DB db=null;
		
		try {
			
			db=DBFactory.newDB();
		
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//				valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
				
				requestMsg+="\t\n"+name+"="+valueStr;
				
			}
			logger.info("获取支付宝POST过来反馈信息:"+requestMsg);
			
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号  系统中为支付单号
			out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
			//交易状态
			/*
			 *  WAIT_BUYER_PAY	交易创建，等待买家付款。
				TRADE_CLOSED	在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易。
				TRADE_SUCCESS	交易成功，且可对该交易做操作，如：多级分润、退款等。
				TRADE_FINISHED	交易成功且结束，即不可再做任何操作。
			 */
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
			//验证是否为支付宝调用回调接口数据
//			boolean signVerified = AlipayNotify.verify(params);
			boolean signVerified=true;
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			logger.info("支付ID："+out_trade_no+"\t交易状态："+trade_status+"\t支付宝交易号："+trade_no+"\t"+"是否为支付宝调用:"+signVerified);

			
			
			System.out.println("支付结果成功!:"+signVerified);
			
			
			if(signVerified)
			{//验证成功
				//退款成功,请在这里加上商户的业务逻辑程序代码
				if(params.containsKey("refund_status")&&"REFUND_SUCCESS".equals(params.get("refund_status")))
				{
					//退款成功
					RefundManager refundManager=new RefundManager();
					
					JSONObject param=new JSONObject();
					
					param.put("out_trade_no", out_trade_no);
					param.put("trade_no", trade_no);
					param.put("trade_status", trade_status);
					param.put("out_trade_no", out_trade_no);
					param.put("out_trade_no", out_trade_no);
					
					logger.info("订单退款成功 param："+param.toString());
					
					refundManager.processRefundCallBack(db, out_trade_no, param);
					
				}
				
				logger.info("交易时间 param："+new Date()+"\t");
				
				if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
					//交易成功 ，交易成功，且可对该交易做操作，如：多级分润、退款等。
					// 交易成功且结束，即不可再做任何操作。
					//，判断退款还是支付
						//支付成功
					logger.info("交易成功 param："+params.toString()+"\t");
					if(!params.containsKey("refund_status")){
						//本次交易支付的订单金额，单位为人民币（元）
						String cashFree=new String(request.getParameter("total_fee").getBytes("ISO-8859-1"),"UTF-8");
						if(!Checker.isEmpty(cashFree)){
							cashFree=VirementManager.changeY2F(cashFree)+"";
						}
						
						BusinessCallBack businessCallBack = new BusinessCallBack();
//						businessCallBack.callBack(out_trade_no, db,"1");
						Map<String,String> cparams=new HashMap<String,String>();
						cparams.put(BusinessCallBack.CASH_FREE, cashFree);
						cparams.put(BusinessCallBack.OTHER_ORDER_CODE, trade_no);
						
						businessCallBack.callBack(out_trade_no, db,cparams,"1");
						//更新交易成功时间
					}
				}
					
				reuslt="success";	//请不要修改或删除
	
			}else{//验证失败
				reuslt="fail";
				
			}
		
		} catch (Exception e) {
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
		logger.info("支付宝通知接口返回数据:"+reuslt+"\tout_trade_no:"+out_trade_no);
		return reuslt;
	}
	
}
