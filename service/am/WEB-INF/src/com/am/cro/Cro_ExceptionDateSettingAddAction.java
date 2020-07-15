package com.am.cro;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 张少飞
 *@create 2017年9月14日
 *@version
 *说明：汽车公社-汽修厂后台管理端-预约假期设置 新增action 先保存 再新增 
 * 移除时进行保存操作
 */
public class Cro_ExceptionDateSettingAddAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		System.err.println("进入新增Action");
		DB db=DBFactory.getDB();
		//保存数据
		Table table=ac.getTable("cro_exceptiondatesetting");
		db.save(table);
		System.err.println("保存数据");
		//刷新页面
		ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.list.do");
		System.err.println("刷新页面");
		return ac;
	}
}

