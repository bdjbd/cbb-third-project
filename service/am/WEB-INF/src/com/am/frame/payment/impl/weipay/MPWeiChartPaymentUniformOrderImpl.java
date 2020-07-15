package com.am.frame.payment.impl.weipay;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.payment.impl.AbstractPaymentUniformOrder;
import com.am.frame.weichart.beans.BizOrder;
import com.am.frame.weichart.conf.WeiChartConfig;
import com.am.frame.weichart.util.Utils;
import com.am.frame.weichart.util.WeiChartAPIUtils;

/**
 * @author YueBin
 * @create 2016年7月16日
 * @version 说明:<br />
 *          微信公众账号支付统一下单接口
 */
public class MPWeiChartPaymentUniformOrderImpl extends
		AbstractPaymentUniformOrder {

	/** MpWeiPayAPIKey 微信公众平台支付PayAPIKey **/
	private static String weiPayAPIKey = "MpWeiPayAPIKey";

	@Override
	public JSONObject execute(PaymentRequestEntity request) {
		
		JSONObject returnResult=new JSONObject();
		
		saveRecord(request);

		// 微信公众账号支付
		if (request != null) {
			try {
				JSONObject result = new JSONObject();

				/**
				 * 用户id
				 */
				String memberId =request.getMemberId() ;//request.getParameter("memberId");
				/**
				 * 出账账户code
				 */
				String outAccountCode =request.getOutAccountCode() ;//request.getParameter("outAccountCode");
				/**
				 * 入账账户code
				 */
				String inAccountCode =request.getInAccountCode();// request.getParameter("inAccountCode");
				/**
				 * 支付ID
				 */
				String pay_id =request.getPayId() ;//request.getParameter("pay_id");
				/**
				 * 支付金额
				 */
				String pay_money =request.getPayMoney() ;//request.getParameter("pay_money");
				/**
				 * 支付类型  1 支付 2 充值
				 */
				String pay_type =request.getPayType() ;//request.getParameter("pay_type");
				/**
				 * 账户类型  1 系统账户 2 支付宝 3 微信 4 银联
				 */
				String account_type =request.getAccountType() ;//request.getParameter("account_type");
				/**
				 * 业务参数 
				 */
				String business =request.getBusiness() ;//request.getParameter("business");
				/**
				 * 入账描述
				 */
				String inremakes =request.getInrRemakes() ;//request.getParameter("inremakes");
				/**
				 * 出账描述
				 */
				String outremakes =request.getOutRemakes() ;//request.getParameter("outremakes");
				// 1,获取订单信息
				String body =request.getCommodityName() ;//request.getParameter("commodityName");
				logger.info(body);

				String openId =request.getOpenid() ;//request.getParameter("openid");
				logger.info("openid:" + openId);

				String attchValue =request.getAttchStr() ;//request.getParameter("attchStr");
				
				if(attchValue==null||"null".equals(attchValue)){
					attchValue=System.currentTimeMillis()+"";
				}

				if (attchValue != null) {
					attchValue = attchValue.replaceAll("@", "=");
				}
				
				// 下单服务器IP地址
				String ip = request.getRemoteAddr();

				BizOrder bizOrder = new BizOrder();
				bizOrder.setId(pay_id);
				bizOrder.setDescript(body);
				bizOrder.setAttch(attchValue);
				logger.info("attchValue======:" + bizOrder.getAttch());

				// 系单位为元，需要转换成分，乘以100
				int totalFree=(int) (Double.parseDouble(pay_money)*100);
				bizOrder.setTotalFee(totalFree);
				

				// 2,调用统一下单接口，下单，然后返回调用微信支付
				WeiChartAPIUtils weiChartApi = new WeiChartAPIUtils();
				String xml = weiChartApi.createPayInfo(bizOrder, ip, openId);

				logger.info("统一订单接口返回数据：\n" + xml);

				Map<String, String> map = WeiChartAPIUtils.getUnifiedOrder(xml);
				result.put("appId", WeiChartConfig.getInstance().getAppId());// "appId"
																				// "wx2421b1c4370ec43b",
																				// //公众号名称，由商户传入
				result.put("timeStamp", "" + System.currentTimeMillis() / 1000);// "timeStamp"：" 1395712654",
																				// //时间戳，自1970年以来的秒数
				result.put("nonceStr", Utils.getRandomStr(8));// "nonceStr" ：
																// "e61463f8efa94090b1f366cccfbbb444",
																// //随机串
				result.put("package", "prepay_id=" + map.get("prepay_id"));// "package"
																			// "prepay_id=u802345jgfjsdfgsdg888",
				result.put("signType", "MD5");// "signType" ： "MD5", //微信签名方式：
				String sign = getSing(result);
				// sign=map.get("sign");
				result.put("paySign", sign);// "paySign" ：

				logger.info("微信公众平台统一下单接口返回参数：" + result.toString());
				
				returnResult.put("code","0");
				returnResult.put("account_type_code","MP_WECHAT_ACCOUNT_MODE");
				returnResult.put("UNIFORM",result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		

		return returnResult;
	}

	private String getSing(JSONObject result) throws JSONException {

		String stringA = "appId=" + result.getString("appId") + "&nonceStr="
				+ result.getString("nonceStr") + "&package="
				+ result.getString("package") + "&signType=MD5&timeStamp="
				+ result.getString("timeStamp") + "&key="
				+ WeiChartConfig.getInstance().getValue(weiPayAPIKey);

		String sign = DigestUtils.md5Hex(stringA).toUpperCase();

		return sign;
	}

}