package com.am.mall_logistics_info.action;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年5月19日 下午4:37:55
 * @explain 说明 : 
 */
public class SaveLogisticsInfo extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		Table table = ac.getTable("MALL_LOGISTICS_INFO");
		db.save(table);
		
		String id = table.getRows().get(0).getValue("id");
		
		String sql = " UPDATE mall_logistics_info SET freight = freight * 100,deposit = deposit * 100 WHERE id = ? ";
		db.execute(sql,id,Type.VARCHAR);
	}

}
