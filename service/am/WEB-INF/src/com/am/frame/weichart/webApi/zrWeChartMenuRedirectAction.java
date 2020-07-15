package com.am.frame.weichart.webApi;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.member.MemberManager;
import com.am.frame.weichart.util.WeiChartAPIUtils;
import com.fastunit.Var;
import com.fastunit.util.Checker;
import com.p2p.service.IWebApiService;

/**
 * @author YueBin
 * @create 2016年7月15日
 * @version 
 * 说明:<br />
 * 微信公众账号 菜单接入转发地址
 */
public class zrWeChartMenuRedirectAction implements IWebApiService {

	private Logger logger=LoggerFactory.getLogger(getClass());
	
	/**OpenId**/
	private String openId;
	private String menu;
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//菜单名称
		menu=request.getParameter("menu");
		
		process(request, response);
		
		return null;
	}

	public String getOpenId() {
		return openId;
	}
	
	public String getMenu(){
		return menu;
	}
	
	
	public String process(HttpServletRequest request,HttpServletResponse response){
		
		String returnResult="";
		
		//菜单名称
		String menu=request.getParameter("menu");
		//微信返回code
		String code=request.getParameter("code");
		//微信返回 state 
		String state=request.getParameter("state");
		
		logger.info("微信OAth2.0验证返回信息 menu"+menu+" \tcode:"+code+" \tstate:"+state);
		
		JSONObject result=WeiChartAPIUtils.getSnsapiBaseInfoByCode(code);
		
		try{
			
			logger.info("微信OAth2.0验证返回信息 result："+result);
			
			if(result!=null&&result.has("openid")){
				openId=result.getString("openid");
			}
			
			if("home".equals(menu)||"getOpenId".equals(menu)){//公众账号进入，首页获取openiid
				
				JSONObject params=new JSONObject();
				
				if(result!=null&&result.has("openid")){
					openId=result.getString("openid");
					params.put("openid",openId );
					
					//拼接前台访问地址
					//http://yuebin616.iask.in/wap/index.jsp/?v=0.0.1&openid=$am{openid}/#/mall/weChartIn/
					String url =Var.get("zrredirectMenuUrl");
					
					url=url+URLEncoder.encode(openId, "UTF-8");
					
					logger.info("微信公众账号转发地址："+url);
					
					returnResult=url;
				}
			}else if("payment".equals(menu)){
				//支付页面获取信息
				JSONObject params=new JSONObject();
				if(result!=null&&result.has("openid")){
					
					String memberId="";
					
					openId=result.getString("openid");
					
					logger.info("state:"+state);
					if(state!=null){
						memberId=state.split("@")[1];
						state=state.split("@")[0];
					}
					logger.info("memberId:"+memberId);
					
					params.put("openid",openId );
					params.put("scoure","MpWeiChartPayment");
					params.put("state",state);
					
					//拼接前台访问地址
					String url =Var.get("redirectMenuPaymentUrl");
					url=url+URLEncoder.encode(params.toString(), "UTF-8");
					
					logger.info("微信公众账号转发地址："+url);
					
					//更新用户信息
					if(!Checker.isEmpty(memberId)){
						MemberManager memberManager=new MemberManager();
						memberManager.updateMemberOpenId(openId, memberId);
					}
					returnResult=url;
				}
				
			}
			
			response.sendRedirect(returnResult);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return returnResult;
	}
	
	

}
