package com.am.frame.webapi.widthdrawaudit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.editflowstatus.EditFlowStatus;
import com.p2p.service.IWebApiService;

/**
 *@author 作者：yangdong
 *@create 时间：2016年7月25日 下午7:27:50
 *@version 说明：审核驳回
 */
public class WidthdrawAuditReject implements IWebApiService{
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String id = request.getParameter("id");
		String audit_opinion = request.getParameter("audit_opinion");
		String audit_person = request.getParameter("audit_person");
		String main_audit_opinion = request.getParameter("main_audit_opinion");
		String audit_result = request.getParameter("audit_result");
		EditFlowStatus efs = new EditFlowStatus();
		int res = efs.editFlowStatus("withdrawals", id, audit_person, audit_opinion,main_audit_opinion, audit_result);
		String result = "";
		if("0".equals(res)){
			result = "审核失败！";
		}else{
			result = "审核通过！";
		}
		return result;
	}
}
