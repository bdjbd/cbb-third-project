
package com.am.cro;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/**
 *@author 张少飞
 *@create 2017/7/14
 *@version
 *说明：汽车公社订单结算Action
 */
public class Cro_ExceptionDateSettingFormAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//先保存表单信息
		Table table = ac.getTable("cro_exceptiondatesetting");
		db.save(table);
		TableRow tr = table.getRows().get(0);
		//主键ID
		String id=tr.getValue("id");
		System.err.println("id:"+id);
		String id1 = ac.getRequestParameter("cro_exceptiondatesetting.form.id");
		System.err.println("id:"+id);
		String start_date = ac.getRequestParameter("cro_exceptiondatesetting.form.start_date");
		String end_date = ac.getRequestParameter("cro_exceptiondatesetting.form.end_date");
		String checkSql = "SELECT * FROM cro_exceptiondatesetting WHERE '"+start_date+"' > '"+end_date+"' and id='"+id+"' ";
		MapList map = db.query(checkSql);
		if(!Checker.isEmpty(map)){
			//删除违规数据 提示错误信息
			String delete = " delete from cro_exceptiondatesetting where id='"+id+"'";
			db.execute(delete);
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("假期结束日期不能小于假期开始日期！");
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.form.do?m=e");
			return;
		}else if(Checker.isEmpty(map)){
			System.err.println("正常保存");
			ac.getActionResult().setSuccessful(true);
			ac.getActionResult().addSuccessMessage("保存成功！");
			//ac.getSessionAttribute("am_bdp.cro_exceptiondatesetting.form.id", id);
			
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
		}
	}
	
	
}
