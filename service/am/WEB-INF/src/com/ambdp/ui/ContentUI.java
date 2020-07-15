package com.ambdp.ui;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月4日
 * @version 
 * 说明:<br />
 * 内容标题UI
 */
public class ContentUI implements UnitInterceptor {


	private static final long serialVersionUID = 5209970540129667561L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		//menucode=menucode_content1
		String menuCode=ac.getRequestParameter("menucode");
		
 		if(!Checker.isEmpty(menuCode)){
			ac.setSessionAttribute("menucode",menuCode);
		}
		
		if(Checker.isEmpty(menuCode)){
			menuCode=ac.getRequestParameter("am_content_menucode");
		}
		
		String title=ac.getRequestParameter("title");
		if(!Checker.isEmpty(title))
		{
			title = ac.getRequestParameter("title");
			ac.setSessionAttribute("content_title",ac.getRequestParameter("title"));
		}else
		{
			title =(String) ac.getSessionAttribute("content_title");
		}
		
//		System.out.println("title:"+ac.getRequestParameter("title"));
//		title=(String)ac.getSessionAttribute("content_title");
//		title = new String(title.getBytes("ISO-8859-1"),"UTF-8");
//		if("menucode_content1".equalsIgnoreCase(menuCode)){
//			title="内容组件一";
//		}
//		if("menucode_content2".equalsIgnoreCase(menuCode)){
//			title="内容组件二";
//		}
//		if("menucode_content2".equalsIgnoreCase(menuCode)){
//			title="内容组件二";
//		}
		
		unit.setTitle(title);
		
		
		return unit.write(ac);
	}

}
