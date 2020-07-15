package com.ambdp.mallstore.action;

import com.am.common.util.FileUtils;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.support.action.DefaultAction;

/**
 *@author 作者：yangdong
 *@create 时间：2016年6月20日 下午12:02:32
 *@version 说明：保存店铺图集，处理图集上传
 */
public class SaveImageListAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		
		Table table = ac.getTable("MALL_STOREIMAGELIST");
		
		db.save(table);
		
		//图集id
		String id = table.getRows().get(0).getValue("id");
		
		String imagelist=new FileUtils().getFastUnitFilePathJSON("MALL_STOREIMAGELIST", "bdp_imagelist", id);
		String updateSql="UPDATE mall_storeimagelist  SET imagelist='"
				+imagelist+"'  WHERE id='"+id+"' ";
		db.execute(updateSql);
		
	}
	

}
