package com.am.frame.mallcollegestudentaid.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月19日 下午4:59:15
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class MallCollegeStudentAidListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			for(int i=0;i<unitData.size();i++){
				String dataStatus=unitData.getRow(i).get("status");
				
				if(dataStatus.equals("3") || dataStatus.equals("4")||dataStatus.equals("5")){
					unit.getElement("auditing").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				
			}
		}
		
		
		return unit.write(ac);
	}

}
