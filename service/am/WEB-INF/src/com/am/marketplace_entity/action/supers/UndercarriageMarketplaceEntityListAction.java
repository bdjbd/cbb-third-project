package com.am.marketplace_entity.action.supers;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;
import com.fastunit.util.Checker;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午2:14:13
 * @version 列表强行下架
 */
public class UndercarriageMarketplaceEntityListAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 强行下架
		String[] list = ac.getRequestParameters("_s_mall_super_marketplace_entity.list");
		String[] ids = ac.getRequestParameters("mall_super_marketplace_entity.list.id.k");
		if(!Checker.isEmpty(list)){
			for(int i = 0; i <list.length; i++ ){
				if(list[i].equals("1")){
					String sql = " UPDATE mall_marketplace_entity SET status = 4 WHERE id = '"+ids[i]+"' ";
					db.execute(sql);
				}
			}
		}
	}

}
