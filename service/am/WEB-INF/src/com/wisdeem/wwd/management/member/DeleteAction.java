package com.wisdeem.wwd.management.member;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class DeleteAction extends DefaultAction{
	
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		String isUpdate[] = ac.getRequestParameters("_s_ws_member_group.list");
		String ids[] = ac.getRequestParameters("ws_member_group.list.mb_g_id");
		String id ="";
		String selSQL ="";
		String orgid = ac.getVisitor().getUser().getOrgId();
		String querySQL = "select * from WS_PUBLIC_ACCOUNTS where orgid='"
				+ orgid + "'";
		MapList listr = db.query(querySQL);
		// account_belong=0为私有公众帐号；account_belong=1为公有公众帐号
		String account_belong = listr.getRow(0).get("account_belong");
		for (int i = 0; i < isUpdate.length; i++) {
			//选中状态
			if("1".equals(isUpdate[i])){
			    id = ids[i];
			    if ("0".equals(account_belong)) {// 私有公众帐号
			    	selSQL = "select group_id from ws_member where group_id="+id+"";
			    }else if ("1".equals(account_belong)) {// 公有公众帐号
			    	selSQL = "select memgroup_id from ws_mbdeatil where memgroup_id="+id+"";
			    }
				
				MapList list = db.query(selSQL);
				if(list.size()>0){
					ac.getActionResult().addErrorMessage("该分组已经被引用，不能删除！");
					return;
				}
				String delSQL = "delete from ws_member_group where mb_g_id = "+id+" ";
				db.execute(delSQL);
			}
		}
		ac.getActionResult().addSuccessMessage("删除成功");
	}
}
