package com.wisdeem.wwd.management.member;

import java.util.ArrayList;
import java.util.List;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Type;
import com.fastunit.support.action.DefaultAction;

public class StartMemberAction extends DefaultAction{

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		//获取当前登陆人id
		String username = ac.getVisitor().getUser().getId();

		// 获得要修改的数据的编号
		String isUpdate[] = ac.getRequestParameters("_s_ws_member1.list");
		String ids[] = ac.getRequestParameters("ws_member1.list.member_code");
		String data_status[] = ac.getRequestParameters("ws_member1.list.data_status");
		String orgid = ac.getVisitor().getUser().getOrgId();
		String querySQL = "select * from WS_PUBLIC_ACCOUNTS where orgid='"
				+ orgid + "'";
		MapList listr = db.query(querySQL);
		String sql = "";
		// account_belong=0为私有公众帐号；account_belong=1为公有公众帐号
		String account_belong = listr.getRow(0).get("account_belong");
		List<String[]> list = new ArrayList<String[]>();
		for (int i = 0; i < isUpdate.length; i++) {
			if("1".equals(isUpdate[i])) {
				if("1".equals(data_status[i])){
					ac.getActionResult().addErrorMessage("已激活的数据不能再激活，请重新选择！");
					return;
				}
				list.add(new String[]{ids[i]});
			}
		}
		if ("0".equals(account_belong)) {// 私有公众帐号
		    //批量更新
			sql = "update WS_MEMBER set data_status=1 where member_code = ? ";
		}else if ("1".equals(account_belong)) {// 公有公众帐号
			sql = "update WS_MBDEATIL set data_status=1 where care_orgid='"+orgid+"' and member_code = ? ";
		}
		db.executeBatch(sql, list,new int[]{Type.INTEGER});
		ac.getActionResult().addSuccessMessage("激活成功");
	}
}
