package com.am.frame.weichart.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.fastunit.Var;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年7月28日
 * @version 
 * 说明:<br />
 * 获取用户微信基本信息
 */
public class GetMPUserBaseInfoWebApi  implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
//		var url="http://open.weixin.qq.com/connect/oauth2/authorize?appid=wx090cd4187ae83c3f&redirect_uri=http://hlw.triph.cn/AmRes/com.am.frame.weichart.webApi.WeChartMenuRedirectAction.do?
		//menu=home&response_type=code&scope=snsapi_base&state=menu001#wechat_redirect ";
//	    $http.post(url)
//	            .success(function(data){
//	        console.error(data);
//	    });
		
		String url="https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid="+Var.get("weiChartAppId")+"&redirect_uri="
				+ "http://hlw.triph.cn/AmRes/com.am.frame.weichart.webApi.WeChartMenuRedirectAction.do?"
				+ "menu=home&response_type=code&scope=snsapi_base&state=menu001#wechat_redirect ";
		
		
		JSONObject result=WeiChartAPIUtils.getHttpsURL(url);
		
		logger.info(result.toString());
		
		return result.toString();
	}

}
