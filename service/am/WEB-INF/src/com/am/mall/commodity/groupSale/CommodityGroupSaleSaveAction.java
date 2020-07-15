package com.am.mall.commodity.groupSale;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 * @author YueBin
 * @create 2014年11月10日
 * @version 
 * 说明:<br />
 * 商品套餐设置保存Action
 */
public class CommodityGroupSaleSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//super.doAction(db, ac);
		
		Table tableSale=ac.getTable("mall_commoditygroupsale");
		
		Table tableSaleSet=ac.getTable("mall_commoditygroupssaleset");
		
		db.save(tableSale);
		
		String id=tableSale.getRows().get(0).getValue("id");
		
		for(int i=0;i<tableSaleSet.getRows().size();i++){
			tableSaleSet.getRows().get(i).setValue("commoditygroupsaleid",id);
		}
		
		db.save(tableSaleSet);
		
	}
	
}
