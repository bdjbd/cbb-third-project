package com.am.frame.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jgroups.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.sdk.md5.MD5Util;
import com.p2p.service.IWebApiService;

public class TonkenAPI implements IWebApiService {

	private static final Logger log = LoggerFactory.getLogger(AmResServlet.class);
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		//项目账户秘钥
		String Tonken_Key = request.getParameter("check_code"); 
		
		//返回结果
		JSONObject resultjso = new JSONObject();
		
		try {
			if(request.getSession().getAttribute("API_secretKey")==null)
			{
				//服务器config.properties 配置文件秘钥
				String API_secretKey = MD5Util.getSingleton().textToMD5L32(PropertiesUtil.getPropertiesUtil().getValue("API_secretKey"));
				
				if(API_secretKey.equals(Tonken_Key))
				{
					String tonken = UUID.randomUUID().toString();
					
					request.getSession().setAttribute("platform_secretKey", tonken);
					resultjso.put("CODE","40000");
					resultjso.put("TonkenMSG","令牌申请成功!");
					resultjso.put("SECRETKEY", tonken);
				}else
				{
					resultjso.put("CODE", "40101");
					resultjso.put("TonkenMSG", "令牌无效!");
				}
			}else
			{
				if(Tonken_Key.equals(request.getSession().getAttribute("API_secretKey")))
				{
					String tonken = UUID.randomUUID().toString();
					request.getSession().setAttribute("platform_secretKey", tonken);
					resultjso.put("CODE","40000");
					resultjso.put("TonkenMSG","令牌申请成功!");
					resultjso.put("SECRETKEY", tonken);
				}else
				{
					resultjso.put("CODE", "40101");
					resultjso.put("TonkenMSG", "令牌无效!");
				}
			}
		} catch (Exception e) {
			try {
				resultjso.put("CODE", "40001");
				resultjso.put("TonkenMSG", "错误的请求!");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		log.info("令牌请求结果:"+resultjso.toString());
		
		return resultjso.toString();
	}
}
