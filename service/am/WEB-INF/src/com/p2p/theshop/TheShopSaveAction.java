package com.p2p.theshop;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;
import com.wisdeem.wwd.WeChat.Utils;

public class TheShopSaveAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//super.doAction(db, ac);
		Table tab = ac.getTable("THESHOP");
		db.save(tab);
		
		String shopId=tab.getRows().get(0).getValue("shop_id");
		
		String Pcimg=Utils.getFastUnitFilePath("THESHOP", "bdpshop_pcimag", shopId);
		String mobileimg=Utils.getFastUnitFilePath("THESHOP", "bdpshop_mobileimag", shopId);
		
		String sql = "UPDATE THESHOP SET shop_pcimag='"+Pcimg+"', shop_mobileimag ='"+mobileimg+"'";
		
		db.execute(sql);
	}
}
