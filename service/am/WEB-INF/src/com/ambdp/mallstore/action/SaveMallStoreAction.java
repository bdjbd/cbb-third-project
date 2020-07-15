package com.ambdp.mallstore.action;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 作者：yangdong
 *@create 时间：2016年6月19日 下午4:14:12
 *@version 说明：保存门店信息，处理主图路径
 */
public class SaveMallStoreAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		// 保存数据
		Table table = ac.getTable("MALL_STORE");
		db.save(table);
		
		String mallstoreid = table.getRows().get(0).getValue("id");
		
		String mainimages=new FileUtils().getFastUnitFilePathJSON("MALL_STORE", "bdp_mainimgs", mallstoreid);
		String shelfimage=new FileUtils().getFastUnitFilePathJSON("MALL_STORE", "bdp_shelf_image", mallstoreid);
		String list_image =new FileUtils().getFastUnitFilePathJSON("MALL_STORE", "bdp_list_image", mallstoreid);
		String updateSql="UPDATE mall_store  SET mainimgs='"
				+mainimages+"',shelf_image = '"+shelfimage+"',list_image = '"+list_image +"'  WHERE id='"+mallstoreid+"' ";
		db.execute(updateSql);
		
	}
		
}
