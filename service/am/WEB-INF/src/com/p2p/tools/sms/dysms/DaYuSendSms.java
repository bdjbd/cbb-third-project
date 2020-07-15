package com.p2p.tools.sms.dysms;



import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.p2p.tools.sms.ISendSmsMessage;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * 短信发送接口
 * 
 * @author guorenjie
 *
 */
public class DaYuSendSms implements ISendSmsMessage {

	private Logger logger = LoggerFactory.getLogger(getClass());
	static final String url = "http://gw.api.taobao.com/router/rest";
	static final String appkey = "24622688";
	static final String secret = "73620e2f5f21ecf04f7cc627438dd997";

	public static AlibabaAliqinFcSmsNumSendResponse  sendSms(String phone, String content) throws ApiException{
		System.err.println("----------进入短信发送接口DaYuSendSms----------");
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend( "" );
		req.setSmsType( "normal" );
		req.setSmsFreeSignName( "汽车公社" );//短信签名
		req.setRecNum(phone);				//手机号
		String templateCode = "";
		String templateParam= "";
		JSONObject template;
		try {
			template = new JSONObject(content);
			templateCode = template.getString("number");
			templateParam = template.get("content").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		req.setSmsParamString(templateParam);
		req.setSmsTemplateCode(templateCode);//短信模板
		AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
		System.err.println("----------输出模板变量templateParam----------"+templateParam);
		System.err.println("----------输出短信模板templateCode----------" + templateCode );
		System.out.println(rsp.getBody());
		return rsp;		
	}
	@Override
	public String send(String content, String phone) {
		try {
			sendSms(phone, content);
		} catch (ApiException e) {
			e.printStackTrace();
		}	
		return "";
	}
}
