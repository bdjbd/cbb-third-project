package com.am.borrowing.ui;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.constants.ElementShowMode;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/** * @author  作者：yangdong
 * @date 创建时间：2016年4月28日 下午7:12:39
 * @version 
 * @parameter  
 * @since  
 * @return
 */
public class BorrowingListUI implements UnitInterceptor {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList unitData=unit.getData();
		if(!Checker.isEmpty(unitData)){
			for(int i=0;i<unitData.size();i++){
				String dataStatus=unitData.getRow(i).get("status");
				
				if(dataStatus.equals("2") || dataStatus.equals("3")){
					unit.getElement("auditing").setShowMode(i,ElementShowMode.CONTROL_DISABLED);
				}
				
			}
		}
		
		
		return unit.write(ac);
	}

}
