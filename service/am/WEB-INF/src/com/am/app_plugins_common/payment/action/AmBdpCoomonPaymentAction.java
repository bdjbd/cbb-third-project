package com.am.app_plugins_common.payment.action;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.app_plugins_common.entity.PayBusiness;
import com.am.app_plugins_common.service.PaymentService;
import com.am.frame.payment.IPaymentUniformOrder;
import com.am.frame.payment.wechatpay.PayDao.QrcodePay;
import com.am.frame.payment.wechatpay.entity.WxPayDto;
import com.fastunit.Var;
import com.fastunit.context.ActionContext;
import com.fastunit.context.LocalContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.Action;


/**
 * 平台确认支付Action
 * @author wz
 * 1，调用bdp平台统一下单接口
 *
 */
public class AmBdpCoomonPaymentAction implements Action {

	private Logger logger=LoggerFactory.getLogger(getClass());

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		
		String payId=ac.getRequestParameter("common_confirm_payment.ordercode");
		
		String memberId=ac.getRequestParameter("common_confirm_payment.memberid");
		
		if(payId!=null){
			payId=payId.replaceAll("-", "");
		}
		ac.setRequestParameter("pay_id", payId);
		
		String payMoney=ac.getRequestParameter("common_confirm_payment.pay_money");
		ac.setRequestParameter("pay_money", payMoney);
		
		String outremakes=ac.getRequestParameter("common_confirm_payment.outremakes");
		ac.setRequestParameter("outremakes", outremakes);
		
		String out_account_code=ac.getRequestParameter("common_confirm_payment.payment_month");
		ac.setRequestParameter("out_account_code", out_account_code);
		
		String businessStr=ac.getRequestParameter("common_confirm_payment.business");
		ac.setRequestParameter("business", businessStr);
		
		String successUrl=ac.getRequestParameter("common_confirm_payment.success_url");
		ac.setRequestParameter("success_url", successUrl);
		
		String lostUrl=ac.getRequestParameter("common_confirm_payment.lost_url");
		ac.setRequestParameter("lost_url", lostUrl);
		
		String in_account_code=ac.getRequestParameter("common_confirm_payment.in_account_code");
		ac.setRequestParameter("in_account_code", in_account_code);
		
		String account_type=ac.getRequestParameter("common_confirm_payment.account_type");
		ac.setRequestParameter("account_type", account_type);
		
		String pay_type=ac.getRequestParameter("common_confirm_payment.pay_type");
		ac.setRequestParameter("pay_type", pay_type);
		
		String inremakes=ac.getRequestParameter("common_confirm_payment.inremakes");
		ac.setRequestParameter("inremakes", inremakes);
		
		String commodityName=ac.getRequestParameter("common_confirm_payment.commodityname");
		ac.setRequestParameter("commodityName", commodityName);
		
		String openId = ac.getRequestParameter("common_confirm_payment.openid");
		ac.setRequestParameter("openid", openId);
		
		String attchStr = ac.getRequestParameter("common_confirm_payment.attchstr");
		ac.setRequestParameter("attchStr", attchStr);
		
		PaymentService ps=new PaymentService();
		PayBusiness business=new PayBusiness();
		
		business.setPayment_id(payId);
		business.setIn_account_code(in_account_code);
		business.setMemberid(memberId);
		business.setOrders(payId);
		business.setPaymoney(payMoney);
		business.setSuccess_call_back(successUrl);
		
		DB db=null;
		
		try{
			
			logger.info(memberId+"进行充值操作。");
			
			HttpServletRequest request=LocalContext.getLocalContext().getHttpServletRequest();
			HttpServletResponse response=LocalContext.getLocalContext().getHttpServletResponse();
			
			JSONObject result=ps.amBdpPayment(request,
					response,
					memberId,
					out_account_code,
					payId,
					payMoney,
					account_type,
					businessStr,//business.getBusinessJSON().toString(),
					outremakes,
					in_account_code,
					pay_type,
					inremakes,
					commodityName,
					attchStr,
					openId,
					db);
					
			logger.info("result:"+result);
			if(result!=null&&result.has("code")&&"0".equals(result.get("code")+"")){
				//统一下单成功，弹出支付界面
				String accountTypeCode=result.getString(IPaymentUniformOrder.ACCOUNT_TYPE_CODE);
				
				logger.info("支付界面accountTypeCode:"+accountTypeCode);
				
				String url="";
				
				switch (accountTypeCode) {
				case IPaymentUniformOrder.ALIPAY_WEB_PAY:
					//支付宝支付
					url=alipyaWebPay(payId, commodityName, payMoney, ac);
					break;

				case IPaymentUniformOrder.WEI_XIN_SANC_PAY:
					url=weiXinScanPayment(payId, commodityName, payMoney, ac);
					break;
				}
				
				
				logger.info("调用bdp平台统一下单接口URL："+url);
				
				ac.getActionResult().setSuccessful(true);
				ac.getActionResult().setUrl(url);
				
				
			}else{
				logger.info("支付参数不正确");
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ac;
	}
	
	
	private String weiXinScanPayment(String payId,
			String commodityName,
			String payMoney,
			ActionContext ac) throws Exception {
		
		String returnCode ="";
		
		String spbillCreateIp=LocalContext.getLocalContext().getHttpServletRequest().getRemoteAddr();		//支付Ip
		String nonce =payId;// payObj.getString("NONCE");//订单ID
		String TradeType ="NATIVE";// payObj.getString("TRADETYPE");	//支付类型 APP,NATIVE,JSAPI
		String OpenID ="";		//如果为NATIVE可为空
		
		//扫码支付
		WxPayDto wxentity = new WxPayDto();
		wxentity.setNotifyUrl(Var.get("wepay_mobile_notify"));
		wxentity.setTotalFee(payMoney);
		wxentity.setSpbillCreateIp(spbillCreateIp);
		wxentity.setBody(commodityName);
		wxentity.setNonce(nonce);
		wxentity.setOpenId(OpenID);
		wxentity.setTrade_type(TradeType);
		wxentity.setOrderId(payId);
		
	    QrcodePay.getInstance();
		//获取二维码内容
		returnCode = QrcodePay.getCodeurl(wxentity);
		
		logger.info("returnCode:"+returnCode);
		
		String reuslt="/weipay/weipay.jsp?time="+
				System.currentTimeMillis()+
				"&weiPayQrcode="+returnCode+ 
				"&commodityName="+URLEncoder.encode(commodityName,"UTF-8")+
				"&payMoney="+payMoney+
				"&payId="+payId;;
		
		return reuslt;
	}


	public String alipyaWebPay(String payId,String commodityName,String payMoney ,ActionContext ac) throws Exception{
		
		if(commodityName==null){
			commodityName="";
		}
		
		//支付宝支付
		String aliPayWebParas="seller=";//+AlipayConfig.wapNotify_url;//Var.get("weiChartNotifyUrl");
        aliPayWebParas+="&WIDout_trade_no="+payId;
        aliPayWebParas+="&WIDsubject=s";
        aliPayWebParas+="&WIDtotal_fee="+payMoney;
        aliPayWebParas+="&WIDshow_url=";
        aliPayWebParas+="&WIDbody=";//+URLEncoder.encode(commodityName,"UTF-8");
//     //支付宝网页支付
        
        return "/alipay/wap/alipayapi.jsp?"+aliPayWebParas;
		
	}
	
	
}
