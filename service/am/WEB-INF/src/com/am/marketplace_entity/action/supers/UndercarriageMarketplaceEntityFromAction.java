package com.am.marketplace_entity.action.supers;

import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

/** 
 * @author  作者：yangdong
 * @date 创建时间：2016年4月26日 下午2:00:50
 * @version 强行下架
 */
public class UndercarriageMarketplaceEntityFromAction extends DefaultAction {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 强制下架
		String id = ac.getRequestParameter("mall_super_marketplace_entity.form.id");
		String review_remarks = ac.getRequestParameter("mall_super_marketplace_entity.form.review_remarks");
		String sql = " UPDATE mall_marketplace_entity SET status = 4,review_remarks = '"+review_remarks+"' WHERE id = '"+id+"' ";
		db.execute(sql);
	}
		
}
