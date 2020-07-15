package com.am.frame.webapi.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.am.frame.badge.AMBadgeManager;
import com.p2p.service.IWebApiService;

/**
 * @author Mike
 * @create 2014年11月19日
 * @version 
 * 说明:<br />
 * 获取用户徽章
 * 参数：<br />
 * @params
 * MEMBERID用户编号;
 * BADGECODE 徽章模板编码 ;
 * PARAM 徽章参数;
 */
public class GetUserBadge implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		//用户编号
		String memberId=request.getParameter("MEMBERID");
		//徽章模板编码 
		String badgeCode=request.getParameter("BADGECODE");
		//徽章参数
		String params=request.getParameter("PARAM");
		
		AMBadgeManager bm=new AMBadgeManager();
		
		bm.init(memberId, badgeCode);
		
		String result= bm.getBadgeValueOfName(params);
		
		
		return result;
	}

}
