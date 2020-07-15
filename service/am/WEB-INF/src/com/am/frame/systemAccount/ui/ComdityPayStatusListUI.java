package com.am.frame.systemAccount.ui;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.util.Checker;

/**
 * @author YueBin
 * @create 2016年5月10日
 * @version 
 * 说明:<br />
 * 商品支付类型类别UI
 */
public class ComdityPayStatusListUI implements UnitInterceptor {

	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		
		MapList dataMap=unit.getData();
		
		if(!Checker.isEmpty(dataMap)){
			for(int i=0;i<dataMap.size();i++){
				Row row=dataMap.getRow(i);
				if("1".equals(row.get("ps_status"))){//0 停用；1 启用
					unit.getElement("operation").setName(i,"停用");
				}else{
					unit.getElement("operation").setName(i,"启用");
				}
			}
		}
		
		return unit.write(ac);
	}

}
