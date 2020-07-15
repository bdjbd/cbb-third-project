package com.am.organization.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class OrgainizationListUI implements UnitInterceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = -700233827828438055L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
//		for(int i=0;i<unitData.size();i++){
		if(!Checker.isEmpty(unitData))
		{
			for (int i = 0; i < unitData.size(); i++) 
			{
				String status=unitData.getRow(i).get("status");
				if("30".equals(status))
				{
					unit.getElement("edit").setTitle(i, "查看");
					unit.getElement("edit").setDefaultValue(i, "查看");
					unit.getElement("edit").setLink(i,"/am_bdp/lxny_organizational_relationshi_join.form.do?m=s&lxny_organizational_relationshi_join.form.id=$D{id}");
				}
				if("10".equals(status))
				{
					unit.getElement("edit").setTitle(i, "查看");
					unit.getElement("edit").setDefaultValue(i, "查看");
					unit.getElement("edit").setLink(i,"/am_bdp/lxny_organizational_relationshi_join.form.do?m=s&lxny_organizational_relationshi_join.form.id=$D{id}");
				}
				if("11".equals(status))
				{
					unit.getElement("edit").setTitle(i, "查看");
					unit.getElement("edit").setDefaultValue(i, "查看");
					unit.getElement("edit").setLink(i,"/am_bdp/lxny_organizational_relationshi_join.form.do?m=s&lxny_organizational_relationshi_join.form.id=$D{id}");
				}
				
			}
			
		}
	
		return unit.write(ac);
	}

}
