package com.am.cro;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 *@author 张少飞
 *@create 2017/9/15
 *@version
 *说明：汽车公社-汽修厂后台管理端-预约假期设置 UI
 */
public class Cro_exceptionDateSettingListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		DB db=DBFactory.newDB();
		MapList unitData=unit.getData();
		for(int i=0;i<unitData.size();i++){
			//用列表各id分别在数据库中查询，该条信息是否已被实际删除，若已实际删除，则在当前列表中，把该条信息隐藏掉
			String id = unitData.getRow(i).get("id");
			System.err.println("进入UI控制，获得列表id:"+id);
			String checkSql = "select * from cro_exceptiondatesetting where id = '"+id+"'";
			System.err.println("执行UI控制Sql:"+checkSql);
			MapList map=db.query(checkSql);
			//若该条信息实际不存在，则在页面上屏蔽该条信息的缓存
			if(Checker.isEmpty(map)){
				System.err.println("注意:id"+id+"的数据已被实际删除，准备隐藏该信息");
				unit.getElement("start_date").setShowMode(i, ElementShowMode.REMOVE);
				System.err.println("id"+id+"的start_date已被隐藏");
				unit.getElement("end_date").setShowMode(i, ElementShowMode.REMOVE);
				System.err.println("id"+id+"的end_date已被隐藏");
				unit.getElement("orgcode").setShowMode(i, ElementShowMode.REMOVE);
				System.err.println("id"+id+"的orgcode已被隐藏");
				unit.getElement("id").setShowMode(i, ElementShowMode.REMOVE);
				System.err.println("id"+id+"的id已被隐藏");
				unit.removeElement(i);
				System.err.println("第"+i+"行已被整体隐藏");
				unit.getElement("id").setShowMode(i,ElementShowMode.REMOVE);
			}
//				unit.getElement("operation").setDefaultValue(i,"停用");
//				//设定当前行不能被选中 (这一条有争议 视情况而定吧)
//				unit.setListSelectAttribute(i, "disabled");  
//				//设定当前行不能修改
				
			}
		//重新加载当前页面
		//ac.getActionResult().setUrl("/am_bdp/cro_exceptiondatesetting.do");
		return unit.write(ac);
	}

}
