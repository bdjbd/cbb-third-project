package com.am.cro;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author 张少飞
 *@create 2017年9月14日
 *@version
 *说明：汽车公社-汽修厂后台管理端-预约假期设置 保存 处理action 单页面多行编辑 
 * 验证 假期结束日期 不能小于 假期开始日期
 */
public class Cro_ExceptionDateSettingSaveAction extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		System.err.println("进入保存Action");
		DB db=DBFactory.getDB();
		//保存数据
		Table table=ac.getTable("cro_exceptiondatesetting");
		db.save(table);
		
		//获取全部主键
		String[] Ids=ac.getRequestParameters("cro_exceptiondatesetting.list.id.k");
		//列表有值，才进行判断
		if(!Checker.isEmpty(Ids)){
			for(int i=0;i<Ids.length;i++){
				if(Checker.isEmpty(Ids[i])){
					ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("页面未自动刷新，请手动刷新！");
					return ac;
				}else{
				//检查是否存在结束日期小于开始日期的数据
				String sql="SELECT * FROM cro_exceptiondatesetting WHERE start_date > end_date and id= '"+Ids[i]+"'";
				MapList list=db.query(sql);
				if(!Checker.isEmpty(list)){
					String deleteSql = " delete from cro_exceptiondatesetting where id='"+Ids[i]+"'";
					db.execute(deleteSql);
					ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
					ac.getActionResult().setSuccessful(false);
					ac.getActionResult().addErrorMessage("假期结束日期不能小于假期开始日期！");
					//删除这条违规数据
					return ac;
				
				    }
				}
			}
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
		}else{
			//列表无值
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("页面未自动刷新，请手动刷新！");
			return ac;
		}
		return ac;
	}
}

