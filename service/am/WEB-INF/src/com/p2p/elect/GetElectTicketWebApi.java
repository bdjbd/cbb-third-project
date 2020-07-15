package com.p2p.elect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

/**
 * 电子券WebAPI 接口用来处理电子券相关的业务接口，具体业务和ACTION_NAME相关
 * @author Administrator
 *
 */
public class GetElectTicketWebApi implements IWebApiService {

	/**请求动作名称**/
	private static final String ACTION_NAME="ACTION_NAME";
	private static final String BASE_PATH="com.p2p.elect.action"; 
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		
		String result="";
		
		String action=request.getParameter(ACTION_NAME);
		String memberCode=request.getParameter("member_code");
		
		//获取会员抵现券
		if("getMortgage".equalsIgnoreCase(action)){
			result=ElectTicketManager.getInstance().getMemberElectTicketToJson(memberCode,"3");
		}
		
		return result;
	}

}
