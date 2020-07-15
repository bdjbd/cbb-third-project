package com.am.frame.weichart.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.fastunit.Var;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年7月7日
 * @version 
 * 说明:<br />
 * 微信获取微信SDK签名及权限
 */
public class GetWeiChartJSSDKWebApi implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		 JSONObject result=new JSONObject();
		
		//微信公众号Token
		String token=Var.get("weiChartToken");
		//微信公众号 AppId
		String appid=Var.get("weiChartAppId");
		//微信公众号 sppSecret
		String appSecret=Var.get("weiChartAppSecret");
		//微信公众账号调用页面URL
		String weiChartJSSDKUrl=request.getParameter("jsSdkUrl");
		
		try {
			JSONObject accessToke=new JSONObject();
			JSONArray apiListJa=new JSONArray();
			
			apiListJa.put("chooseWXPay");//微信支付
			apiListJa.put("onMenuShareTimeline");//onMenuShareTimeline 分享到朋友圈
			apiListJa.put("onMenuShareAppMessage");//onMenuShareAppMessage 分享给朋友
			apiListJa.put("onMenuShareQQ");//onMenuShareQQ
			apiListJa.put("onMenuShareWeibo");//onMenuShareWeibo
			apiListJa.put("hideOptionMenu");//hideOptionMenu
			apiListJa.put("showOptionMenu");//showOptionMenu
			apiListJa.put("hideMenuItems");//hideMenuItems
			apiListJa.put("showMenuItems");//showMenuItems
			apiListJa.put("hideAllNonBaseMenuItem");//hideAllNonBaseMenuItem
			apiListJa.put("showAllNonBaseMenuItem");//showAllNonBaseMenuItem
				
			
			//1,获取AccessToken
			String accessstoken=WeiChartAPIUtils.getAccessToken(token, appid, appSecret);
			logger.info("accessstoken:"+accessstoken);
			//2,获取JSTicket
			result=WeiChartAPIUtils.getJSApiConfig(weiChartJSSDKUrl, apiListJa, accessstoken, appid, appSecret);
			logger.info("获取JSTicket:"+result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return result.toString();
	}

}
