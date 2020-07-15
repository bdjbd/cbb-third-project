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
public class Cro_ExceptionDateSettingSaveAction_1 extends DefaultAction {

	@Override
	public ActionContext execute(ActionContext ac) throws Exception {
		System.err.println("进入保存Action_1");
		DB db=DBFactory.getDB();
		//保存数据
		Table table=ac.getTable("cro_exceptiondatesetting");
		db.save(table);
		
		String orgid = ac.getVisitor().getUser().getOrgId();
		String listSql = "select * from cro_exceptiondatesetting where orgcode = '"+orgid+"'";
		System.err.println("查询列表："+listSql);
		MapList map = db.query(listSql);
		//列表无数据
		if(Checker.isEmpty(map)){
			ac.getActionResult().setSuccessful(false);
			ac.getActionResult().addErrorMessage("请录入数据！");
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.list.do?m=s");
			return ac;
		}else{
		//列表有数据
			boolean flag = true;
			for(int i=0;i<map.size();i++){
				//检查每一条数据
				String id = map.getRow(i).get("id");
				String checkSql = "SELECT * FROM cro_exceptiondatesetting WHERE start_date > end_date and id= '"+id+"'";
				MapList mapI = db.query(checkSql);
				if(!Checker.isEmpty(mapI)){
					flag = false;
					//for循环，删除全部违规数据
					String deleteI = " delete from cro_exceptiondatesetting where id='"+id+"'";
					db.execute(deleteI);
				}
				
			}
			//循环删除数据库中所有不合格数据，然而怎样刷新当前页面呢？
			ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.list.do?m=s");
			//刷新之后再提示！
			if(flag == false){
				ac.getActionResult().setSuccessful(false);
				ac.getActionResult().addErrorMessage("假期结束日期不能小于假期开始日期！");
				return ac;
			}else if(flag == true){
				//列表有数据且遍历之后全部合格
				ac.getActionResult().setSuccessful(true);
				ac.getActionResult().addSuccessMessage("保存成功！");
				ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.list.do?m=e");
				
			}
			
		}
		
		return ac;
	}
}

