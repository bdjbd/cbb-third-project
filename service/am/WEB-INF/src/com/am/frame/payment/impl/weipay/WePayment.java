package com.am.frame.payment.impl.weipay;

import org.json.JSONException;
import org.json.JSONObject;

import com.am.frame.payment.entity.PaymentRequestEntity;
import com.am.frame.payment.impl.AbstractPaymentUniformOrder;
import com.fastunit.util.Checker;

/**
 * 微信支付下单接口
 * @author mac
 *
 */
public class WePayment extends AbstractPaymentUniformOrder
{

	@Override
	public JSONObject execute(PaymentRequestEntity request) 
	{
		JSONObject resultJson = null;
		
		JSONObject DataSaveresult = saveRecord(request);
		
		String API_key = "";
		try {
			API_key = DataSaveresult.getString("API_KEY");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//第三方账户 微信支付
		if(request!=null)
		{
    		try 
    		{
				resultJson = new JSONObject();
				
				JSONObject pprepayObj = null;
				
				WePayRequest wPayRequest = new WePayRequest();

				if(!Checker.isEmpty(request.getPayId()) && !Checker.isEmpty(request.getPayMoney()))
				{
					JSONObject jso = new JSONObject();
					jso.put("pay_id", request.getPayId());
					jso.put("total_fee",request.getPayMoney() );//params.get("pay_money")
					pprepayObj = wPayRequest.doWeinXinRequest(request.getRequest(),request.getResponse(),jso.toString(),API_key);
				}
				
				//调用微信返回数据
				
				if("0".equals(pprepayObj.get("retcode")))
				{
					resultJson.put("prepay_id", pprepayObj.get("prepay_id"));
					resultJson.put("appId",pprepayObj.get("appid"));
					resultJson.put("finalsign", pprepayObj.get("finalsign"));
					resultJson.put("msg","获取统一支付单号成功");
					resultJson.put("account_type_code", "WECHAT_ACCOUNT_MODE");
					resultJson.put("code","0");
				}else
				{
					resultJson.put("code", "0");
					resultJson.put("msg", "订单获取失败");

				}
    		} catch (Exception e) {
				e.printStackTrace();
			}
	    		
		}
		return resultJson;
	}

}
