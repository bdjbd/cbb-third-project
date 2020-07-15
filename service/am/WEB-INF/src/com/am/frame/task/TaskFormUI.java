package com.am.frame.task;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DBFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class TaskFormUI implements UnitInterceptor {


	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		String entId=ac.getRequestParameter("p2p_enterprisetask.form.id");
		
		String sql="SELECT * FROM  am_EnterpriseTask  WHERE id='"+entId+"'";
		
		MapList map=DBFactory.getDB().query(sql);
		
		if(Checker.isEmpty(map)||"0".equalsIgnoreCase(map.getRow(0).get("etaskstate"))){
			String btn="<span unselectable=\"on\" id=\"p2p_enterprisetask.form.add\" "
					+ "onclick=doAjax(\"/am_bdp/p2p_enterprisetask.form/add.do?type=start&id="+entId+"\"); "
					+ "class=\"E41N\" onmouseover=\"className='E41O'\" "
					+ "onmouseout=\"className='E41N'\" "
					+ "onmousedown=\"className='E41P'\" onmouseup=\"className='E41O'\">"
					+ "<div unselectable=\"on\" class=\"E41T\">启用</div></span>";
			
			unit.getElement("add").setHtml(btn);
		
		}else{
			
			String btn="<span unselectable=\"on\" id=\"p2p_enterprisetask.form.add\" "
					+ "onclick=doAjax(\"/am_bdp/p2p_enterprisetask.form/add.do?type=stop&id="+entId+"\"); "
					+ "class=\"E41N\" onmouseover=\"className='E41O'\" "
					+ "onmouseout=\"className='E41N'\" "
					+ "onmousedown=\"className='E41P'\" onmouseup=\"className='E41O'\">"
					+ "<div unselectable=\"on\" class=\"E41T\">停用</div></span>";
			
			unit.getElement("add").setHtml(btn);
	
		}
		
		return unit.write(ac);
	}

}
