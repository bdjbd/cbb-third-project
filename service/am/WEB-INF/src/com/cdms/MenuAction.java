package com.cdms;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.am.frame.webapi.db.DBManager;
import com.p2p.service.IWebApiService;

public class MenuAction implements IWebApiService {
Logger logger = LoggerFactory.getLogger(getClass());
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String title=request.getParameter("title");
		DBManager db=new DBManager();
		String regexp = "\'";
		title = title.replaceAll(regexp, "");
		String s="select id from cdms_menu_click where menu_name='"+title+"'";
		logger.debug(s);
		boolean b=db.queryToJSON(s).length()!=0;
		String sql="";
		UUID uuid=UUID.randomUUID();
		if (b) {
			sql+="update cdms_menu_click set click_num=click_num+1 where menu_name='"+title+"'";
		}else{
			sql+="insert into cdms_menu_click values('"+uuid+"','"+title+"',1)";
		}
		db.execute(sql);
		return null;
	}

}
