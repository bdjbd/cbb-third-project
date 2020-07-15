package com.wisdeem.wwd.news;

import com.fastunit.Invocation;
import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.ActionInterceptor;
import com.fastunit.util.Checker;

public class NewsCategorySaveAI implements ActionInterceptor {

	@Override
	public void intercept(ActionContext ac, Invocation invocation) throws Exception {
		DB db=DBFactory.getDB();
		String querSQL="SELECT orgid FROM newscategory WHERE orgid='"+ac.getVisitor().getUser().getOrgId()+"' ";
		MapList map=db.query(querSQL);
		if(!Checker.isEmpty(map)&&map.size()>=5){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("企业信息分类最多有5个可以生效");
			return ;
		}else{
			invocation.invoke();
		}
		
	}

}
