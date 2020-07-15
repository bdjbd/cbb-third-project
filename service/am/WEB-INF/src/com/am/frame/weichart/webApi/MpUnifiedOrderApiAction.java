package com.am.frame.weichart.webApi;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.weichart.beans.BizOrder;
import com.am.frame.weichart.conf.WeiChartConfig;
import com.am.frame.weichart.util.Utils;
import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年3月20日
 * @version 说明:<br />
 *          微信公众平台 统一下单接口
 */
public class MpUnifiedOrderApiAction implements IWebApiService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	/** MpWeiPayAPIKey 微信公众平台支付PayAPIKey **/
	private static String weiPayAPIKey = "MpWeiPayAPIKey";

	private String getSing(JSONObject result) throws JSONException {

		String stringA = "appId=" + result.getString("appId") + "&nonceStr="
				+ result.getString("nonceStr") + "&package="
				+ result.getString("package") + "&signType=MD5&timeStamp="
				+ result.getString("timeStamp") + "&key="
				+ WeiChartConfig.getInstance().getValue(weiPayAPIKey);

		String sign = DigestUtils.md5Hex(stringA).toUpperCase();

		return sign;
	}

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		// 1,获取订单信息

		String body = request.getParameter("body");
		logger.info(body);

		String openId = request.getParameter("openid");
		logger.info("openid:" + openId);

		String attchValue = request.getParameter("attchStr");

		if (attchValue != null) {
			attchValue = attchValue.replaceAll("@", "=");
		}
		
		//下单服务器IP地址
		String ip = request.getRemoteAddr();

		BizOrder bizOrder = new BizOrder();
		bizOrder.setDescript(request.getParameter("body").toString());
		bizOrder.setId(request.getParameter("ordernumber").toString());
		bizOrder.setTotalFee(Integer.valueOf(request.getParameter("total_fee")
				.toString()));
		bizOrder.setDescript(body);
		bizOrder.setId(request.getParameter("ordernumber") + "");
		bizOrder.setAttch(attchValue);
		logger.info("attchValue======:" + bizOrder.getAttch());

		// 系单位为元，需要转换成分，乘以100
		bizOrder.setTotalFee(Integer.valueOf(request.getParameter("total_fee")
				+ "") * 100);
		// 调试数据，默认为1分钱
		// bizOrder.setTotalFee(1);

		// 2,调用统一下单接口，下单，然后返回调用微信支付
		WeiChartAPIUtils weiChartApi = new WeiChartAPIUtils();
		String xml = weiChartApi.createPayInfo(bizOrder, ip, openId);

		// Map<String, String> payInfoMap = WeiChartAPIUtils.xmlStrToMap(xml);

		logger.info("统一订单接口返回数据：\n" + xml);

		Map<String, String> map = WeiChartAPIUtils.getUnifiedOrder(xml);

		JSONObject result = new JSONObject();

		try {
			result.put("appId", WeiChartConfig.getInstance().getAppId());// "appId"
																			// "wx2421b1c4370ec43b",
																			// //公众号名称，由商户传入
			result.put("timeStamp", "" + System.currentTimeMillis() / 1000);// "timeStamp"：" 1395712654",
																			// //时间戳，自1970年以来的秒数
			result.put("nonceStr", Utils.getRandomStr(8));// "nonceStr" ：
															// "e61463f8efa94090b1f366cccfbbb444",
															// //随机串
			result.put("package", "prepay_id=" + map.get("prepay_id"));// "package"
																		// ：
																		// "prepay_id=u802345jgfjsdfgsdg888",
			result.put("signType", "MD5");// "signType" ： "MD5", //微信签名方式：
			String sign = getSing(result);
			// sign=map.get("sign");
			result.put("paySign", sign);// "paySign" ：
										// "70EA570631E4BB79628FBCA90534C63FF7FADD89"
										// //微信签名
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("微信公众平台同意下单接口返回参数："+result.toString());

		return result.toString();
	}

}
