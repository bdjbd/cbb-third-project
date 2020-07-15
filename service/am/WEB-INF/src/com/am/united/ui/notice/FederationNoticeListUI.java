package com.am.united.ui.notice;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月13日
 * @version 
 * 说明:<br />
 * 联合会通知列表UI
 */
public class FederationNoticeListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList data=unit.getData();
		
		if(!Checker.isEmpty(data)){
			for(int i=0;i<data.size();i++){
				Row row=data.getRow(i);
				if("1".equals(row.get("status"))){
					unit.setListSelectAttribute(i, "disabled");
					unit.getElement("edit").setShowMode(i,ElementShowMode.READONLY );
				}
			}
		}
		
		return unit.write(ac);
	}

}
