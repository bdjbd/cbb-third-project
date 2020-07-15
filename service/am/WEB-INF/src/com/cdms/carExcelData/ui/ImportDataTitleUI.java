package com.cdms.carExcelData.ui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdms.carExcelData.ui.ImportDataTitleUI;
import com.fastunit.Page;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class ImportDataTitleUI implements UnitInterceptor{
	private static final String PropertytelTitleUI_SESSION_TITLE="PropertytelTitleUI_SESSION_TITLE";
    Logger log = LoggerFactory.getLogger(ImportDataTitleUI.class);
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String title=ac.getRequestParameter("data_import.title");
//		ac.setAttribute("title1", title);
//		System.err.println(title);
//		String title2=(String) ac.getAttribute("title1");
		
		if(Checker.isEmpty(title)||"null".equals(title)){
			log.debug("判定title为空");
			title=(String)ac.getSessionAttribute(PropertytelTitleUI_SESSION_TITLE);
		}
		
		if(!Checker.isEmpty(title)&&!"null".equals(title)){
			log.debug("判定title不为空");
			ac.setSessionAttribute(PropertytelTitleUI_SESSION_TITLE,title);
		}
		
		
		unit.setTitle(title);
		
		return unit.write(ac);
	}
}
