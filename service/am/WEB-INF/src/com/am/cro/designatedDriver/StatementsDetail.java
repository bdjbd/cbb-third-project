package com.am.cro.designatedDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
/**
 * 详情结算
 * @author 王成阳
 * 2017-9-8
 */
public class StatementsDetail extends DefaultAction
{
	//输出日志
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public ActionContext execute(ActionContext ac,DB db) throws Exception
	{
		logger.debug("详情结算方法进入");
		//获取页面id
		String id = ac.getRequestParameter("id");
		
		Statements ts=new Statements();
		ts.Update(db, id, ac);
		   
		return ac;
	}
}
