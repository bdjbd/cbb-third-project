package com.am.frame.memberlogout.ui;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

/**
 * @author YueBin
 * @create 2016年6月17日
 * @version 
 * 说明:<br />
 */
public class MemberLogoutApplyListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList data=unit.getData();
		
		
		for(int i=0;i<data.size();i++){
			//scan
			Row row=data.getRow(i);
			String status=row.get("status");
			if(!"1".equals(status)){
				unit.getElement("scan").setLink(i,"/am_bdp/am_member_logout.form.do?m=s"
						+"&am_member_form.id="+row.get("memberid")
						+"&am_member_logoiut.id="+row.get("id"));
			}else{
				unit.getElement("scan").setLink(i,"/am_bdp/am_member_logout.form.do?m=e"
						+"&am_member_form.id="+row.get("memberid")
						+"&am_member_logoiut.id="+row.get("id"));
			}
		}
		
		
		
		return unit.write(ac);
	}

}
