package com.am.organization.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

public class ExitOrganizerFormUI implements UnitInterceptor{
	private static final long serialVersionUID = 1L;
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		MapList unitData=unit.getData();
//		for(int i=0;i<unitData.size();i++){
		if(!Checker.isEmpty(unitData))
		{
			String status=unitData.getRow(0).get("status");
			//状态
			//10=待审核  =>审核拒绝，审核通过
			//11=审核拒绝=> 
			//12=通过审核 =>强制踢出
			//20=退出待审核=>
			//21=退出审核拒绝=>
			//22=退出审核通过=>
			//30=强制踢出=>
			
			switch (status) {
			case "10":
				unit.getElement("joining_mechanism_id").setShowMode(ElementShowMode.READONLY);
				unit.getElement("exit_description").setShowMode(ElementShowMode.READONLY);
				unit.getElement("save").setShowMode(ElementShowMode.HIDDEN);
				break;
			case "11":
				unit.getElement("joining_mechanism_id").setShowMode(ElementShowMode.READONLY);
				unit.getElement("exit_description").setShowMode(ElementShowMode.READONLY);
				unit.getElement("save").setShowMode(ElementShowMode.HIDDEN);
				break;
			case "12":
				
				break;
			case "20":
				unit.getElement("joining_mechanism_id").setShowMode(ElementShowMode.READONLY);
				unit.getElement("exit_description").setShowMode(ElementShowMode.READONLY);
				unit.getElement("save").setShowMode(ElementShowMode.HIDDEN);
				break;
			case "21":
				
				break;
			case "22":
				unit.getElement("joining_mechanism_id").setShowMode(ElementShowMode.READONLY);
				unit.getElement("exit_description").setShowMode(ElementShowMode.READONLY);
				unit.getElement("save").setShowMode(ElementShowMode.HIDDEN);
				break;
			case "30":
				
				unit.getElement("save").setShowMode(ElementShowMode.HIDDEN);
				break;

			default:
				break;
			}
		}
//	}
		return unit.write(ac);
	}
}
