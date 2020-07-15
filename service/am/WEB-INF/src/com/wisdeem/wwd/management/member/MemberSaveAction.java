package com.wisdeem.wwd.management.member;

import com.fastunit.MapList;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.support.action.DefaultAction;

public class MemberSaveAction extends DefaultAction {
	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {

		String orgid = ac.getVisitor().getUser().getOrgId();
		String querySQL = "select * from WS_PUBLIC_ACCOUNTS where orgid='"
				+ orgid + "'";
		MapList listr = db.query(querySQL);
		//account_belong=0为私有公众帐号；account_belong=1为公有公众帐号
		String account_belong = listr.getRow(0).get("account_belong");
		String member_code = ac.getRequestParameter("ws_member1.form.member_code");
		String group_id = ac.getRequestParameter("ws_member1.form.group_id");
		String idCardNo=ac.getRequestParameter("ws_member1.form.idcardno");
		String upSQL="";
		if("0".equals(account_belong)){//私有公众帐号
			upSQL="update WS_MEMBER set group_id="+group_id+",idcardNo='"+idCardNo+"' where member_code="+member_code+"";
			
		}else if("1".equals(account_belong)){//公有公众帐号
			upSQL = "update WS_MBDEATIL set memgroup_id="+group_id+",idcardNo='"+idCardNo+"' where member_code="+member_code+" and care_orgid='"+orgid+"'";
		}
		db.execute(upSQL);
	}
}
