package com.am.frame.weichart.webApi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.fastunit.Var;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年9月8日
 * @version 
 * 说明:<br />
 * 更具体是信息获取会员的openid
 * 此模块是获取微信开放平台的openid，不是微信公众账号的OPENID
 */
public class GetWeiChartOpenPlatformUserBaseInfo implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		JSONObject result=new JSONObject();
		
		//会员ID
		String memberId=request.getParameter("member_id");
		//code
		String code=request.getParameter("code");
		
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?"
				+"appid="+Var.get("wepay_mobile_appid")//微信开放平台APPID
				+"&secret="+Var.get("wepay_mobile_appsecret")//5bf5e4d67875e50a03b6a605ea50175f
				+"&code="+code+"&grant_type=authorization_code";
		
		JSONObject res=WeiChartAPIUtils.getHttpsURL(url);
		
		logger.info("result:"+result.toString());
		try{
			//此ID为微信开放平台的OPENID，和微信公众账号的不同 
			String openId=res.getString("openid");
			
			MemberManager mm=new MemberManager();
			mm.updateMemberAPPOpenId(openId, memberId);
			
			
			result.put("CODE","0");
			result.put("RESULT",res);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result.toString();
	}

}
