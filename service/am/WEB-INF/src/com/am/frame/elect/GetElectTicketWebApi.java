package com.am.frame.elect;

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
	
	@Override
	public String execute(HttpServletRequest request,HttpServletResponse response) {
		
		String result="";
		
		//接口功能名称
		String action=request.getParameter(ACTION_NAME);
		//会员ID
		String memberCode=request.getParameter("memberid");
		//企业电子券ID
		String electTicketId=request.getParameter("EterpElectTicketID");
		
		
		//获取会员抵现券
		if("getMortgage".equalsIgnoreCase(action)){
			
			result=ElectTicketManager.getInstance().getMemberElectTicketToJson(memberCode,"3");
		
		}else if("ExchangeTicket".equalsIgnoreCase(action)){
			//积分兑换
			result=ElectTicketManager.getInstance().exchangeTicket(memberCode, electTicketId);
		}
		
		return result;
	}

}
